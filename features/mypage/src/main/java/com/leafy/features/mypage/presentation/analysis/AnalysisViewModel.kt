package com.leafy.features.mypage.presentation.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.AnalysisUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnalysisViewModel(
    private val analysisUseCases: AnalysisUseCases,
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalysisUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAnalysisData()
    }

    private fun loadAnalysisData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val userResult = userUseCases.getCurrentUserId()

            if (userResult is DataResourceResult.Success) {
                val userId = userResult.data

                analysisUseCases.getUserAnalysis(userId)
                    .catch { e ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = e.message ?: "데이터를 불러오는 중 오류가 발생했습니다."
                            )
                        }
                    }
                    .collectLatest { analysis ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                analysisData = analysis,
                                isChartVisible = true
                            )
                        }
                    }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "로그인이 필요합니다."
                    )
                }
            }
        }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}