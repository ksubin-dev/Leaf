package com.leafy.features.mypage.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        loadUserProfile()          // 사용자 정보 로드
        loadCurrentMonthRecords()  // 기존 로직
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val currentId = getCurrentUserIdUseCase()

                // 2. ID를 이용해 유저 정보와 통계 정보를 "동시에" 가져오기 (async 활용 가능)
                // 현재는 간단하게 순차 호출로 작성합니다.
                val user = getUserUseCase(currentId)
                val stats = getUserStatsUseCase(currentId)

                _uiState.update {
                    it.copy(
                        user = user,
                        userStats = stats,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                // 필요 시 에러 처리 로직 추가
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
            val result = getRecordByDateUseCase(newDate.toLocalDate())
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
                    val days = result.data.map { it.date.dayOfMonth }
                    _uiState.update { it.copy(recordedDays = days) }
                }
            }
        }
    }
}