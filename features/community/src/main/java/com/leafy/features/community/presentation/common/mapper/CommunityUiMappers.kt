package com.leafy.features.community.presentation.common.mapper

import com.leafy.features.community.presentation.common.model.CommentUiModel
import com.leafy.features.community.presentation.common.model.CommunityPostUiModel
import com.leafy.features.community.presentation.common.model.UserUiModel
import com.leafy.features.community.util.toKiloFormat
import com.leafy.shared.ui.utils.LeafyTimeUtils
import com.subin.leafy.domain.model.Comment
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.TeaMaster
import com.subin.leafy.domain.model.User

// [1] Post Domain -> UI
fun CommunityPost.toUiModel(): CommunityPostUiModel {
    return CommunityPostUiModel(
        postId = this.id,
        authorId = this.author.id,
        authorName = this.author.nickname,
        authorProfileUrl = this.author.profileImageUrl,
        isFollowingAuthor = this.author.isFollowing,

        title = this.title,
        content = this.content,
        imageUrls = this.imageUrls,

        tags = this.tags,
        originNoteId = this.originNoteId,

        timeAgo = LeafyTimeUtils.getRelativeTime(this.createdAt),

        brewingSummary = this.brewingSummary?.takeIf { it.isNotBlank() },

        teaType = this.teaType?.name,
        brewingChips = this.brewingSummary?.split("·")?.map { it.trim() } ?: emptyList(),

        likeCount = this.stats.likeCount.toKiloFormat(),
        commentCount = this.stats.commentCount.toKiloFormat(),
        viewCount = this.stats.viewCount.toKiloFormat(),
        bookmarkCount = this.stats.bookmarkCount.toKiloFormat(),

        rating = this.rating,

        isLiked = this.myState.isLiked,
        isBookmarked = this.myState.isBookmarked
    )
}

// [2] TeaMaster Domain -> UI (UserUiModel)
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

// [3] Comment Domain -> UI
fun Comment.toUiModel(): CommentUiModel {
    return CommentUiModel(
        commentId = this.id,
        authorId = this.author.id,
        authorName = this.author.nickname,
        authorProfileUrl = this.author.profileImageUrl,
        content = this.content,
        timeAgo = LeafyTimeUtils.getRelativeTime(this.createdAt),
        isMine = this.isMine
    )
}
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