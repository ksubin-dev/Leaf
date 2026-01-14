package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserBadge
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    // 1. 내 프로필 실시간 감지 (Flow) - 내비게이션 드로어, 마이페이지용
    fun getMyProfileFlow(): Flow<DataResourceResult<User>>

    // 2. 특정 유저 프로필 조회 (단건) - 상대방 프로필 클릭 시
    // (내 팔로우 여부 isFollowing 계산 포함)
    suspend fun getUserProfile(targetUserId: String): DataResourceResult<User>

    // 3. 프로필 수정 (닉네임, 한줄소개, 프사)
    suspend fun updateProfile(
        nickname: String? = null,
        bio: String? = null,
        profileUrl: String? = null
    ): DataResourceResult<Unit>

    // 4. 소셜 (팔로우 / 언팔로우)
    suspend fun followUser(targetUserId: String): DataResourceResult<Unit>
    suspend fun unfollowUser(targetUserId: String): DataResourceResult<Unit>

    // 5. 팔로워/팔로잉 목록 가져오기
    suspend fun getFollowers(userId: String): DataResourceResult<List<User>>
    suspend fun getFollowings(userId: String): DataResourceResult<List<User>>

    // 6. 뱃지 목록 가져오기 (Library 데이터와 병합됨)
    suspend fun getUserBadges(userId: String): DataResourceResult<List<UserBadge>>

    // 7. 유저 검색
    suspend fun searchUsers(query: String): DataResourceResult<List<User>>

    // 8. 닉네임 중복 체크 (프로필 수정 시 사용)
    suspend fun checkNicknameAvailability(nickname: String): DataResourceResult<Boolean>
}