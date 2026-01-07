package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.TimerPresetDto
import com.subin.leafy.domain.model.TimerPreset
import com.subin.leafy.domain.model.TeaType

// 1. DTO -> Domain (서버에서 내 프리셋 불러오기)
fun TimerPresetDto.toTimerDomain() = TimerPreset(
    id = this.id,
    name = this.name,
    teaType = runCatching { TeaType.valueOf(this.teaType) }.getOrDefault(TeaType.ETC),
    temperature = this.waterTemp,
    baseTimeSeconds = this.timeSeconds,
    leafAmount = this.leafAmount,
    waterAmount = 200
)

// 2. Domain -> DTO (새 프리셋 저장하거나 수정할 때)
fun TimerPreset.toDto(userId: String) = TimerPresetDto(
    id = this.id,
    userId = userId, // 누구의 프리셋인지 저장 시점에 주입
    name = this.name,
    waterTemp = this.temperature,
    timeSeconds = this.baseTimeSeconds,
    leafAmount = this.leafAmount,
    teaType = this.teaType.name
)

/**
 * 리스트 변환 헬퍼
 */
fun List<TimerPresetDto>.toTimerDomainList() = this.map { it.toTimerDomain() }