package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.entity.SettingsEntity
import com.subin.leafy.domain.model.AppSettings


// 1. Entity -> Domain (로컬에서 설정을 읽어올 때)
fun SettingsEntity.toDomain() = AppSettings(
    isAutoLoginEnabled = this.isAutoLoginEnabled,
    isPushNotificationEnabled = this.isPushNotificationEnabled,
    lastLoginEmail = this.lastLoginEmail
)

// 2. Domain -> Entity (설정을 로컬에 저장할 때)
fun AppSettings.toEntity() = SettingsEntity(
    isAutoLoginEnabled = this.isAutoLoginEnabled,
    isPushNotificationEnabled = this.isPushNotificationEnabled,
    lastLoginEmail = this.lastLoginEmail
)