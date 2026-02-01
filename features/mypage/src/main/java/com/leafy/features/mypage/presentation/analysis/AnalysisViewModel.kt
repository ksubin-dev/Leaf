package com.leafy.features.mypage.presentation.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.R
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.UserAnalysis
import com.subin.leafy.domain.usecase.AnalysisUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AnalysisSideEffect {
    data class ShowToast(val message: UiText) : AnalysisSideEffect
}

data class AnalysisUiState(
    val isLoading: Boolean = false,
    val analysisData: UserAnalysis? = null,
    val isChartVisible: Boolean = false
)

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val analysisUseCases: AnalysisUseCases,
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalysisUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<AnalysisSideEffect>()
    val sideEffect: Flow<AnalysisSideEffect> = _sideEffect.receiveAsFlow()

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
                        _uiState.update { it.copy(isLoading = false) }
                        sendEffect(AnalysisSideEffect.ShowToast(UiText.StringResource(R.string.msg_analysis_error)))
                    }
                    .onEach { analysis ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                analysisData = analysis,
                                isChartVisible = true
                            )
                        }
                    }
                    .launchIn(viewModelScope)
            } else {
                _uiState.update { it.copy(isLoading = false) }
                sendEffect(AnalysisSideEffect.ShowToast(UiText.StringResource(R.string.msg_login_required)))
            }
        }
    }

    private fun sendEffect(effect: AnalysisSideEffect) {
        viewModelScope.launch { _sideEffect.send(effect) }
    }
}