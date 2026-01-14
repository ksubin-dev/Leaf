package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.timer.*

data class TimerUseCases(
    // 프리셋
    val getPresets: GetPresetsUseCase,
    val savePreset: SavePresetUseCase,
    val deletePreset: DeletePresetUseCase,

    // 최근 기록
    val saveLastUsedRecipe: SaveLastUsedRecipeUseCase,
    val getLastUsedRecipe: GetLastUsedRecipeUseCase,

    // 설정
    val getTimerSettings: GetTimerSettingsUseCase,
    val updateTimerSettings: UpdateTimerSettingsUseCase
)