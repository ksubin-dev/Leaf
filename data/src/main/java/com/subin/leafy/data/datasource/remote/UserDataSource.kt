package com.subin.leafy.data.datasource.remote

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserBadge
import kotlinx.coroutines.flow.Flow

interface UserDataSource {

    // --- 1. 유저 정보 기본 (CRUD) ---
    suspend fun getUser(userId: String): DataResourceResult<User>
    fun getUserFlow(userId: String): Flow<DataResourceResult<User>>
    suspend fun updateUser(user: User): DataResourceResult<Unit>
    suspend fun searchUsers(query: String): DataResourceResult<List<User>>
    suspend fun isNicknameDuplicate(nickname: String): Boolean

    // --- 팔로우 액션 (Action) ---
    suspend fun followUser(myId: String, targetUserId: String): DataResourceResult<Unit>
    suspend fun unfollowUser(myId: String, targetUserId: String): DataResourceResult<Unit>
    fun isFollowingFlow(myId: String, targetUserId: String): Flow<Boolean>
    // 1. 내 팔로잉 목록 보기: ID 리스트를 주면 유저 정보 리스트로 변환
    suspend fun getUsersByIds(userIds: List<String>): DataResourceResult<List<User>>
    // 2. 내 팔로워 목록 보기: 나를 팔로우하는 사람 검색
    suspend fun getFollowers(myUserId: String): DataResourceResult<List<User>>
    suspend fun getFollowingIds(userId: String): DataResourceResult<List<String>>
    fun getFollowingIdsFlow(userId: String): Flow<DataResourceResult<List<String>>>
    // --- 3. [추가] 뱃지 시스템 (Badge) ---
    // 뱃지는 유저 프로필의 일부이므로 여기서 가져옵니다.
    // 보통 Firestore의 `users/{userId}/badges` 서브컬렉션에서 긁어옵니다.
    suspend fun getUserBadges(userId: String): DataResourceResult<List<UserBadge>>

    //뱃지 저장 (획득 시 호출)
    suspend fun saveUserBadge(userId: String, badge: UserBadge): DataResourceResult<Unit>

    // - isAdding = true : 리스트에 추가 (좋아요 함)
    // - isAdding = false: 리스트에서 제거 (좋아요 취소)
    suspend fun toggleLikePost(userId: String, postId: String, isAdding: Boolean): DataResourceResult<Unit>

    suspend fun toggleBookmarkPost(userId: String, postId: String, isAdding: Boolean): DataResourceResult<Unit>

    suspend fun updateNotificationSetting(userId: String, isAgreed: Boolean): DataResourceResult<Unit>

    suspend fun deleteUser(userId: String): DataResourceResult<Unit>

}