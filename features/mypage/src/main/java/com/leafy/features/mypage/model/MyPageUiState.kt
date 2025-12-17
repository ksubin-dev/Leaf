package com.leafy.features.mypage.model

import com.leafy.features.mypage.presentation.analyze.data.BrewingPatternData
import com.leafy.features.mypage.presentation.analyze.data.TeaTypeRecord
import com.leafy.features.mypage.presentation.analyze.data.TeaRecommendation
import com.leafy.features.mypage.presentation.analyze.data.TopTeaRanking
import com.leafy.features.mypage.presentation.badges.data.BadgeItem
import com.leafy.features.mypage.presentation.collection.data.TeaCollectionItem
import com.leafy.features.mypage.presentation.wishlist.data.WishlistItem
import com.leafy.features.mypage.presentation.wishlist.data.SavedCommunityNote
import com.leafy.shared.R as SharedR


data class MyPageUiState(
    val isLoading: Boolean = false,
    val username: String = "Loading...",
    val bio: String = "",
    val profileImageRes: Int = SharedR.drawable.ic_profile_1,
    val stats: UserStats = UserStats(),
    val analyzeData: AnalyzeData = AnalyzeData(),
    val wishlistData: WishlistData = WishlistData(),
    val collectionItems: List<TeaCollectionItem> = emptyList(),
    val badges: List<BadgeItem> = emptyList(),
    val error: String? = null
)


data class UserStats(
    val notesCount: Int = 0,
    val postsCount: Int = 0,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val rating: Float = 0.0f,
    val badgesCount: Int = 0
)

data class AnalyzeData(
    val brewingData: BrewingPatternData = BrewingPatternData("N/A", "N/A", "0회", "N/A"),
    val teaTypeRecords: List<TeaTypeRecord> = emptyList(),
    val recommendations: List<TeaRecommendation> = emptyList(),
    val topTeas: List<TopTeaRanking> = emptyList()
)

data class WishlistData(
    val wishlistItems: List<WishlistItem> = emptyList(),
    val savedNotes: List<SavedCommunityNote> = emptyList()
)