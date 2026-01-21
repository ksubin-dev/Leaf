package com.leafy.features.mypage.presentation.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.R
import com.leafy.shared.ui.model.UserUiModel
import com.leafy.shared.utils.ImageCompressor
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserAnalysis
import com.subin.leafy.domain.usecase.AnalysisUseCases
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.TeaUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import com.subin.leafy.domain.usecase.user.FollowType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class MyPageViewModel(
    private val userUseCases: UserUseCases,
    private val noteUseCases: NoteUseCases,
    private val postUseCases: PostUseCases,
    private val analysisUseCases: AnalysisUseCases,
    private val imageUseCases: ImageUseCases,
    private val teaUseCases: TeaUseCases,
    private val imageCompressor: ImageCompressor
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyPageUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialData()
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
                loadCalendarData(userId, LocalDate.now())
                loadSavedLists()
                loadFollowLists(userId)

                loadTeaStats()
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun loadTeaStats() {
        viewModelScope.launch {
            teaUseCases.getTeaCount().collectLatest { count ->
                _uiState.update { it.copy(myTeaCabinetCount = count) }
            }
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
                val (content, icon) = generateRandomInsight(analysis)

                _uiState.update {
                    it.copy(
                        userAnalysis = analysis,
                        analysisTeaserContent = content,
                        analysisTeaserIconRes = icon
                    )
                }
            }
        }
    }

    private fun generateRandomInsight(data: UserAnalysis): Pair<String?, Int?> {
        if (data.totalBrewingCount == 0) {
            return "아직 분석할 데이터가 없어요. 차를 마셔보세요!" to R.drawable.ic_leaf
        }

        val insights = mutableListOf<Pair<String, Int>>()

        if (data.preferredTimeSlot.isNotBlank()) {
            insights.add("주로 ${data.preferredTimeSlot} 시간대에 차를 즐깁니다." to R.drawable.ic_clock)
        }

        if (data.averageBrewingTime.isNotBlank()) {
            insights.add("평균 브루잉 시간은 ${data.averageBrewingTime} 입니다." to R.drawable.ic_timer)
        }

        if (data.preferredTemperature > 0) {
            insights.add("가장 선호하는 물 온도는 ${data.preferredTemperature}°C 입니다." to R.drawable.ic_temp)
        }

        if (!data.favoriteTeaType.isNullOrBlank() && data.favoriteTeaType != "-") {
            insights.add("요즘은 '${data.favoriteTeaType}'를 가장 많이 드셨네요." to R.drawable.ic_note_photo_teaware)
        }

        if (data.currentStreakDays > 2) {
            insights.add("${data.currentStreakDays}일째 연속으로 차를 마시고 계시네요!" to R.drawable.ic_weather_clear)
        }

        if (insights.isEmpty()) return null to null

        return insights.random()
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
            val noteDate = Instant.ofEpochMilli(note.date)
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

    fun toggleEditProfileMode() {
        _uiState.update { currentState ->
            val isEditing = !currentState.isEditingProfile
            if (isEditing) {
                currentState.copy(
                    isEditingProfile = true,
                    editNickname = currentState.myProfile?.nickname ?: "",
                    editBio = currentState.myProfile?.bio ?: "",
                    editProfileImageUri = null,
                    isNicknameValid = true,
                    profileEditMessage = null
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
            _uiState.update { it.copy(profileEditMessage = "닉네임을 확인해주세요.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            var profileImageUrl: String? = null

            if (currentState.editProfileImageUri != null) {
                try {
                    val compressedUriString = imageCompressor.compressImage(currentState.editProfileImageUri.toString())

                    val uploadResult = imageUseCases.uploadImage(
                        compressedUriString,
                        "profile_images"
                    )

                    if (uploadResult is DataResourceResult.Success) {
                        profileImageUrl = uploadResult.data
                    } else {
                        val errorMsg = (uploadResult as DataResourceResult.Failure).exception.message
                        _uiState.update {
                            it.copy(isLoading = false, profileEditMessage = "이미지 업로드 실패: $errorMsg")
                        }
                        return@launch
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(isLoading = false, profileEditMessage = "이미지 처리 중 오류 발생: ${e.message}")
                    }
                    return@launch
                }
            }

            val updateResult = userUseCases.updateProfile(
                nickname = currentState.editNickname,
                bio = currentState.editBio,
                profileUrl = profileImageUrl
            )

            if (updateResult is DataResourceResult.Failure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        profileEditMessage = updateResult.exception.message ?: "프로필 수정 실패"
                    )
                }
                return@launch
            }

            if (updateResult is DataResourceResult.Success) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isEditingProfile = false,
                        profileEditMessage = "프로필이 수정되었습니다."
                    )
                }
                loadMyProfile()
            }
        }
    }

    fun onProfileMessageShown() {
        _uiState.update { it.copy(profileEditMessage = null) }
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