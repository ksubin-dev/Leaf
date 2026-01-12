package com.leafy.features.note.ui.detail

import com.subin.leafy.domain.model.BrewingNote

data class DetailUiState(
    val isLoading: Boolean = true,
    val note: BrewingNote? = null, // 로딩 후 데이터가 들어옴
    val isAuthor: Boolean = false, // 본인 글인지 여부
    val isLiked: Boolean = false,  // (UI 즉시 반영용)
    val isBookmarked: Boolean = false,
    val errorMessage: String? = null,
    val isDeleteSuccess: Boolean = false // 삭제 완료 시 화면 종료 트리거
)