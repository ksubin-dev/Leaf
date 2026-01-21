package com.leafy.shared.ui.mapper

import com.leafy.shared.ui.model.CommentUiModel
import com.leafy.shared.ui.model.CommunityPostUiModel
import com.leafy.shared.ui.model.UserUiModel
import com.leafy.shared.ui.utils.LeafyTimeUtils
import com.leafy.shared.utils.toKiloFormat
import com.subin.leafy.domain.model.Comment
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.TeaMaster
import com.subin.leafy.domain.model.User

/**
 * [1] CommunityPost Domain -> Sealed UI Model 변환
 * teaType과 rating이 모두 존재하면 BrewingNote, 아니면 General로 변환합니다.
 */
fun CommunityPost.toUiModel(): CommunityPostUiModel {
    val teaTypeName = this.teaType?.name
    val ratingValue = this.rating

    return if (teaTypeName != null && ratingValue != null) {
        // 1. 시음 노트 게시글로 매핑
        CommunityPostUiModel.BrewingNote(
            postId = this.id,
            authorId = this.author.id,
            authorName = this.author.nickname,
            authorProfileUrl = this.author.profileImageUrl,
            isFollowingAuthor = this.author.isFollowing,
            title = this.title,
            content = this.content,
            imageUrls = this.imageUrls,
            tags = this.tags,
            timeAgo = LeafyTimeUtils.formatTimeAgo(this.createdAt),
            likeCount = this.stats.likeCount.toKiloFormat(),
            commentCount = this.stats.commentCount.toKiloFormat(),
            viewCount = this.stats.viewCount.toKiloFormat(),
            bookmarkCount = this.stats.bookmarkCount.toKiloFormat(),
            isLiked = this.myState.isLiked,
            isBookmarked = this.myState.isBookmarked,

            // BrewingNote 전용 필드 (Non-nullable)
            teaType = teaTypeName,
            rating = ratingValue,
            brewingChips = this.brewingSummary?.split("·")?.map { it.trim() } ?: emptyList(),
            originNoteId = this.originNoteId
        )
    } else {
        // 2. 일반 게시글로 매핑
        CommunityPostUiModel.General(
            postId = this.id,
            authorId = this.author.id,
            authorName = this.author.nickname,
            authorProfileUrl = this.author.profileImageUrl,
            isFollowingAuthor = this.author.isFollowing,
            title = this.title,
            content = this.content,
            imageUrls = this.imageUrls,
            tags = this.tags,
            timeAgo = LeafyTimeUtils.formatTimeAgo(this.createdAt),
            likeCount = this.stats.likeCount.toKiloFormat(),
            commentCount = this.stats.commentCount.toKiloFormat(),
            viewCount = this.stats.viewCount.toKiloFormat(),
            bookmarkCount = this.stats.bookmarkCount.toKiloFormat(),
            isLiked = this.myState.isLiked,
            isBookmarked = this.myState.isBookmarked
        )
    }
}

/**
 * [2] TeaMaster Domain -> UserUiModel 변환
 */
fun TeaMaster.toUiModel(): UserUiModel {
    return UserUiModel(
        userId = this.id,
        nickname = this.nickname,
        title = this.title,
        bio = null,
        profileImageUrl = this.profileImageUrl,
        isFollowing = this.isFollowing,
        followerCount = this.followerCount.toKiloFormat(),
        followingCount = "0",
        postCount = this.postCount.toString(),
        expertTags = this.expertTypes.map { it.label }
    )
}

/**
 * [3] Comment Domain -> CommentUiModel 변환
 */
fun Comment.toUiModel(): CommentUiModel {
    return CommentUiModel(
        commentId = this.id,
        authorId = this.author.id,
        authorName = this.author.nickname,
        authorProfileUrl = this.author.profileImageUrl,
        content = this.content,
        timeAgo = LeafyTimeUtils.formatTimeAgo(this.createdAt),
        isMine = this.isMine
    )
}

/**
 * [4] User Domain -> UserUiModel 변환
 */
fun User.toUiModel(): UserUiModel {
    return UserUiModel(
        userId = this.id,
        nickname = this.nickname,
        title = this.masterTitle ?: "티 러버",
        bio = this.bio,
        profileImageUrl = this.profileImageUrl,
        isFollowing = this.relationState.isFollowing,
        followerCount = this.socialStats.followerCount.toKiloFormat(),
        followingCount = this.socialStats.followingCount.toKiloFormat(),
        postCount = this.postCount.toString(),
        expertTags = this.expertTypes.map { it.label }
    )
}