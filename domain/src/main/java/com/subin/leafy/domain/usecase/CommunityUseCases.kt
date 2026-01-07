package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.community.AddCommentUseCase
import com.subin.leafy.domain.usecase.community.DeleteCommentUseCase
import com.subin.leafy.domain.usecase.community.GetCommentsUseCase
import com.subin.leafy.domain.usecase.community.GetFollowingFeedUseCase
import com.subin.leafy.domain.usecase.community.GetMostSavedNotesUseCase
import com.subin.leafy.domain.usecase.community.GetPopularNotesUseCase
import com.subin.leafy.domain.usecase.community.GetRecommendedMastersUseCase
import com.subin.leafy.domain.usecase.community.ToggleFollowUseCase
import com.subin.leafy.domain.usecase.community.ToggleLikeUseCase
import com.subin.leafy.domain.usecase.community.ToggleSaveUseCase

data class CommunityUseCases(
    val getPopularNotes: GetPopularNotesUseCase,
    val getMostSavedNotes: GetMostSavedNotesUseCase,
    val getRecommendedMasters: GetRecommendedMastersUseCase,
    val getFollowingFeed: GetFollowingFeedUseCase,
    val toggleLike: ToggleLikeUseCase,
    val toggleSave: ToggleSaveUseCase,
    val toggleFollow: ToggleFollowUseCase,
    val getComments: GetCommentsUseCase,
    val addComment: AddCommentUseCase,
    val deleteComment: DeleteCommentUseCase
)