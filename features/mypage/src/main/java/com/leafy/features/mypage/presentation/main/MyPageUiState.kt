package com.leafy.features.mypage.presentation.main

import android.net.Uri
import com.leafy.shared.ui.model.UserUiModel
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserAnalysis
import java.time.LocalDate

data class MyPageUiState(
    val isLoading: Boolean = false,

    val myProfile: User? = null,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val followerList: List<UserUiModel> = emptyList(),
    val followingList: List<UserUiModel> = emptyList(),

    val isEditingProfile: Boolean = false,
    val editNickname: String = "",
    val editBio: String = "",
    val isNicknameValid: Boolean = true,
    val editProfileImageUri: Uri? = null,
    val userAnalysis: UserAnalysis? = null,

    val currentYear: Int = LocalDate.now().year,
    val currentMonth: Int = LocalDate.now().monthValue,
    val calendarNotes: List<BrewingNote> = emptyList(),

    val selectedDate: LocalDate = LocalDate.now(),
    val selectedDateNotes: List<BrewingNote> = emptyList(),

    val analysisTeaserContent: UiText? = null,
    val analysisTeaserIconRes: Int? = null,

    val bookmarkedPosts: List<CommunityPost> = emptyList(),
    val likedPosts: List<CommunityPost> = emptyList(),
    val myTeaCabinetCount: Int = 0,
)