package com.leafy.shared.ui.model

sealed interface CommunityPostUiModel {
    // 모든 게시글이 공통으로 가지는 속성
    val postId: String
    val authorId: String
    val authorName: String
    val authorProfileUrl: String?
    val isFollowingAuthor: Boolean
    val title: String
    val content: String
    val imageUrls: List<String>
    val tags: List<String>
    val timeAgo: String
    val likeCount: String
    val commentCount: String
    val viewCount: String
    val bookmarkCount: String
    val isLiked: Boolean
    val isBookmarked: Boolean

    // 1. 일반 게시글 (시음 정보 없음)
    data class General(
        override val postId: String,
        override val authorId: String,
        override val authorName: String,
        override val authorProfileUrl: String?,
        override val isFollowingAuthor: Boolean,
        override val title: String,
        override val content: String,
        override val imageUrls: List<String>,
        override val tags: List<String> = emptyList(),
        override val timeAgo: String,
        override val likeCount: String,
        override val commentCount: String,
        override val viewCount: String,
        override val bookmarkCount: String,
        override val isLiked: Boolean,
        override val isBookmarked: Boolean
    ) : CommunityPostUiModel

    // 2. 시음 노트 게시글 (시음 정보 필수)
    data class BrewingNote(
        override val postId: String,
        override val authorId: String,
        override val authorName: String,
        override val authorProfileUrl: String?,
        override val isFollowingAuthor: Boolean,
        override val title: String,
        override val content: String,
        override val imageUrls: List<String>,
        override val tags: List<String> = emptyList(),
        override val timeAgo: String,
        override val likeCount: String,
        override val commentCount: String,
        override val viewCount: String,
        override val bookmarkCount: String,
        override val isLiked: Boolean,
        override val isBookmarked: Boolean,

        // 시음 노트 전용 필드 (Non-nullable로 선언하여 스마트 캐스트 문제 해결)
        val teaType: String,
        val rating: Int,
        val brewingChips: List<String>,
        val originNoteId: String? // 원본 연결 ID만 선택적으로 유지
    ) : CommunityPostUiModel
}

// UserUiModel과 CommentUiModel은 기존 형태 유지 (필요 시 수정 가능)
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

data class CommentUiModel(
    val commentId: String,
    val authorId: String,
    val authorName: String,
    val authorProfileUrl: String?,
    val content: String,
    val timeAgo: String,
    val isMine: Boolean
)