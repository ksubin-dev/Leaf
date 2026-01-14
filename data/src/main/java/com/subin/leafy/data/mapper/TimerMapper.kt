package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.TimerPresetDTO
import com.subin.leafy.domain.model.TimerPreset

fun TimerPresetDTO.toDomain() = TimerPreset(
    id = this.id,
    name = this.name,
    temp = this.temp,
    baseTimeSeconds = this.time,    // DTO의 time을 Domain의 baseTimeSeconds로
    leafAmount = this.amount,       // DTO의 amount를 Domain의 leafAmount로
    teaType = this.type             // DTO의 type을 Domain의 teaType으로
)

// 리스트 변환 유틸리티
fun List<TimerPresetDTO>.toDomainList() = this.map { it.toDomain() }