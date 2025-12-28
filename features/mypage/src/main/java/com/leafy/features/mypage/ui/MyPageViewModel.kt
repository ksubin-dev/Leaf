package com.leafy.features.mypage.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.ui.utils.LeafyTimeUtils
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.note.GetMonthlyRecordsUseCase
import com.subin.leafy.domain.usecase.note.GetRecordByDateUseCase
import com.subin.leafy.domain.usecase.user.GetCurrentUserIdUseCase
import com.subin.leafy.domain.usecase.user.GetUserStatsUseCase
import com.subin.leafy.domain.usecase.user.GetUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class MyPageViewModel(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getUserStatsUseCase: GetUserStatsUseCase,
    private val getMonthlyRecordsUseCase: GetMonthlyRecordsUseCase,
    private val getRecordByDateUseCase: GetRecordByDateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyPageUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserProfile()
        loadCurrentMonthRecords()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val currentId = getCurrentUserIdUseCase() ?: return@launch
            getUserUseCase(currentId).collect { result ->
                when (result) {
                    is DataResourceResult.Success -> {
                        _uiState.update { it.copy(user = result.data) }
                    }
                    is DataResourceResult.Failure -> {
                        _uiState.update { it.copy(errorMessage = result.exception.message) }
                    }
                    else -> {}
                }
            }
        }

        viewModelScope.launch {
            val currentId = getCurrentUserIdUseCase() ?: return@launch
            getUserStatsUseCase(currentId).collect { result ->
                when (result) {
                    is DataResourceResult.Success -> {
                        _uiState.update { it.copy(userStats = result.data, isLoading = false) }
                    }
                    is DataResourceResult.Failure -> {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                    else -> {}
                }
            }
        }
    }

    // [좌우 이동 로직]
    fun changeMonth(amount: Long) {
        _uiState.update { it.copy(selectedDateTime = it.selectedDateTime.plusMonths(amount)) }
        loadCurrentMonthRecords()
    }

    // [날짜 클릭 로직]
    fun onDateSelected(day: Int) {
        val newDate = _uiState.value.selectedDateTime.withDayOfMonth(day)
        _uiState.update { it.copy(selectedDateTime = newDate) }

        viewModelScope.launch {
            val dateString = LeafyTimeUtils.formatToString(newDate)
            val result = getRecordByDateUseCase(dateString)

            if (result is DataResourceResult.Success) {
                _uiState.update { it.copy(selectedRecord = result.data) }
            }
        }
    }

    private fun loadCurrentMonthRecords() {
        val year = _uiState.value.selectedDateTime.year
        val month = _uiState.value.selectedDateTime.monthValue

        viewModelScope.launch {
            getMonthlyRecordsUseCase(year, month).collect { result ->
                if (result is DataResourceResult.Success) {
                    val days = result.data.map { record ->
                        LeafyTimeUtils.parseToDateTime(record.dateString).dayOfMonth
                    }.distinct()

                    _uiState.update { it.copy(
                        monthlyRecords = result.data,
                        recordedDays = days
                    ) }
                }
            }
        }
    }
}