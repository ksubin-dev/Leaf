package com.leafy.features.community.presentation.common.model

data class CommunityPostUiModel(
    val postId: String,
    val authorId: String,
    val authorName: String,
    val authorProfileUrl: String?,
    val isFollowingAuthor: Boolean,

    val title: String,
    val content: String,
    val imageUrls: List<String>,

    val tags: List<String> = emptyList(),
    val originNoteId: String? = null,

    val timeAgo: String,

    val teaType: String?,
    val brewingSummary: String?,
    val rating: Int?,

    val brewingChips: List<String> = emptyList(),

    val likeCount: String,
    val commentCount: String,
    val viewCount: String,
    val bookmarkCount: String,

    val isLiked: Boolean,
    val isBookmarked: Boolean
) {
    val isBrewingNote: Boolean get() = teaType != null
}

// 2. 티 마스터 및 유저 프로필용 공통 모델
data class UserUiModel(
    val userId: String,
    val nickname: String,
    val title: String,
    val bio: String? = null,

    val profileImageUrl: String?,
    val isFollowing: Boolean,
    val followerCount: String,
    val followingCount: String = "0",
    val postCount: String = "0",
    val expertTags: List<String>
)

// 3. 댓글 (상세 보기용)
data class CommentUiModel(
    val commentId: String,
    val authorId: String,
    val authorName: String,
    val authorProfileUrl: String?,
    val content: String,
    val timeAgo: String,
    val isMine: Boolean
)