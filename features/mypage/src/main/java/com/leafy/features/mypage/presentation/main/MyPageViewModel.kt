package com.leafy.features.mypage.presentation.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.R
import com.leafy.shared.ui.model.UserUiModel
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserAnalysis
import com.subin.leafy.domain.usecase.AnalysisUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.TeaUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import com.subin.leafy.domain.usecase.user.FollowType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

sealed interface MyPageSideEffect {
    data class ShowToast(val message: UiText) : MyPageSideEffect
}

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val noteUseCases: NoteUseCases,
    private val postUseCases: PostUseCases,
    private val analysisUseCases: AnalysisUseCases,
    private val teaUseCases: TeaUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyPageUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<MyPageSideEffect>()
    val sideEffect: Flow<MyPageSideEffect> = _sideEffect.receiveAsFlow()

    private val _currentCalendarDate = MutableStateFlow(LocalDate.now())


    init {
        loadInitialData()
        observeCalendarData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            launch { noteUseCases.syncNotes() }
            launch { teaUseCases.syncTeas() }

            loadMyProfile()

            val result = userUseCases.getCurrentUserId()
            val userId = if (result is DataResourceResult.Success) result.data else null

            if (userId != null) {
                loadAnalysisData(userId)
                loadSavedLists()
                loadFollowLists(userId)
                loadTeaStats()
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeCalendarData() {
        _currentCalendarDate
            .map { it.year to it.monthValue }
            .distinctUntilChanged()
            .flatMapLatest { (year, month) ->
                val userIdResult = userUseCases.getCurrentUserId()
                val userId = if (userIdResult is DataResourceResult.Success) userIdResult.data else ""

                if (userId.isNotEmpty()) {
                    noteUseCases.getNotesByMonth(userId, year, month)
                } else {
                    flowOf(emptyList())
                }
            }
            .onEach { notes ->
                _uiState.update { state ->
                    val selectedNotes = filterNotesByDate(notes, state.selectedDate)
                    state.copy(
                        calendarNotes = notes,
                        selectedDateNotes = selectedNotes
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onMonthChanged(newDate: LocalDate) {
        _uiState.update {
            it.copy(
                currentYear = newDate.year,
                currentMonth = newDate.monthValue,
                selectedDate = newDate
            )
        }
        _currentCalendarDate.value = newDate
    }

    private fun loadTeaStats() {
        teaUseCases.getTeaCount()
            .onEach { count ->
                _uiState.update { it.copy(myTeaCabinetCount = count) }
            }
            .launchIn(viewModelScope)
    }

    private fun loadMyProfile() {
        userUseCases.getMyProfile()
            .onEach { result ->
                if (result is DataResourceResult.Success) {
                    _uiState.update {
                        it.copy(
                            myProfile = result.data,
                            followerCount = result.data.socialStats.followerCount,
                            followingCount = result.data.socialStats.followingCount
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadAnalysisData(userId: String) {
        analysisUseCases.getUserAnalysis(userId)
            .onEach { analysis ->
                val (content, icon) = generateRandomInsight(analysis)
                _uiState.update {
                    it.copy(
                        userAnalysis = analysis,
                        analysisTeaserContent = content,
                        analysisTeaserIconRes = icon
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadSavedLists() {
        postUseCases.getBookmarkedPosts()
            .onEach { result ->
                if (result is DataResourceResult.Success) {
                    _uiState.update { it.copy(bookmarkedPosts = result.data) }
                }
            }
            .launchIn(viewModelScope)

        postUseCases.getLikedPosts()
            .onEach { result ->
                if (result is DataResourceResult.Success) {
                    _uiState.update { it.copy(likedPosts = result.data) }
                }
            }
            .launchIn(viewModelScope)
    }

    fun loadFollowLists(userIdParam: String? = null) {
        val userId = userIdParam ?: _uiState.value.myProfile?.id ?: return

        viewModelScope.launch {
            val followerResult = userUseCases.getFollowList(userId, FollowType.FOLLOWER)
            if (followerResult is DataResourceResult.Success) {
                val uiModels = followerResult.data.map { it.toUiModel() }
                _uiState.update { it.copy(followerList = uiModels) }
            }

            val followingResult = userUseCases.getFollowList(userId, FollowType.FOLLOWING)
            if (followingResult is DataResourceResult.Success) {
                val uiModels = followingResult.data.map { it.toUiModel() }
                _uiState.update { it.copy(followingList = uiModels) }
            }
        }
    }

    fun onDateSelected(date: LocalDate) {
        val filtered = filterNotesByDate(_uiState.value.calendarNotes, date)
        _uiState.update { it.copy(selectedDate = date, selectedDateNotes = filtered) }
    }

    fun toggleFollow(targetUserId: String) {
        viewModelScope.launch {
            val targetUser = _uiState.value.followingList.find { it.userId == targetUserId }
                ?: _uiState.value.followerList.find { it.userId == targetUserId }

            val isCurrentlyFollowing = targetUser?.isFollowing ?: false
            val newStatus = !isCurrentlyFollowing

            val result = userUseCases.followUser(targetUserId, newStatus)

            if (result is DataResourceResult.Success) {
                loadFollowLists()
                loadMyProfile()
            } else {
                sendEffect(MyPageSideEffect.ShowToast(UiText.StringResource(R.string.msg_follow_failed)))
            }
        }
    }

    fun toggleEditProfileMode() {
        _uiState.update { currentState ->
            if (!currentState.isEditingProfile) {
                currentState.copy(
                    isEditingProfile = true,
                    editNickname = currentState.myProfile?.nickname ?: "",
                    editBio = currentState.myProfile?.bio ?: "",
                    editProfileImageUri = null,
                    isNicknameValid = true
                )
            } else {
                currentState.copy(isEditingProfile = false)
            }
        }
    }

    fun onProfileImageSelected(uri: Uri) {
        _uiState.update { it.copy(editProfileImageUri = uri) }
    }

    fun onNicknameChange(newNickname: String) {
        _uiState.update { it.copy(editNickname = newNickname) }
        viewModelScope.launch {
            if (newNickname == _uiState.value.myProfile?.nickname) {
                _uiState.update { it.copy(isNicknameValid = true) }
            } else {
                val isAvailable = userUseCases.checkNickname(newNickname)
                val isValid = isAvailable is DataResourceResult.Success && isAvailable.data
                _uiState.update { it.copy(isNicknameValid = isValid) }
            }
        }
    }

    fun onBioChange(newBio: String) {
        _uiState.update { it.copy(editBio = newBio) }
    }

    fun saveProfile() {
        val currentState = _uiState.value

        if (!currentState.isNicknameValid) {
            sendEffect(MyPageSideEffect.ShowToast(UiText.StringResource(R.string.msg_check_nickname)))
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val imageUriString = currentState.editProfileImageUri?.toString()

            userUseCases.scheduleProfileUpdate(
                nickname = currentState.editNickname,
                bio = currentState.editBio,
                imageUriString = imageUriString
            )

            _uiState.update { state ->
                val updatedProfile = state.myProfile?.copy(
                    nickname = currentState.editNickname,
                    bio = currentState.editBio,
                    profileImageUrl = imageUriString ?: state.myProfile.profileImageUrl
                )

                state.copy(
                    isLoading = false,
                    isEditingProfile = false,
                    myProfile = updatedProfile
                )
            }

            sendEffect(MyPageSideEffect.ShowToast(UiText.StringResource(R.string.msg_save_start_background)))
        }
    }

    private fun generateRandomInsight(data: UserAnalysis): Pair<UiText?, Int?> {
        if (data.totalBrewingCount == 0) {
            return UiText.StringResource(R.string.insight_no_data) to R.drawable.ic_leaf
        }

        val insights = mutableListOf<Pair<UiText, Int>>()

        if (data.preferredTimeSlot.isNotBlank()) {
            insights.add(UiText.StringResource(R.string.insight_time_slot, data.preferredTimeSlot) to R.drawable.ic_clock)
        }
        if (data.averageBrewingTime.isNotBlank()) {
            insights.add(UiText.StringResource(R.string.insight_avg_time, data.averageBrewingTime) to R.drawable.ic_timer)
        }
        if (data.preferredTemperature > 0) {
            insights.add(UiText.StringResource(R.string.insight_pref_temp, data.preferredTemperature) to R.drawable.ic_temp)
        }
        if (!data.favoriteTeaType.isNullOrBlank() && data.favoriteTeaType != "-") {
            insights.add(UiText.StringResource(R.string.insight_fav_tea, data.favoriteTeaType!!) to R.drawable.ic_note_photo_teaware)
        }
        if (data.currentStreakDays > 2) {
            insights.add(UiText.StringResource(R.string.insight_streak, data.currentStreakDays) to R.drawable.ic_weather_clear)
        }
        return if (insights.isEmpty()) null to null else insights.random()
    }

    private fun filterNotesByDate(notes: List<BrewingNote>, date: LocalDate): List<BrewingNote> {
        return notes.filter { note ->
            Instant.ofEpochMilli(note.date).atZone(ZoneId.systemDefault()).toLocalDate() == date
        }
    }

    private fun sendEffect(effect: MyPageSideEffect) {
        viewModelScope.launch { _sideEffect.send(effect) }
    }

    private fun User.toUiModel(): UserUiModel {
        return UserUiModel(
            userId = this.id,
            nickname = this.nickname,
            profileImageUrl = this.profileImageUrl,
            title = this.masterTitle ?: "티 러버",
            isFollowing = this.relationState.isFollowing,
            followerCount = this.socialStats.followerCount.toString(),
            expertTags = this.expertTypes.map { it.name }
        )
    }
}