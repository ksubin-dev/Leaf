package com.subin.leafy.data.datasource.remote

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.Notification
import com.subin.leafy.domain.model.TeaItem
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface UserDataSource {
    // --- 기존 기능 (그대로 유지) ---
    suspend fun getCurrentUserId(): String?
    suspend fun getUser(userId: String): DataResourceResult<User>
    fun getUserFlow(userId: String): Flow<DataResourceResult<User>>
    suspend fun getUserStats(userId: String): DataResourceResult<UserStats>
    suspend fun updateUser(user: User): DataResourceResult<Unit>

    // --- 팔로우 관련 ---
    suspend fun followUser(myId: String, targetUserId: String): DataResourceResult<Unit>
    suspend fun unfollowUser(myId: String, targetUserId: String): DataResourceResult<Unit>
    suspend fun checkFollowStatus(myId: String, targetUserId: String): Boolean
    fun isFollowingFlow(myId: String, targetUserId: String): Flow<Boolean>
    suspend fun fetchTopUsers(limit: Int): DataResourceResult<List<User>>

    suspend fun getFollowUsers(userIds: List<String>): DataResourceResult<List<User>>

    // --- 알림 (Notification) ---
    fun getNotificationsFlow(userId: String): Flow<DataResourceResult<List<Notification>>>
    suspend fun markNotificationAsRead(notificationId: String): DataResourceResult<Unit>

    // --- 내 차 보관함 (Tea Chest) ---
    fun getTeaChestFlow(userId: String): Flow<DataResourceResult<List<TeaItem>>>
    suspend fun addTeaToChest(teaItem: TeaItem): DataResourceResult<Unit>

    // --- 계정 관리 ---
    suspend fun withdrawAccount(userId: String): DataResourceResult<Unit>

    suspend fun searchUsers(query: String): DataResourceResult<List<User>>
}