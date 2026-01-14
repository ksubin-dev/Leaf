package com.subin.leafy.domain.model

data class AppSettings(
    val isDarkTheme: Boolean = false,
    val language: String = "ko",
    val isNotificationAgreed: Boolean = false,
    val autoLogin: Boolean = false
)