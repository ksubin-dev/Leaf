package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.*
import com.subin.leafy.domain.model.*

// =================================================================
// 1. DTO -> Domain (서버에서 가져올 때)
// =================================================================

fun CommunityPostDto.toDomain(): CommunityPost {
    return CommunityPost(
        id = this.id,
        author = this.author.toDomain(),
        imageUrls = this.imageUrls,
        title = this.title,
        content = this.content,
        originNoteId = this.originNoteId,
        teaType = this.teaType?.let { runCatching { TeaType.valueOf(it) }.getOrNull() },
        rating = this.rating,
        tags = this.tags,
        brewingSummary = this.brewingSummary,

        stats = this.stats.toDomain(),

        // 내 상태는 Repository에서 별도 로직으로 채워넣으므로 초기값은 false
        myState = PostSocialState(isLiked = false, isBookmarked = false),

        topComment = this.topComment?.toDomain(),
        createdAt = this.createdAt
    )
}

fun PostAuthorDto.toDomain() = PostAuthor(
    id = this.id,
    nickname = this.nickname,
    profileImageUrl = this.profileImageUrl,
    isFollowing = false
)

fun PostStatisticsDto.toDomain() = PostStatistics(
    likeCount = this.likeCount,
    bookmarkCount = this.bookmarkCount,
    commentCount = this.commentCount,
    viewCount = this.viewCount
)

fun CommentDto.toDomain() = Comment(
    id = this.id,
    postId = this.postId,
    author = CommentAuthor(
        id = this.author.id,
        nickname = this.author.nickname,
        profileImageUrl = this.author.profileImageUrl
    ),
    content = this.content,
    createdAt = this.createdAt,
    isMine = false
)

fun CommunityPost.toRankingItem(rank: Int): RankingItem {
    return RankingItem(
        rank = rank,
        postId = this.id,
        teaName = this.title,
        teaType = this.teaType ?: TeaType.ETC,
        rating = this.rating,
        viewCount = this.stats.viewCount,
        imageUrl = this.imageUrls.firstOrNull()
    )
}

// =================================================================
// 2. Domain -> DTO (서버에 업로드할 때)
// =================================================================

fun CommunityPost.toDto() = CommunityPostDto(
    id = this.id,
    author = this.author.toDto(),
    title = this.title,
    content = this.content,
    imageUrls = this.imageUrls,
    originNoteId = this.originNoteId,
    teaType = this.teaType?.name,
    rating = this.rating,
    tags = this.tags,
    brewingSummary = this.brewingSummary,
    stats = this.stats.toDto(),
    topComment = this.topComment?.toDto(),
    createdAt = this.createdAt
)

fun PostAuthor.toDto() = PostAuthorDto(
    id = this.id,
    nickname = this.nickname,
    profileImageUrl = this.profileImageUrl
)

fun PostStatistics.toDto() = PostStatisticsDto(
    likeCount = this.likeCount,
    bookmarkCount = this.bookmarkCount,
    commentCount = this.commentCount,
    viewCount = this.viewCount
)

fun Comment.toDto() = CommentDto(
    id = this.id,
    postId = this.postId,
    author = PostAuthorDto(
        id = this.author.id,
        nickname = this.author.nickname,
        profileImageUrl = this.author.profileImageUrl
    ),
    content = this.content,
    createdAt = this.createdAt
)

// =================================================================
// 3. List 변환 헬퍼 (Extension Functions)
// =================================================================

fun List<CommunityPostDto>.toDomainList() = this.map { it.toDomain() }
fun List<CommentDto>.toDomainList() = this.map { it.toDomain() }