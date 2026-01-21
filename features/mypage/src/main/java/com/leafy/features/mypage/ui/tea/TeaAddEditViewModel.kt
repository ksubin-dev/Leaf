package com.leafy.features.mypage.ui.tea

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.utils.ImageCompressor
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TeaItem
import com.subin.leafy.domain.model.TeaType
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.TeaUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class TeaAddEditUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isTeaSaved: Boolean = false,

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

class TeaAddEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val teaUseCases: TeaUseCases,
    private val imageUseCases: ImageUseCases,
    private val imageCompressor: ImageCompressor
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeaAddEditUiState())
    val uiState = _uiState.asStateFlow()

    private val teaIdArg: String? = savedStateHandle["teaId"]

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
                val errorMsg = (result as? DataResourceResult.Failure)?.exception?.message
                    ?: "차 정보를 불러올 수 없습니다."

                _uiState.update {
                    it.copy(isLoading = false, errorMessage = errorMsg)
                }
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
                        throw Exception("이미지 업로드 실패")
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "이미지 처리 오류") }
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
                _uiState.update { it.copy(isLoading = false, isTeaSaved = true) }
            } else {
                val errorMsg = (result as DataResourceResult.Failure).exception.message
                _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg ?: "저장 실패") }
            }
        }
    }

    fun deleteTea() {
        val teaId = _uiState.value.teaId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = teaUseCases.deleteTea(teaId)
            if (result is DataResourceResult.Success) {
                _uiState.update { it.copy(isLoading = false, isTeaSaved = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "삭제 실패") }
            }
        }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}