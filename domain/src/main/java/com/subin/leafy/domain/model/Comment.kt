package com.subin.leafy.domain.model

import java.util.Date

data class Comment(
    val id: String,
    val postId: String,
    val author: CommentAuthor,      // 작성자 정보 묶음
    val content: String,
    val createdAt: Long,            // Date -> Long 통일
    val isMine: Boolean = false
)

data class CommentAuthor(
    val id: String,
    val nickname: String,
    val profileImageUrl: String?
)