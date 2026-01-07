package com.subin.leafy.data.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class SettingsEntity(
    val isAutoLoginEnabled: Boolean = false,
    val isPushNotificationEnabled: Boolean = true,
    val lastLoginEmail: String = ""
)