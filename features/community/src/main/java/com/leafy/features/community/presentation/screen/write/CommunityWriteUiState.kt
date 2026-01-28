package com.leafy.features.community.presentation.screen.write

import android.net.Uri
import com.leafy.features.community.presentation.common.model.NoteSelectionUiModel

data class CommunityWriteUiState(
    val title: String = "",
    val content: String = "",
    val selectedImageUris: List<Uri> = emptyList(),
    val tags: List<String> = emptyList(),
    val currentTagInput: String = "",


    val linkedNoteId: String? = null,
    val linkedNoteTitle: String? = null,
    val linkedTeaType: String? = null,
    val linkedDate: String? = null,
    val linkedRating: Int? = null,
    val linkedThumbnailUri: Uri? = null,

    val brewingSummary: String? = null,

    val myNotes: List<NoteSelectionUiModel> = emptyList(),

    val isLoading: Boolean = false,
) {
    val isPostValid: Boolean
        get() = title.isNotBlank() && title.length <= 50 &&
                content.trim().length >= 10 &&
                selectedImageUris.isNotEmpty()
}