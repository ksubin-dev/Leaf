package com.leafy.features.mypage.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.ui.model.UserUiModel
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.usecase.AnalysisUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import com.subin.leafy.domain.usecase.user.FollowType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class MyPageViewModel(
    private val userUseCases: UserUseCases,
    private val noteUseCases: NoteUseCases,
    private val postUseCases: PostUseCases,
    private val analysisUseCases: AnalysisUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyPageUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            launch {
                noteUseCases.syncNotes()
            }

            loadMyProfile()
            val result = userUseCases.getCurrentUserId()

            val userId = if (result is DataResourceResult.Success) result.data else null
            if (userId != null) {
                loadAnalysisData(userId)
                loadCalendarData(userId, LocalDate.now())
                loadSavedLists()
                loadFollowLists(userId)
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun loadMyProfile() {
        userUseCases.getMyProfile().onEach { result ->
            if (result is DataResourceResult.Success) {
                _uiState.update {
                    it.copy(
                        myProfile = result.data,
                        followerCount = result.data.socialStats.followerCount,
                        followingCount = result.data.socialStats.followingCount
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun loadAnalysisData(userId: String) {
        viewModelScope.launch {
            analysisUseCases.getUserAnalysis(userId).collectLatest { analysis ->
                _uiState.update {
                    it.copy(userAnalysis = analysis)
                }
            }
        }
    }

    private fun loadCalendarData(userId: String, date: LocalDate) {
        viewModelScope.launch {
            noteUseCases.getNotesByMonth(userId, date.year, date.monthValue)
                .collectLatest { notes ->
                    val selectedNotes = filterNotesByDate(notes, _uiState.value.selectedDate)
                    _uiState.update {
                        it.copy(
                            calendarNotes = notes,
                            selectedDateNotes = selectedNotes
                        )
                    }
                }
        }
    }

    private fun loadSavedLists() {
        viewModelScope.launch {
            postUseCases.getBookmarkedPosts().collectLatest { result ->
                if (result is DataResourceResult.Success) {
                    _uiState.update { it.copy(bookmarkedPosts = result.data) }
                }
            }
        }

        viewModelScope.launch {
            postUseCases.getLikedPosts().collectLatest { result ->
                if (result is DataResourceResult.Success) {
                    _uiState.update { it.copy(likedPosts = result.data) }
                }
            }
        }
    }

    fun onDateSelected(date: LocalDate) {
        val filtered = filterNotesByDate(_uiState.value.calendarNotes, date)
        _uiState.update {
            it.copy(selectedDate = date, selectedDateNotes = filtered)
        }
    }

    fun onMonthChanged(newDate: LocalDate) {
        _uiState.update {
            it.copy(
                currentYear = newDate.year,
                currentMonth = newDate.monthValue,
                selectedDate = newDate
            )
        }
        val userId = _uiState.value.myProfile?.id ?: return
        loadCalendarData(userId, newDate)
    }

    private fun filterNotesByDate(notes: List<BrewingNote>, date: LocalDate): List<BrewingNote> {
        return notes.filter { note ->
            val noteDate = java.time.Instant.ofEpochMilli(note.date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            noteDate == date
        }
    }

    fun loadFollowLists(userIdParam: String? = null) {
        val userId = userIdParam ?: _uiState.value.myProfile?.id ?: return

        viewModelScope.launch {
            val followerResult = userUseCases.getFollowList(userId, FollowType.FOLLOWER)
            if (followerResult is DataResourceResult.Success) {
                val uiModels = followerResult.data.map { it.toUiModel() }
                _uiState.update { it.copy(followerList = uiModels) }
            }
        }

        viewModelScope.launch {
            val followingResult = userUseCases.getFollowList(userId, FollowType.FOLLOWING)
            if (followingResult is DataResourceResult.Success) {
                val uiModels = followingResult.data.map { it.toUiModel() }
                _uiState.update { it.copy(followingList = uiModels) }
            }
        }
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
            }
        }
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