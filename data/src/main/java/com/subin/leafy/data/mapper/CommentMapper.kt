package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.CommentDto
import com.subin.leafy.domain.model.Comment
import com.subin.leafy.domain.model.CommentAuthor

// 1. DTO -> Domain (불러오기)
fun CommentDto.toCommentDomain(currentUserId: String? = null) = Comment(
    id = this.id,
    postId = this.postId,
    author = CommentAuthor(
        id = this.authorId,
        nickname = this.authorName,
        profileImageUrl = this.authorProfileUrl
    ),
    content = this.content,
    createdAt = this.createdAt,
    isMine = currentUserId != null && this.authorId == currentUserId
)

// 2. Domain -> DTO (저장하기)
fun Comment.toDto() = CommentDto(
    id = this.id,
    postId = this.postId,
    authorId = this.author.id,
    authorName = this.author.nickname,
    authorProfileUrl = this.author.profileImageUrl,
    content = this.content,
    createdAt = this.createdAt
)

/**
 * 리스트 변환 헬퍼
 */
fun List<CommentDto>.toCommentDomainList(currentUserId: String? = null) =
    this.map { it.toCommentDomain(currentUserId) }