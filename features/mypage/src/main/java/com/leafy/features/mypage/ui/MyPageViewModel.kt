package com.leafy.features.mypage.ui
//
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.leafy.shared.ui.utils.LeafyTimeUtils
//import com.subin.leafy.domain.common.DataResourceResult
//import com.subin.leafy.domain.model.User
//import com.subin.leafy.domain.model.UserStats
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.channels.BufferOverflow
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//
//sealed interface MyPageUiEffect {
//    data class NavigateToDetail(val noteId: String) : MyPageUiEffect
//    data class NavigateToDailyRecords(val date: String) : MyPageUiEffect
//    data class ShowSnackbar(val message: String) : MyPageUiEffect
//}
//
//class MyPageViewModel(
//    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
//    private val getUserUseCase: GetUserUseCase,
//    private val getUserStatsUseCase: GetUserStatsUseCase,
//    private val getMonthlyRecordsUseCase: GetMonthlyRecordsUseCase,
//    private val getBrewingInsightsUseCase: GetBrewingInsightsUseCase,
//) : ViewModel() {
//
//    private val _selectedDate = MutableStateFlow(LeafyTimeUtils.now())
//
//    private val _effect = MutableSharedFlow<MyPageUiEffect>(
//        replay = 0,
//        extraBufferCapacity = 1,
//        onBufferOverflow = BufferOverflow.DROP_OLDEST
//    )
//    val effect = _effect.asSharedFlow()
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    private val userInfoFlow = flow { emit(getCurrentUserIdUseCase()) }
//        .filterNotNull()
//        .flatMapLatest { id ->
//            combine(
//                getUserUseCase(id),
//                getUserStatsUseCase(id),
//                getBrewingInsightsUseCase(id).map { DataResourceResult.Success(it) }
//            ) { user, stats, insights -> Triple(user, stats, insights) }
//        }
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    private val monthlyRecordsFlow = _selectedDate
//        .map { it.year to it.monthValue }
//        .distinctUntilChanged()
//        .flatMapLatest { (year, month) ->
//            val currentId = getCurrentUserIdUseCase() ?: return@flatMapLatest flowOf(DataResourceResult.Loading)
//            getMonthlyRecordsUseCase(currentId, year, month)
//        }
//
//    val uiState: StateFlow<MyPageUiState> = combine(
//        userInfoFlow,
//        monthlyRecordsFlow,
//        _selectedDate
//    ) { (userRes, statsRes, insightRes), recordsRes, selectedDate ->
//
//        val error = listOf(userRes, statsRes, recordsRes, insightRes)
//            .filterIsInstance<DataResourceResult.Failure>()
//            .firstOrNull()?.exception?.message
//
//        val records = (recordsRes as? DataResourceResult.Success)?.data ?: emptyList()
//        val insights = (insightRes as? DataResourceResult.Success)?.data ?: emptyList()
//
//        MyPageUiState(
//            user = (userRes as? DataResourceResult.Success)?.data,
//            userStats = (statsRes as? DataResourceResult.Success)?.data,
//            selectedDate = selectedDate,
//            monthlyRecords = records,
//            brewingInsights = insights,
//            recordedDays = records.mapNotNull { record ->
//                runCatching { LeafyTimeUtils.parseToDate(record.dateString).dayOfMonth }.getOrNull()
//            }.distinct(),
//            selectedRecord = records.find { it.dateString == LeafyTimeUtils.formatToString(selectedDate) },
//            isLoading = recordsRes is DataResourceResult.Loading || userRes is DataResourceResult.Loading,
//            errorMessage = error
//        )
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = MyPageUiState(isLoading = true)
//    )
//
//    fun changeMonth(amount: Long) {
//        _selectedDate.update { it.plusMonths(amount) }
//    }
//
//    fun onDateSelected(day: Int) {
//        _selectedDate.update { it.withDayOfMonth(day) }
//    }
//
//    fun onViewAllClick(date: String) {
//        viewModelScope.launch {
//            _effect.emit(MyPageUiEffect.NavigateToDailyRecords(date))
//        }
//    }
//
//    fun onRecordDetailClick(noteId: String) {
//        viewModelScope.launch {
//            _effect.emit(MyPageUiEffect.NavigateToDetail(noteId))
//        }
//    }
//
//    fun refresh() {
//        _selectedDate.update { it }
//    }
//}