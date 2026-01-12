package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.local.LocalSettingDataSource
import com.subin.leafy.data.datasource.remote.AuthDataSource
import com.subin.leafy.data.datasource.remote.UserDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserRelationState
import com.subin.leafy.domain.model.UserSocialStatistics
import com.subin.leafy.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource,
    private val settingDataSource: LocalSettingDataSource
) : AuthRepository {

    override fun isUserLoggedIn(): Boolean = authDataSource.isUserLoggedIn()

    override fun getCurrentUserId(): String? = authDataSource.getCurrentUserId()

    // =================================================================
    // 1. 로그인 (Login)
    // =================================================================
    override suspend fun login(email: String, password: String): DataResourceResult<User> {
        val authResult = authDataSource.login(email, password)
        if (authResult is DataResourceResult.Failure) {
            return DataResourceResult.Failure(authResult.exception)
        }

        // 2. 성공 시 UID 획득
        val uid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("UID Fetch Error"))

        // 3. Firestore에서 유저 정보(User 도메인 객체) 가져오기
        return userDataSource.getUser(uid)
    }

    // =================================================================
    // 2. 회원가입 (Sign Up)
    // =================================================================
    override suspend fun signUp(email: String, password: String, nickname: String): DataResourceResult<User> {

        // 1. 닉네임 중복 검사 (DB 먼저 확인)
        val isDuplicate = userDataSource.isNicknameDuplicate(nickname)
        if (isDuplicate) {
            return DataResourceResult.Failure(Exception("이미 사용 중인 닉네임입니다."))
        }

        // 2. 중복 아니면 Firebase Auth 계정 생성 진행
        val authResult = authDataSource.signUp(email, password)
        if (authResult is DataResourceResult.Failure) {
            return DataResourceResult.Failure(authResult.exception)
        }

        // 3. 성공 시 UID 획득
        val uid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("UID Fetch Error"))

        // 4. 초기 User 객체 생성
        val newUser = createNewUser(uid, email, nickname)

        // 5. Firestore에 저장
        val saveResult = userDataSource.updateUser(newUser)

        return if (saveResult is DataResourceResult.Success) {
            DataResourceResult.Success(newUser)
        } else {
            // DB 저장이 실패했다면... (계정은 만들어졌는데 DB가 없는 꼬인 상태)
            // 실제 앱에선 여기서 '계정 삭제'를 하거나, 재시도 로직이 필요할 수 있음
            DataResourceResult.Failure(Exception("Failed to save user profile"))
        }
    }

    override suspend fun checkNicknameAvailability(nickname: String): DataResourceResult<Boolean> {
        return try {
            val isDuplicate = userDataSource.isNicknameDuplicate(nickname)
            DataResourceResult.Success(!isDuplicate)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    // =================================================================
    // 3. 로그아웃 & 탈퇴
    // =================================================================
    override suspend fun signOut(): DataResourceResult<Unit> {
        return try {
            authDataSource.logout()
            settingDataSource.clearSettings() // 로컬 설정 초기화
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun deleteAccount(): DataResourceResult<Unit> {
        return try {
            val uid = getCurrentUserId()
                ?: return DataResourceResult.Failure(Exception("Not logged in"))

            // 1. Firestore 데이터 삭제 (UserDataSource에 deleteUser가 필요하거나, 여기서 로직 처리)
            // (만약 UserDataSource에 deleteUser를 아직 안 만드셨다면, 일단 주석 처리하거나 추가 필요)
            // userDataSource.deleteUser(uid)

            // 2. Auth 계정 삭제
            val authResult = authDataSource.deleteAuthAccount()
            if (authResult is DataResourceResult.Failure) return authResult

            // 3. 설정 초기화
            settingDataSource.clearSettings()

            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    // =================================================================
    // 4. 설정 (Settings)
    // =================================================================
    override fun getAutoLoginState(): Flow<Boolean> {
        return settingDataSource.getAppSettingsFlow().map { it.autoLogin }
    }

    override suspend fun setAutoLoginState(enabled: Boolean) {
        settingDataSource.updateAutoLogin(enabled)
    }

    // --- Helper: 신규 유저 초기값 생성 ---
    private fun createNewUser(uid: String, email: String, nickname: String): User {
        return User(
            id = uid,
            nickname = nickname,
            profileImageUrl = null,
            bio = "안녕하세요! 차를 즐기는 티 러버입니다.",
            masterTitle = null,
            socialStats = UserSocialStatistics(0, 0),
            relationState = UserRelationState(isFollowing = false),
            followingIds = emptyList(),
            likedPostIds = emptyList(),
            bookmarkedPostIds = emptyList(),
            createdAt = System.currentTimeMillis()
        )
    }
}