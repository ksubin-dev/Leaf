package com.subin.leafy.data.model.dto

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class CommentDTO(
    val id: String = "",
    val postId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorProfileUrl: String? = null,
    val content: String = "",
    @ServerTimestamp val createdAt: Date? = null
)