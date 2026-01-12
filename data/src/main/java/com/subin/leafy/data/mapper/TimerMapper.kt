package com.subin.leafy.data.mapper

import com.subin.leafy.data.datasource.local.room.entity.TimerPresetEntity
import com.subin.leafy.data.model.dto.TimerPresetDto
import com.subin.leafy.domain.model.BrewingRecipe
import com.subin.leafy.domain.model.TeaType
import com.subin.leafy.domain.model.TimerPreset

// =================================================================
// 1. Entity (Room DB) <-> Domain
// =================================================================

// Entity -> Domain (DB에서 꺼내서 쓸 때)
fun TimerPresetEntity.toDomain(): TimerPreset {
    return TimerPreset(
        id = this.id,
        name = this.name,
        teaType = runCatching { TeaType.valueOf(this.teaType) }.getOrDefault(TeaType.ETC),
        isDefault = this.isDefault,
        recipe = BrewingRecipe(
            waterTemp = this.waterTemp,
            brewTimeSeconds = this.brewTimeSeconds,
            leafAmount = this.leafAmount,
            waterAmount = this.waterAmount,
            infusionCount = this.infusionCount,
            teaware = this.teaware
        )
    )
}

// Domain -> Entity (DB에 저장할 때)
fun TimerPreset.toEntity(): TimerPresetEntity {
    return TimerPresetEntity(
        id = this.id,
        name = this.name,
        teaType = this.teaType.name,
        isDefault = this.isDefault,

        waterTemp = this.recipe.waterTemp,
        brewTimeSeconds = this.recipe.brewTimeSeconds,
        leafAmount = this.recipe.leafAmount,
        waterAmount = this.recipe.waterAmount,
        infusionCount = this.recipe.infusionCount,
        teaware = this.recipe.teaware
    )
}

// =================================================================
// 2. DTO (Server/DataStore) <-> Entity & Domain
// =================================================================

// DTO -> Entity (서버 데이터를 로컬 DB에 넣을 때)
fun TimerPresetDto.toEntity(): TimerPresetEntity {
    return TimerPresetEntity(
        id = this.id,
        name = this.name,
        teaType = this.teaType,
        isDefault = this.isDefault,

        waterTemp = this.waterTemp,
        brewTimeSeconds = this.timeSeconds,
        leafAmount = this.leafAmount,
        waterAmount = this.waterAmount,
        infusionCount = this.infusionCount,
        teaware = ""
    )
}

// DTO -> Domain (리스트 변환 헬퍼에서 필요함!)
fun TimerPresetDto.toDomain(): TimerPreset {
    return TimerPreset(
        id = this.id,
        name = this.name,
        teaType = runCatching { TeaType.valueOf(this.teaType) }.getOrDefault(TeaType.ETC),
        isDefault = this.isDefault,
        recipe = BrewingRecipe(
            waterTemp = this.waterTemp,
            brewTimeSeconds = this.timeSeconds,
            leafAmount = this.leafAmount,
            waterAmount = this.waterAmount,
            infusionCount = this.infusionCount,
            teaware = ""
        )
    )
}

// =================================================================
// 3. 리스트 변환 헬퍼 (List Helper)
// =================================================================

// DTO 리스트 -> Domain 리스트
fun List<TimerPresetDto>.toTimerDomainList() = this.map { it.toDomain() }

// Entity 리스트 -> Domain 리스트
fun List<TimerPresetEntity>.toTimerDomainList() = this.map { it.toDomain() }