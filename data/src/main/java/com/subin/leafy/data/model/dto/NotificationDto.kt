package com.subin.leafy.data.model.dto

import com.google.firebase.firestore.PropertyName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: String = "",
    val type: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderProfileUrl: String? = null,
    val targetPostId: String? = null,
    val message: String = "",
    @get:PropertyName("isRead")
    @set:PropertyName("isRead")
    var isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)