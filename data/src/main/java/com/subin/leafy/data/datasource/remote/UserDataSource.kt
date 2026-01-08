package com.subin.leafy.data.datasource.remote

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserDataSource {

    // --- 유저 정보 관련 ---
    suspend fun getUser(userId: String): DataResourceResult<User>
    fun getUserFlow(userId: String): Flow<DataResourceResult<User>>
    suspend fun updateUser(user: User): DataResourceResult<Unit>
    suspend fun searchUsers(query: String): DataResourceResult<List<User>>

    // --- 팔로우 액션 (Action) ---
    suspend fun followUser(myId: String, targetUserId: String): DataResourceResult<Unit>
    suspend fun unfollowUser(myId: String, targetUserId: String): DataResourceResult<Unit>
    fun isFollowingFlow(myId: String, targetUserId: String): Flow<Boolean>
    // 1. 내 팔로잉 목록 보기: ID 리스트를 주면 유저 정보 리스트로 변환
    suspend fun getUsersByIds(userIds: List<String>): DataResourceResult<List<User>>
    // 2. 내 팔로워 목록 보기: 나를 팔로우하는 사람 검색
    suspend fun getFollowers(myUserId: String): DataResourceResult<List<User>>
}