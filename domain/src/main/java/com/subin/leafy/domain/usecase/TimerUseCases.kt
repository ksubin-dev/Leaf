package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.timer.GetPresetsUseCase

data class TimerUseCases(
    val getPresets: GetPresetsUseCase
)