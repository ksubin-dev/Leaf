package com.leafy.features.community.ui.model

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

// 2. 티 마스터 (추천 사용자용)
data class UserUiModel(
    val userId: String,
    val nickname: String,
    val title: String,            // 예: "홍차 소믈리에"
    val profileImageUrl: String?,
    val isFollowing: Boolean,
    val followerCount: String,    // "500", "1.5k"
    val expertTags: List<String>  // ["녹차", "홍차"] - 티 타입 칩 표시용
)

// 3. 댓글 (상세 보기용)
data class CommentUiModel(
    val commentId: String,
    val authorId: String,
    val authorName: String,
    val authorProfileUrl: String?,
    val content: String,
    val timeAgo: String,          // "방금 전"
    val isMine: Boolean           // 삭제/수정 버튼 표시 여부
)