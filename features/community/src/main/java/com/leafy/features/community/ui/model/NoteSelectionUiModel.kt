package com.leafy.features.community.ui.model

import android.net.Uri

data class NoteSelectionUiModel(
    val id: String,
    val title: String,        // 예: "에티오피아 예가체프"
    val teaType: String,      // <-- 이렇게 분리! (예: "핸드드립" or "녹차")
    val date: String,         // <-- 이렇게 분리! (예: "2024.01.14")
    val rating: Int,          // 별점 (숫자만)
    val thumbnailUri: Uri? = null // (선택사항) 썸네일 이미지
)