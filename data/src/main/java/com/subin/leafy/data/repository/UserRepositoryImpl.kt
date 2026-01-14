package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.remote.AuthDataSource
import com.subin.leafy.data.datasource.remote.UserDataSource
import com.subin.leafy.data.util.BadgeLibrary
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserBadge
import com.subin.leafy.domain.model.UserRelationState
import com.subin.leafy.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource
) : UserRepository {

    // 1. 내 프로필 Flow
    override fun getMyProfileFlow(): Flow<DataResourceResult<User>> {
        val myUid = authDataSource.getCurrentUserId()

        return if (myUid != null) {
            userDataSource.getUserFlow(myUid)
        } else {
            // 로그인 안 된 상태면 실패 혹은 빈 값 처리
            flow { emit(DataResourceResult.Failure(Exception("Not logged in"))) }
        }
    }

    // 2. 상대방 프로필 조회 (팔로우 여부 계산 로직 포함)
    override suspend fun getUserProfile(targetUserId: String): DataResourceResult<User> {
        // (1) 상대방 정보 가져오기
        val targetUserResult = userDataSource.getUser(targetUserId)
        if (targetUserResult is DataResourceResult.Failure) return targetUserResult

        val targetUser = (targetUserResult as DataResourceResult.Success).data
        val myUid = authDataSource.getCurrentUserId()

        // (2) 내가 이 사람을 팔로우 중인지 확인
        // 만약 '내 정보'가 있다면 followingIds를 확인, 없으면(비로그인) false
        var isFollowing = false

        if (myUid != null && myUid != targetUserId) {
            val myProfileResult = userDataSource.getUser(myUid)
            if (myProfileResult is DataResourceResult.Success) {
                isFollowing = myProfileResult.data.followingIds.contains(targetUserId)
            }
        }

        // (3) 상태 업데이트 후 반환
        return DataResourceResult.Success(
            targetUser.copy(
                relationState = UserRelationState(isFollowing = isFollowing)
            )
        )
    }

    // 3. 프로필 수정
    override suspend fun updateProfile(
        nickname: String?,
        bio: String?,
        profileUrl: String?
    ): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("Not logged in"))

        // 현재 내 정보 가져오기 (기존 값 유지 위함)
        val myProfileResult = userDataSource.getUser(myUid)
        if (myProfileResult !is DataResourceResult.Success) {
            return DataResourceResult.Failure(Exception("Failed to load profile"))
        }

        val currentUser = myProfileResult.data

        // 변경된 필드만 적용 (null이면 기존 값 유지)
        val updatedUser = currentUser.copy(
            nickname = nickname ?: currentUser.nickname,
            bio = bio ?: currentUser.bio,
            profileImageUrl = profileUrl ?: currentUser.profileImageUrl
        )

        return userDataSource.updateUser(updatedUser)
    }

    // 4. 소셜 기능
    override suspend fun followUser(targetUserId: String): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("Not logged in"))

        return userDataSource.followUser(myUid, targetUserId)
    }

    override suspend fun unfollowUser(targetUserId: String): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("Not logged in"))

        return userDataSource.unfollowUser(myUid, targetUserId)
    }

    override suspend fun getFollowers(userId: String): DataResourceResult<List<User>> {
        return userDataSource.getFollowers(userId)
    }

    override suspend fun getFollowings(userId: String): DataResourceResult<List<User>> {
        // 내 followingIds 리스트를 가져와서 -> getUserByIds로 상세 정보 조회
        val userResult = userDataSource.getUser(userId)

        return if (userResult is DataResourceResult.Success) {
            val followingIds = userResult.data.followingIds
            if (followingIds.isNotEmpty()) {
                userDataSource.getUsersByIds(followingIds)
            } else {
                DataResourceResult.Success(emptyList())
            }
        } else {
            DataResourceResult.Failure(Exception("Failed to fetch user"))
        }
    }

    override fun getFollowingIdsFlow(userId: String): Flow<DataResourceResult<List<String>>> {
        return userDataSource.getFollowingIdsFlow(userId)
    }

    // 5. 뱃지 가져오기 (Library 병합 로직)
    override suspend fun getUserBadges(userId: String): DataResourceResult<List<UserBadge>> {
        val remoteBadgesResult = userDataSource.getUserBadges(userId)

        if (remoteBadgesResult is DataResourceResult.Success) {
            val remoteBadges = remoteBadgesResult.data

            // (2) BadgeLibrary(정적 데이터)와 병합
            val combinedBadges = remoteBadges.mapNotNull { remoteBadge ->
                // ID로 라이브러리에서 찾기
                val libraryInfo = BadgeLibrary.findById(remoteBadge.id)

                if (libraryInfo != null) {
                    // DB의 획득 날짜 + 라이브러리의 이미지/설명 합체!
                    remoteBadge.copy(
                        name = libraryInfo.title,
                        description = libraryInfo.description,
                        imageUrl = libraryInfo.imageUrl
                    )
                } else {
                    // 라이브러리에 없는 뱃지면(삭제됨?) 제외하거나 그대로 표시
                    null
                }
            }
            return DataResourceResult.Success(combinedBadges)
        }

        return remoteBadgesResult
    }

    // 6. 검색 & 중복 체크
    override suspend fun searchUsers(query: String): DataResourceResult<List<User>> {
        return userDataSource.searchUsers(query)
    }

    override suspend fun checkNicknameAvailability(nickname: String): DataResourceResult<Boolean> {
        return try {
            val isDuplicate = userDataSource.isNicknameDuplicate(nickname)
            DataResourceResult.Success(!isDuplicate)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }
}