package com.leafy.features.mypage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.features.mypage.model.*
import com.leafy.features.mypage.presentation.badges.data.BadgeItem
import com.leafy.features.mypage.presentation.collection.data.TeaCollectionItem
import com.leafy.features.mypage.presentation.analyze.data.BrewingPatternData
import com.leafy.features.mypage.presentation.analyze.data.TeaRecommendation
import com.leafy.features.mypage.presentation.analyze.data.TeaTypeRecord
import com.leafy.features.mypage.presentation.analyze.data.TopTeaRanking
import com.leafy.features.mypage.presentation.wishlist.data.WishlistItem
import com.leafy.features.mypage.presentation.wishlist.data.SavedCommunityNote
import com.leafy.shared.R as SharedR
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyPageViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MyPageUiState(isLoading = true))
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

    init {
        loadMyPageData()
    }

    private fun loadMyPageData() {
        viewModelScope.launch {

            val stats = createDummyUserStats()

            _uiState.value = MyPageUiState(
                isLoading = false,
                username = "TeaLover_Jane",
                bio = "Tea Enthusiast",
                profileImageRes = SharedR.drawable.ic_profile_1,
                stats = stats,
                analyzeData = createDummyAnalyzeData(),
                wishlistData = createDummyWishlistData(),
                collectionItems = createDummyCollectionData(),
                badges = createDummyBadgesData()
            )
        }
    }


    private fun createDummyUserStats() = UserStats(
        notesCount = 127, postsCount = 43, followerCount = 120,
        followingCount = 45, rating = 4.2f, badgesCount = 12
    )

    private fun createDummyAnalyzeData() = AnalyzeData(
        brewingData = BrewingPatternData("85°C", "3분 30초", "4회", "오후 (14:00 - 17:00)"),
        teaTypeRecords = listOf(
            TeaTypeRecord("녹차", 28), TeaTypeRecord("홍차", 35), TeaTypeRecord("우롱차", 18),
            TeaTypeRecord("백차", 12), TeaTypeRecord("말차", 5), TeaTypeRecord("황차", 2)
        ),
        recommendations = listOf(
            TeaRecommendation("1", "Dragon Well Green", "Tea lover", 4.5f, "비슷한 취향", ""),
            TeaRecommendation("2", "Iron Goddess Oolong", "Teavana", 4.7f, "새로운 발견", "")
        ),
        topTeas = listOf(
            TopTeaRanking(1, "Milky Oolong", 12, 4.8f, ""),
            TopTeaRanking(2, "Chamomile Blend", 9, 4.7f, ""),
            TopTeaRanking(3, "Darjeeling First Flush", 7, 4.6f, "")
        )
    )

    private fun createDummyWishlistData() = WishlistData(
        wishlistItems = listOf(
            WishlistItem("1", "다즐링 퍼스트 플러시", "인도 | 홍차", SharedR.drawable.ic_sample_tea_4),
            WishlistItem("2", "백모단 화이트티", "중국 | 백차", SharedR.drawable.ic_sample_tea_5)
        ),
        savedNotes = listOf(
            SavedCommunityNote("n1", "Dragon Well Green Tea", "Fresh, grassy...", 4.5f, 124, SharedR.drawable.ic_sample_tea_13, SharedR.drawable.ic_profile_1)
        )
    )

    private fun createDummyCollectionData() = listOf(
        TeaCollectionItem("1", "Earl Grey Supreme", "Twinings", "Plenty", SharedR.drawable.ic_sample_collection_tea_1),
        TeaCollectionItem("2", "Sencha Green", "Ippodo Tea", "Low", SharedR.drawable.ic_sample_collection_tea_2)
    )

    private fun createDummyBadgesData() = listOf(
        BadgeItem(id = "1", title = "녹차 러버", description = "녹차 10회 기록", isAcquired = true, iconRes = SharedR.drawable.ic_badges_1),
        BadgeItem(id = "2", title = "100번째 기록", description = "총 100번째 시음 노트 작성", isAcquired = false, progress = "잠금 해제 필요", iconRes = SharedR.drawable.ic_badges_2)
    )
}