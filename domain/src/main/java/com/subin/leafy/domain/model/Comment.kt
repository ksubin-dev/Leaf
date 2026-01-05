package com.subin.leafy.domain.model

import java.util.Date

data class Comment(
    val id: String,
    val postId: String,
    val authorId: String,
    val authorName: String,
    val authorProfileUrl: String?,
    val content: String,
    val createdAt: Date = Date()
)