package com.subin.leafy.data.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.subin.leafy.data.datasource.local.LocalSettingDataSource
import com.subin.leafy.data.datasource.remote.AuthDataSource
import com.subin.leafy.data.datasource.remote.StorageDataSource
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
    private val storageDataSource: StorageDataSource,
    private val settingDataSource: LocalSettingDataSource
) : AuthRepository {

    override fun isUserLoggedIn(): Boolean = authDataSource.isUserLoggedIn()

    override fun getCurrentUserId(): String? = authDataSource.getCurrentUserId()

    override suspend fun login(email: String, password: String): DataResourceResult<User> {

        val authResult = authDataSource.login(email, password)

        if (authResult is DataResourceResult.Failure) {
            val originalException = authResult.exception

            val friendlyMessage = when (originalException) {
                is FirebaseAuthInvalidUserException -> "존재하지 않는 계정입니다. 회원가입을 먼저 진행해주세요."
                is FirebaseAuthInvalidCredentialsException -> "이메일 또는 비밀번호가 올바르지 않습니다."
                is FirebaseNetworkException -> "네트워크 연결 상태를 확인해주세요."
                else -> "로그인에 실패했습니다. 다시 시도해주세요."
            }
            return DataResourceResult.Failure(Exception(friendlyMessage))
        }

        val uid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("UID Fetch Error"))

        return userDataSource.getUser(uid)
    }

    override suspend fun signUp(email: String, password: String, nickname: String, profileImageUri: String?): DataResourceResult<User> {

        val isDuplicate = userDataSource.isNicknameDuplicate(nickname)
        if (isDuplicate) {
            return DataResourceResult.Failure(Exception("이미 사용 중인 닉네임입니다."))
        }

        val authResult = authDataSource.signUp(email, password)

        if (authResult is DataResourceResult.Failure) {
            val friendlyMessage = when (val originalException = authResult.exception) {
                is FirebaseAuthUserCollisionException -> "이미 가입된 이메일 주소입니다."
                is FirebaseAuthInvalidCredentialsException -> "이메일 형식이 올바르지 않습니다."
                is FirebaseNetworkException -> "네트워크 연결 상태를 확인해주세요."
                else -> "회원가입에 실패했습니다. (${originalException.message})"
            }
            return DataResourceResult.Failure(Exception(friendlyMessage))
        }

        val uid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("UID Fetch Error"))

        var uploadedImageUrl: String? = null
        if (profileImageUri != null) {
            val imageResult = storageDataSource.uploadImage(
                uriString = profileImageUri,
                folderPath = "profile_images/$uid"
            )
            if (imageResult is DataResourceResult.Success) {
                uploadedImageUrl = imageResult.data
            }
        }

        val newUser = createNewUser(uid, email, nickname).copy(
            profileImageUrl = uploadedImageUrl
        )

        val saveResult = userDataSource.updateUser(newUser)

        return if (saveResult is DataResourceResult.Success) {
            DataResourceResult.Success(newUser)
        } else {
            try {
                authDataSource.deleteAuthAccount()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            DataResourceResult.Failure(Exception("회원 정보 저장에 실패했습니다. 잠시 후 다시 시도해주세요."))
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

    override suspend fun signOut(): DataResourceResult<Unit> {
        return try {
            authDataSource.logout()
            settingDataSource.clearSettings()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun deleteAccount(): DataResourceResult<Unit> {
        return try {
            val uid = getCurrentUserId()
                ?: return DataResourceResult.Failure(Exception("Not logged in"))

            val dbResult = userDataSource.deleteUser(uid)

            if (dbResult is DataResourceResult.Failure) {
                return DataResourceResult.Failure(dbResult.exception)
            }
            val authResult = authDataSource.deleteAuthAccount()
            if (authResult is DataResourceResult.Failure) return authResult

            settingDataSource.clearSettings()

            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override fun getAutoLoginState(): Flow<Boolean> {
        return settingDataSource.getAppSettingsFlow().map { it.autoLogin }
    }

    override suspend fun setAutoLoginState(enabled: Boolean) {
        settingDataSource.updateAutoLogin(enabled)
    }

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