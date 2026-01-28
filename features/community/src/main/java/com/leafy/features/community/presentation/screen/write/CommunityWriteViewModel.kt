package com.leafy.features.community.presentation.screen.write

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.features.community.presentation.common.model.NoteSelectionUiModel
import com.leafy.shared.utils.ImageCompressor
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

sealed interface CommunityWriteSideEffect {
    data object PostSuccess : CommunityWriteSideEffect
    data class ShowSnackbar(val message: UiText) : CommunityWriteSideEffect
}

@HiltViewModel
class CommunityWriteViewModel @Inject constructor(
    private val postUseCases: PostUseCases,
    private val noteUseCases: NoteUseCases,
    private val userUseCases: UserUseCases,
    private val imageUseCases: ImageUseCases,
    private val imageCompressor: ImageCompressor
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
                sendEffect(CommunityWriteSideEffect.ShowSnackbar(UiText.DynamicString("노트를 불러오지 못했습니다.")))
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

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val finalImageUrls = processImages(state.selectedImageUris)

                val result = if (state.linkedNoteId != null) {
                    postUseCases.shareNoteAsPost(
                        noteId = state.linkedNoteId,
                        content = state.content,
                        tags = state.tags,
                        imageUrls = finalImageUrls
                    )
                } else {
                    createNormalPost(state, finalImageUrls)
                }

                if (result is DataResourceResult.Success) {
                    _uiState.update { it.copy(isLoading = false) }
                    sendEffect(CommunityWriteSideEffect.PostSuccess)
                } else {
                    throw Exception("게시글 업로드에 실패했습니다.")
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                sendEffect(CommunityWriteSideEffect.ShowSnackbar(UiText.DynamicString(e.message ?: "알 수 없는 오류")))
            }
        }
    }

    private suspend fun processImages(uris: List<Uri>): List<String> = coroutineScope {
        val userIdResult = userUseCases.getCurrentUserId()
        if (userIdResult is DataResourceResult.Failure) throw Exception("로그인이 필요합니다.")
        val userId = (userIdResult as DataResourceResult.Success).data

        val imageFolderId = UUID.randomUUID().toString()

        uris.map { uri ->
            async {
                val scheme = uri.scheme
                val uriString = uri.toString()
                if (scheme == "http" || scheme == "https") {
                    uriString
                } else {
                    try {
                        val compressedPath = imageCompressor.compressImage(uriString)
                        val uploadResult = imageUseCases.uploadImage(
                            uri = compressedPath,
                            folder = "posts/$userId/$imageFolderId"
                        )

                        if (uploadResult is DataResourceResult.Success) {
                            uploadResult.data
                        } else {
                            throw Exception("이미지 업로드 실패")
                        }
                    } catch (e: Exception) {
                        throw Exception("이미지 처리 중 오류 발생: ${e.message}")
                    }
                }
            }
        }.awaitAll()
    }

    private suspend fun createNormalPost(
        state: CommunityWriteUiState,
        imageUrls: List<String>
    ): DataResourceResult<Unit> {
        val newPostId = UUID.randomUUID().toString()

        return postUseCases.createPost(
            postId = newPostId,
            title = state.title,
            content = state.content,
            imageUrls = imageUrls,
            teaType = state.linkedTeaType,
            rating = state.linkedRating,
            tags = state.tags,
            brewingSummary = null,
            originNoteId = null
        )
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