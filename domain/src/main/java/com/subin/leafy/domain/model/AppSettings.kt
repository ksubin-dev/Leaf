package com.subin.leafy.domain.model

data class AppSettings(
    val isAutoLoginEnabled: Boolean = false, // 로그인 상태 유지 여부
    val isPushNotificationEnabled: Boolean = true, // 알림 설정 (예시)
    val lastLoginEmail: String = "" // 자동 로그인 시 편의를 위해 저장
)