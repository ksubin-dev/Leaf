package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.timer.*
import javax.inject.Inject

data class TimerUseCases @Inject constructor(
    val getPresets: GetPresetsUseCase,
    val savePreset: SavePresetUseCase,
    val deletePreset: DeletePresetUseCase,
    val saveLastUsedRecipe: SaveLastUsedRecipeUseCase,
    val getLastUsedRecipe: GetLastUsedRecipeUseCase,
    val getTimerSettings: GetTimerSettingsUseCase,
    val updateTimerSettings: UpdateTimerSettingsUseCase
)