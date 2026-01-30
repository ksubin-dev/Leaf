package com.leafy.features.mypage.presentation.tea.edit

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.R
import com.leafy.shared.utils.ImageCompressor
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TeaItem
import com.subin.leafy.domain.model.TeaType
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.TeaUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed interface TeaAddEditSideEffect {
    data class ShowToast(val message: UiText) : TeaAddEditSideEffect
    data object SaveSuccess : TeaAddEditSideEffect
    data object DeleteSuccess : TeaAddEditSideEffect
}

data class TeaAddEditUiState(
    val isLoading: Boolean = false,
    val teaId: String? = null,
    val selectedImageUri: Uri? = null,
    val currentImageUrl: String? = null,
    val brand: String = "",
    val name: String = "",
    val selectedType: TeaType = TeaType.BLACK,
    val origin: String = "",
    val stockQuantity: String = "",
    val memo: String = ""
) {
    val isFormValid: Boolean
        get() = brand.isNotBlank() && name.isNotBlank()
}

@HiltViewModel
class TeaAddEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val teaUseCases: TeaUseCases,
    private val imageUseCases: ImageUseCases,
    private val imageCompressor: ImageCompressor
) : ViewModel() {

    private val teaIdArg: String? = savedStateHandle["teaId"]

    private val _uiState = MutableStateFlow(TeaAddEditUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<TeaAddEditSideEffect>()
    val sideEffect: Flow<TeaAddEditSideEffect> = _sideEffect.receiveAsFlow()

    init {
        if (!teaIdArg.isNullOrBlank() && teaIdArg != "null") {
            loadTeaDetail(teaIdArg)
        }
    }

    private fun loadTeaDetail(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = teaUseCases.getTeaDetail(id)

            if (result is DataResourceResult.Success) {
                val tea = result.data
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        teaId = tea.id,
                        currentImageUrl = tea.imageUrl,
                        brand = tea.brand,
                        name = tea.name,
                        selectedType = tea.type,
                        origin = tea.origin,
                        stockQuantity = tea.stockQuantity,
                        memo = tea.memo
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
                sendEffect(TeaAddEditSideEffect.ShowToast(UiText.StringResource(R.string.msg_tea_load_failed)))
            }
        }
    }

    fun onImageSelected(uri: Uri?) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }
    fun onBrandChange(text: String) { _uiState.update { it.copy(brand = text) } }
    fun onNameChange(text: String) { _uiState.update { it.copy(name = text) } }
    fun onTypeSelected(type: TeaType) { _uiState.update { it.copy(selectedType = type) } }
    fun onOriginChange(text: String) { _uiState.update { it.copy(origin = text) } }
    fun onStockChange(text: String) { _uiState.update { it.copy(stockQuantity = text) } }
    fun onMemoChange(text: String) { _uiState.update { it.copy(memo = text) } }

    fun saveTea() {
        if (!_uiState.value.isFormValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val state = _uiState.value

            var finalImageUrl: String? = state.currentImageUrl
            if (state.selectedImageUri != null) {
                try {
                    val compressedUriStr = imageCompressor.compressImage(state.selectedImageUri.toString())
                    val uploadResult = imageUseCases.uploadImage(compressedUriStr, "tea_images")
                    if (uploadResult is DataResourceResult.Success) {
                        finalImageUrl = uploadResult.data
                    } else {
                        sendEffect(TeaAddEditSideEffect.ShowToast(UiText.StringResource(R.string.msg_image_upload_fail)))
                        _uiState.update { it.copy(isLoading = false) }
                        return@launch
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false) }
                    val msg = e.message
                    val uiText = if (msg != null) UiText.StringResource(R.string.msg_image_process_error, msg)
                    else UiText.StringResource(R.string.msg_unknown_error)
                    sendEffect(TeaAddEditSideEffect.ShowToast(uiText))
                    return@launch
                }
            }

            val teaToSave = TeaItem(
                id = state.teaId ?: UUID.randomUUID().toString(),
                ownerId = "",
                name = state.name,
                brand = state.brand,
                type = state.selectedType,
                origin = state.origin,
                stockQuantity = state.stockQuantity,
                imageUrl = finalImageUrl,
                memo = state.memo,
            )

            val result = teaUseCases.saveTea(teaToSave)
            if (result is DataResourceResult.Success) {
                _uiState.update { it.copy(isLoading = false) }
                sendEffect(TeaAddEditSideEffect.SaveSuccess)
            } else {
                val errorMsg = (result as DataResourceResult.Failure).exception.message
                _uiState.update { it.copy(isLoading = false) }
                val uiText = if (errorMsg != null) UiText.DynamicString(errorMsg)
                else UiText.StringResource(R.string.msg_tea_save_failed)
                sendEffect(TeaAddEditSideEffect.ShowToast(uiText))
            }
        }
    }

    fun deleteTea() {
        val teaId = _uiState.value.teaId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = teaUseCases.deleteTea(teaId)
            if (result is DataResourceResult.Success) {
                _uiState.update { it.copy(isLoading = false) }
                sendEffect(TeaAddEditSideEffect.DeleteSuccess)
                sendEffect(TeaAddEditSideEffect.ShowToast(UiText.StringResource(R.string.msg_tea_delete_success)))
            } else {
                _uiState.update { it.copy(isLoading = false) }
                sendEffect(TeaAddEditSideEffect.ShowToast(UiText.StringResource(R.string.msg_delete_failed)))
            }
        }
    }

    private fun sendEffect(effect: TeaAddEditSideEffect) {
        viewModelScope.launch { _sideEffect.send(effect) }
    }
}