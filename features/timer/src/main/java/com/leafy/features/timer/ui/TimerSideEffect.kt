package com.leafy.features.timer.ui

import com.leafy.shared.utils.UiText

sealed interface TimerSideEffect {
    data class ShowSnackbar(val message: UiText) : TimerSideEffect
    data class NavigateToNote(val navArgsJson: String) : TimerSideEffect
    data object NavigateBack : TimerSideEffect
}