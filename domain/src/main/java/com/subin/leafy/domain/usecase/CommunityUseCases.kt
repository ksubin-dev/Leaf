package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.community.GetFollowingFeedUseCase
import com.subin.leafy.domain.usecase.community.GetMostSavedNotesUseCase
import com.subin.leafy.domain.usecase.community.GetPopularNotesUseCase
import com.subin.leafy.domain.usecase.community.GetPopularTagsUseCase
import com.subin.leafy.domain.usecase.community.GetRecommendedMastersUseCase
import com.subin.leafy.domain.usecase.community.GetRisingNotesUseCase
import com.subin.leafy.domain.usecase.community.ToggleFollowUseCase
import com.subin.leafy.domain.usecase.community.ToggleLikeUseCase

data class CommunityUseCases(
    val getPopularNotes: GetPopularNotesUseCase,
    val getRisingNotes: GetRisingNotesUseCase,
    val getMostSavedNotes: GetMostSavedNotesUseCase,
    val getRecommendedMasters: GetRecommendedMastersUseCase,
    val getPopularTags: GetPopularTagsUseCase,
    val getFollowingFeed: GetFollowingFeedUseCase,
    val toggleLike: ToggleLikeUseCase,
    val toggleFollow: ToggleFollowUseCase
)