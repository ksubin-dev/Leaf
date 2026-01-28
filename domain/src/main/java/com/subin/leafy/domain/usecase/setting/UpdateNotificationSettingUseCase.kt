package com.subin.leafy.domain.usecase.setting

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.SettingRepository
import javax.inject.Inject

class UpdateNotificationSettingUseCase @Inject constructor(
    private val settingRepository: SettingRepository
) {
    suspend fun setNotificationAgreed(isAgreed: Boolean): DataResourceResult<Unit> {
        return settingRepository.setNotificationAgreed(isAgreed)
    }
}