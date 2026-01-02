package com.leafy.features.mypage.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.ui.utils.LeafyTimeUtils
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserStats
import com.subin.leafy.domain.usecase.note.GetMonthlyRecordsUseCase
import com.subin.leafy.domain.usecase.note.GetRecordByDateUseCase
import com.subin.leafy.domain.usecase.user.GetCurrentUserIdUseCase
import com.subin.leafy.domain.usecase.user.GetUserStatsUseCase
import com.subin.leafy.domain.usecase.user.GetUserUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed interface MyPageUiEffect {
    data class NavigateToDetail(val noteId: String) : MyPageUiEffect
    // 필요하다면 설정 이동 등 추가 가능
}

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

    private val _effect = Channel<MyPageUiEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        refresh()
    }

    fun refresh() {
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
                        _uiState.update {
                            it.copy(
                                user = User(id = currentId, username = "사용자", profileImageUrl = null),
                                errorMessage = result.exception.message
                            )
                        }
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
                        _uiState.update {
                            it.copy(
                                userStats = UserStats(0, 0.0, "-", "-", 0),
                                isLoading = false
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun changeMonth(amount: Long) {
        _uiState.update { it.copy(selectedDate = it.selectedDate.plusMonths(amount)) }
        loadCurrentMonthRecords()
    }

    // [날짜 클릭 로직]
    fun onDateSelected(day: Int) {
        val newDate = _uiState.value.selectedDate.withDayOfMonth(day)
        _uiState.update { it.copy(selectedDate = newDate) }

        val dateString = LeafyTimeUtils.formatToString(newDate)

        val recordOnThatDay = _uiState.value.monthlyRecords.find { it.dateString == dateString }

        _uiState.update { it.copy(selectedRecord = recordOnThatDay) }
    }

    fun onRecordDetailClick(noteId: String) {
        viewModelScope.launch {
            _effect.send(MyPageUiEffect.NavigateToDetail(noteId))
        }
    }

    /** 현재 설정된 연/월의 모든 기록 로드 */
    private fun loadCurrentMonthRecords() {
        val year = _uiState.value.selectedDate.year
        val month = _uiState.value.selectedDate.monthValue

        viewModelScope.launch {
            val currentId = getCurrentUserIdUseCase() ?: return@launch

            // 한 달 치 데이터를 통째로 가져옵니다.
            getMonthlyRecordsUseCase(currentId, year, month).collect { result ->
                if (result is DataResourceResult.Success) {
                    val records = result.data

                    // 날짜 숫자 리스트 추출 (캘린더 점 표시용)
                    val days = records.map { record ->
                        LeafyTimeUtils.parseToDate(record.dateString).dayOfMonth
                    }.distinct()

                    _uiState.update { it.copy(
                        monthlyRecords = records,
                        recordedDays = days
                    ) }

                    // 데이터를 새로 불러왔으니, 현재 선택되어 있는 날짜(selectedDay)에
                    // 혹시 기록이 있는지 다시 한번 매칭 시도
                    onDateSelected(_uiState.value.selectedDay)
                }
            }
        }
    }
    }