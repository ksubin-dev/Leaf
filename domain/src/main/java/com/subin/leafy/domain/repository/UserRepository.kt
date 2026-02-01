package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserBadge
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getMyProfileFlow(): Flow<DataResourceResult<User>>

    suspend fun getUserProfile(targetUserId: String): DataResourceResult<User>

    suspend fun updateProfile(
        nickname: String? = null,
        bio: String? = null,
        profileUrl: String? = null
    ): DataResourceResult<Unit>

    suspend fun followUser(targetUserId: String): DataResourceResult<Unit>
    suspend fun unfollowUser(targetUserId: String): DataResourceResult<Unit>

    suspend fun getFollowers(userId: String): DataResourceResult<List<User>>
    suspend fun getFollowings(userId: String): DataResourceResult<List<User>>


    fun getFollowingIdsFlow(userId: String): Flow<DataResourceResult<List<String>>>

    // 팔로워 숫자도
    // fun getFollowerIdsFlow(userId: String): Flow<DataResourceResult<List<String>>>


    suspend fun getUserBadges(userId: String): DataResourceResult<List<UserBadge>>


    suspend fun searchUsers(
        query: String,
        lastUserId: String? = null,
        limit: Int = 20
    ): DataResourceResult<List<User>>

    suspend fun syncFcmToken(isEnabled: Boolean): DataResourceResult<Unit>

    suspend fun checkNicknameAvailability(nickname: String): DataResourceResult<Boolean>

    suspend fun scheduleProfileUpdate(nickname: String, bio: String, imageUriString: String?)
}