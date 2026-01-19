package com.subin.leafy.data.mapper

import com.subin.leafy.data.datasource.local.room.entity.TimerPresetEntity
import com.subin.leafy.data.model.dto.TimerPresetDto
import com.subin.leafy.domain.model.BrewingRecipe
import com.subin.leafy.domain.model.TeaType
import com.subin.leafy.domain.model.TeawareType
import com.subin.leafy.domain.model.TimerPreset

// =================================================================
// 1. Entity (Room DB) <-> Domain
// =================================================================

// Entity -> Domain
fun TimerPresetEntity.toTimerPresetDomain(): TimerPreset {
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
            teaware = runCatching { TeawareType.valueOf(this.teaware) }.getOrDefault(TeawareType.ETC)
        )
    )
}

// Domain -> Entity
fun TimerPreset.toTimerPresetEntity(): TimerPresetEntity {
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
        teaware = this.recipe.teaware.name
    )
}

// =================================================================
// 2. DTO (Server/NavArgs) <-> Domain & Entity
// =================================================================

// DTO -> Domain
fun TimerPresetDto.toTimerPresetDomain(): TimerPreset {
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
            teaware = runCatching { TeawareType.valueOf(this.teaware) }.getOrDefault(TeawareType.ETC)
        )
    )
}

// Domain -> DTO
fun TimerPreset.toTimerPresetDto(userId: String): TimerPresetDto {
    return TimerPresetDto(
        id = this.id,
        userId = userId,
        name = this.name,
        teaType = this.teaType.name,
        isDefault = this.isDefault,
        waterTemp = this.recipe.waterTemp,
        timeSeconds = this.recipe.brewTimeSeconds,
        leafAmount = this.recipe.leafAmount,
        waterAmount = this.recipe.waterAmount,
        infusionCount = this.recipe.infusionCount,
        teaware = this.recipe.teaware.name
    )
}

// DTO -> Entity
fun TimerPresetDto.toTimerPresetEntity(): TimerPresetEntity {
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
        teaware = this.teaware
    )
}

fun List<TimerPresetDto>.toTimerDomainListFromDto() = this.map { it.toTimerPresetDomain() }
fun List<TimerPresetEntity>.toTimerDomainListFromEntity() = this.map { it.toTimerPresetDomain() }