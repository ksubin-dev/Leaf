package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.repository.PostChangeEvent
import com.subin.leafy.domain.usecase.post.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class PostUseCases @Inject constructor(
    val getWeeklyRanking: GetWeeklyRankingUseCase,
    val getPopularPosts: GetPopularPostsUseCase,
    val getMostBookmarkedPosts: GetMostBookmarkedPostsUseCase,
    val getFollowingFeed: GetFollowingFeedUseCase,
    val getPostDetail: GetPostDetailUseCase,
    val searchPosts: SearchPostsUseCase,
    val getRecommendedMasters: GetRecommendedMastersUseCase,
    val getUserPosts: GetUserPostsUseCase,
    val getLikedPosts: GetLikedPostsUseCase,
    val getBookmarkedPosts: GetBookmarkedPostsUseCase,

    val createPost: CreatePostUseCase,
    val updatePost: UpdatePostUseCase,
    val deletePost: DeletePostUseCase,

    val shareNoteAsPost: ShareNoteAsPostUseCase,

    val toggleLike: ToggleLikeUseCase,
    val toggleBookmark: ToggleBookmarkUseCase,
    val incrementViewCount: IncrementViewCountUseCase,

    val getComments: GetCommentsUseCase,
    val addComment: AddCommentUseCase,
    val deleteComment: DeleteCommentUseCase,

    val observePostChanges: ObservePostChangesUseCase,

    val schedulePostUpload: SchedulePostUpload

    //나중에 추가 신고 기능
)