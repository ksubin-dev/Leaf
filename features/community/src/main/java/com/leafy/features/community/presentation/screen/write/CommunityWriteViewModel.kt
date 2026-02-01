package com.leafy.features.community.presentation.screen.write

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.features.community.presentation.common.model.NoteSelectionUiModel
import com.leafy.shared.R
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

sealed interface CommunityWriteSideEffect {
    data object PostSuccess : CommunityWriteSideEffect
    data class ShowToast(val message: UiText) : CommunityWriteSideEffect
}

@HiltViewModel
class CommunityWriteViewModel @Inject constructor(
    private val postUseCases: PostUseCases,
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityWriteUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<CommunityWriteSideEffect>()
    val sideEffect: Flow<CommunityWriteSideEffect> = _sideEffect.receiveAsFlow()

    init {
        fetchMyNotes()
    }

    private fun fetchMyNotes() {
        viewModelScope.launch {
            noteUseCases.getMyNotes().collectLatest { notes ->
                _uiState.update { state ->
                    state.copy(myNotes = notes.map { it.toUiModel() })
                }
            }
        }
    }


    fun updateTitle(text: String) = _uiState.update { it.copy(title = text) }
    fun updateContent(text: String) = _uiState.update { it.copy(content = text) }

    fun addImages(uris: List<Uri>) {
        _uiState.update { it.copy(selectedImageUris = it.selectedImageUris + uris) }
    }

    fun removeImage(uri: Uri) {
        _uiState.update { it.copy(selectedImageUris = it.selectedImageUris - uri) }
    }

    fun updateTagInput(input: String) {
        if (input.endsWith(" ") || input.endsWith("\n")) {
            addTag(input.trim())
        } else {
            _uiState.update { it.copy(currentTagInput = input) }
        }
    }

    fun addTag(tag: String) {
        if (tag.isBlank()) return
        val newTag = if (tag.startsWith("#")) tag else "#$tag"
        _uiState.update {
            if (!it.tags.contains(newTag)) {
                it.copy(tags = it.tags + newTag, currentTagInput = "")
            } else {
                it.copy(currentTagInput = "")
            }
        }
    }

    fun removeTag(tag: String) {
        _uiState.update { it.copy(tags = it.tags - tag) }
    }

    fun onNoteSelected(noteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = noteUseCases.getNoteDetail(noteId = noteId)

            if (result is DataResourceResult.Success) {
                val note = result.data
                val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)

                val minutes = note.recipe.brewTimeSeconds / 60
                val seconds = note.recipe.brewTimeSeconds % 60
                val timeString = if (seconds > 0) "${minutes}분 ${seconds}초" else "${minutes}분"
                val summaryText = "${note.recipe.waterTemp}℃ · $timeString"

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        linkedNoteId = note.id,
                        linkedNoteTitle = note.teaInfo.name,
                        linkedTeaType = note.teaInfo.type.name,
                        linkedDate = dateFormat.format(Date(note.date)),
                        linkedRating = note.rating.stars,
                        linkedThumbnailUri = note.metadata.imageUrls.firstOrNull()?.toUri(),
                        brewingSummary = summaryText,
                        title = "${note.teaInfo.brand} ${note.teaInfo.name}",
                        selectedImageUris = note.metadata.imageUrls.map { it.toUri() },
                        tags = note.evaluation.flavorTags.map { "#${it.name}" }
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
                sendEffect(CommunityWriteSideEffect.ShowToast(
                    UiText.StringResource(R.string.msg_note_load_fail)
                ))
            }
        }
    }

    fun clearLinkedNote() {
        _uiState.update {
            it.copy(
                linkedNoteId = null,
                linkedNoteTitle = null,
                linkedTeaType = null,
                linkedDate = null,
                linkedRating = null,
                linkedThumbnailUri = null,
                brewingSummary = null,
            )
        }
    }

    fun uploadPost() {
        val state = uiState.value
        if (!state.isPostValid) return

        _uiState.update { it.copy(isLoading = true) }

        postUseCases.schedulePostUpload(
            title = state.title,
            content = state.content,
            tags = state.tags,
            imageUriStrings = state.selectedImageUris.map { it.toString() },
            linkedNoteId = state.linkedNoteId,
            linkedTeaType = state.linkedTeaType,
            linkedRating = state.linkedRating
        )

        _uiState.update { it.copy(isLoading = false) }
        sendEffect(CommunityWriteSideEffect.ShowToast(
            UiText.StringResource(R.string.msg_save_start_background)
        ))
        sendEffect(CommunityWriteSideEffect.PostSuccess)
    }

    private fun sendEffect(effect: CommunityWriteSideEffect) {
        viewModelScope.launch { _sideEffect.send(effect) }
    }
}

fun BrewingNote.toUiModel(): NoteSelectionUiModel {
    val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
    val formattedDate = try { dateFormat.format(Date(this.date)) } catch (e: Exception) { "" }

    return NoteSelectionUiModel(
        id = this.id,
        title = this.teaInfo.name,
        teaType = this.teaInfo.type.name,
        date = formattedDate,
        rating = this.rating.stars,
        thumbnailUri = this.metadata.imageUrls.firstOrNull()?.toUri()
    )
}