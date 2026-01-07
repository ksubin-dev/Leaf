package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.CommunityPostDto
import com.subin.leafy.domain.model.*

// [1] DTO -> Domain (피드 불러오기용)
fun CommunityPostDto.toCommunityDomain(isFollowing: Boolean = false) = CommunityPost(
    id = this.id,
    author = PostAuthor(
        id = this.authorId,
        nickname = this.authorName,
        profileImageUrl = this.authorProfileUrl,
        isFollowing = isFollowing
    ),
    title = this.title,
    content = this.content,
    imageUrls = this.imageUrls,
    originNoteId = this.originNoteId,
    teaType = this.teaType?.let { runCatching { TeaType.valueOf(it) }.getOrDefault(TeaType.ETC) },
    rating = this.rating,
    tags = this.tags,
    brewingSummary = this.brewingSummary,
    stats = PostStatistics(
        likeCount = this.likeCount,
        bookmarkCount = this.bookmarkCount,
        commentCount = this.commentCount,
        viewCount = this.viewCount
    ),
    myState = PostSocialState(isLiked = false, isBookmarked = false),
    createdAt = this.createdAt,
    topComment = null
)

// [2] Domain -> DTO (서버 저장용)
fun CommunityPost.toDto() = CommunityPostDto(
    id = this.id,
    authorId = this.author.id,
    authorName = this.author.nickname,
    authorProfileUrl = this.author.profileImageUrl,

    title = this.title,
    content = this.content,
    imageUrls = this.imageUrls,

    originNoteId = this.originNoteId, // 시음 노트 공유라면 ID가 들어가고, 일반글이면 null
    teaType = this.teaType?.name,
    rating = this.rating ?: 0,
    tags = this.tags,
    brewingSummary = this.brewingSummary,
    likeCount = this.stats.likeCount,
    bookmarkCount = this.stats.bookmarkCount,
    commentCount = this.stats.commentCount,
    viewCount = this.stats.viewCount,

    createdAt = this.createdAt
)

/**
 * 리스트 변환 헬퍼
 */
fun List<CommunityPostDto>.toCommunityDomainList() = this.map { it.toCommunityDomain() }