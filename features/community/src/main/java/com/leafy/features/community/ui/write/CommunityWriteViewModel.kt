package com.leafy.features.community.ui.write

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.features.community.ui.model.NoteSelectionUiModel
import com.leafy.shared.util.ImageCompressor
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CommunityWriteViewModel(
    private val postUseCases: PostUseCases,
    private val noteUseCases: NoteUseCases,
    private val userUseCases: UserUseCases,
    private val imageUseCases: ImageUseCases,
    private val imageCompressor: ImageCompressor
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityWriteUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchMyNotes()
    }

    private fun fetchMyNotes() {
        viewModelScope.launch {
            noteUseCases.getMyNotes().collectLatest { notes ->
                _uiState.update { state ->
                    state.copy(
                        myNotes = notes.map { it.toUiModel() }
                    )
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

            val result = noteUseCases.getNoteDetail(noteId)

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

                        title = note.teaInfo.name,
                        selectedImageUris = note.metadata.imageUrls.map { it.toUri() },
                        tags = note.evaluation.flavorTags.map { "#${it.name}" }
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "노트를 불러오지 못했습니다.") }
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
                brewingSummary = null
            )
        }
    }

    fun uploadPost() {
        val state = uiState.value
        if (!state.isPostValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val userIdResult = userUseCases.getCurrentUserId()
                if (userIdResult is DataResourceResult.Failure) throw Exception("User not found")
                val userId = (userIdResult as DataResourceResult.Success).data

                val postImageFolderId = UUID.randomUUID().toString()

                val finalImageUrls = state.selectedImageUris.map { uri ->
                    async {
                        val uriString = uri.toString()
                        if (uriString.startsWith("http")) {
                            uriString
                        } else {
                            val compressedPath = imageCompressor.compressImage(uriString)
                            val uploadResult = imageUseCases.uploadImage(
                                uri = compressedPath,
                                folder = "posts/$userId/$postImageFolderId"
                            )
                            if (uploadResult is DataResourceResult.Success) uploadResult.data
                            else throw Exception("Image upload failed")
                        }
                    }
                }.awaitAll()

                val result = postUseCases.createPost(
                    postId = postImageFolderId,
                    title = state.title,
                    content = state.content,
                    imageUrls = finalImageUrls,
                    teaType = state.linkedTeaType,
                    rating = state.linkedRating,
                    tags = state.tags,
                    brewingSummary = state.brewingSummary,
                    originNoteId = state.linkedNoteId
                )

                if (result is DataResourceResult.Success) {
                    _uiState.update { it.copy(isLoading = false, isPostSuccess = true) }
                } else {
                    throw Exception("Post creation failed")
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}

fun BrewingNote.toUiModel(): NoteSelectionUiModel {
    val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)

    val formattedDate = try {
        dateFormat.format(Date(this.date))
    } catch (e: Exception) {
        ""
    }

    return NoteSelectionUiModel(
        id = this.id,
        title = this.teaInfo.name,
        teaType = this.teaInfo.type.name,
        date = formattedDate,
        rating = this.rating.stars,
        thumbnailUri = this.metadata.imageUrls.firstOrNull()?.toUri()
    )
}