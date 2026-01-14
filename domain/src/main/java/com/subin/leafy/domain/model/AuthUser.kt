package com.subin.leafy.domain.model

data class AuthUser(
    val id: String,
    val email: String,
    val nickname: String? = null,
    val profileUrl: String? = null,
    val followingIds: List<String> = emptyList(),
    val likedPostIds: List<String> = emptyList(),
    val bookmarkedPostIds: List<String> = emptyList(),

    val fcmToken: String? = null,    // 알림 수신을 위한 기기 토큰
    val isNewUser: Boolean = false,  // 가입 직후 온보딩/프로필 설정으로 보낼지 결정용
    val providerId: String? = null   //  어떤 방식으로 로그인했는지 (탈퇴/재인증 시 필요)
)