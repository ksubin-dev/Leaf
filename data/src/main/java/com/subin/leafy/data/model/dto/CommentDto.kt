package com.subin.leafy.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val id: String = "",
    val postId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorProfileUrl: String? = null,
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis()
)