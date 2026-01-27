package com.leafy.features.note.viewmodel

import com.subin.leafy.domain.model.BrewingNote

data class DetailUiState(
    val isLoading: Boolean = true,
    val note: BrewingNote? = null,
    val isAuthor: Boolean = false,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
)