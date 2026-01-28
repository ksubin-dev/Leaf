package com.leafy.features.home.presentation.home

import com.leafy.shared.utils.UiText

sealed interface HomeSideEffect {
    data class ShowSnackbar(val message: UiText) : HomeSideEffect
    data class NavigateTo(val route: String) : HomeSideEffect
}