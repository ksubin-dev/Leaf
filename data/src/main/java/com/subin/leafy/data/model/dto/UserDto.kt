package com.subin.leafy.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val uid: String = "",
    val nickname: String = "",
    val profileImageUrl: String? = null,
    val bio: String? = "",

    // 3. 소셜 수치
    val followerCount: Int = 0,        // 나를 팔로우하는 사람 수
    val followingCount: Int = 0,       // 내가 팔로우하는 사람 수

    val followingIds: List<String> = emptyList(), // 내가 팔로우 중인 유저 UID 목록 (팔로잉 목록 조회용)
    val likedPostIds: List<String> = emptyList(), // 좋아요 누른 시음 노트 ID 목록
    val savedPostIds: List<String> = emptyList(), // 북마크(저장)한 시음 노트 ID 목록

    // 5. 시스템 및 알림 설정
    val fcmToken: String? = null,      // 푸시 알림용 토큰
    val createdAt: Long = System.currentTimeMillis() // 가입일 (오래된 순 정렬 등)
)
