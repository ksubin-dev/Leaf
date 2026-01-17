package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.post.*

data class PostUseCases(
    // 1. 조회
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

    // 2. CRUD
    val createPost: CreatePostUseCase,
    val updatePost: UpdatePostUseCase,
    val deletePost: DeletePostUseCase,

    val shareNoteAsPost: ShareNoteAsPostUseCase,

    // 3. 반응
    val toggleLike: ToggleLikeUseCase,
    val toggleBookmark: ToggleBookmarkUseCase,
    val incrementViewCount: IncrementViewCountUseCase,

    // 4. 댓글
    val getComments: GetCommentsUseCase,
    val addComment: AddCommentUseCase,
    val deleteComment: DeleteCommentUseCase

    //나중에 추가
    //신고 기능
)