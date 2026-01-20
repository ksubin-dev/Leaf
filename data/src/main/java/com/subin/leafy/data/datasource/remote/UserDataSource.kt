package com.subin.leafy.data.datasource.remote

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserBadge
import kotlinx.coroutines.flow.Flow

interface UserDataSource {

    suspend fun getUser(userId: String): DataResourceResult<User>
    fun getUserFlow(userId: String): Flow<DataResourceResult<User>>
    suspend fun updateUser(user: User): DataResourceResult<Unit>
    suspend fun searchUsers(
        query: String,
        lastUserId: String? = null,
        limit: Int = 20
    ): DataResourceResult<List<User>>
    suspend fun isNicknameDuplicate(nickname: String): Boolean

    suspend fun followUser(myId: String, targetUserId: String): DataResourceResult<Unit>
    suspend fun unfollowUser(myId: String, targetUserId: String): DataResourceResult<Unit>
    fun isFollowingFlow(myId: String, targetUserId: String): Flow<Boolean>
    suspend fun getUsersByIds(userIds: List<String>): DataResourceResult<List<User>>
    suspend fun getFollowers(myUserId: String): DataResourceResult<List<User>>
    suspend fun getFollowingIds(userId: String): DataResourceResult<List<String>>
    fun getFollowingIdsFlow(userId: String): Flow<DataResourceResult<List<String>>>
    suspend fun getUserBadges(userId: String): DataResourceResult<List<UserBadge>>

    suspend fun saveUserBadge(userId: String, badge: UserBadge): DataResourceResult<Unit>

    suspend fun toggleLikePost(userId: String, postId: String, isAdding: Boolean): DataResourceResult<Unit>

    suspend fun toggleBookmarkPost(userId: String, postId: String, isAdding: Boolean): DataResourceResult<Unit>

    suspend fun updateNotificationSetting(userId: String, isAgreed: Boolean): DataResourceResult<Unit>

    suspend fun deleteUser(userId: String): DataResourceResult<Unit>

}