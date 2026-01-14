package com.leafy.features.community.ui

data class CommunityCommentUi(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorProfileUrl: String?,
    val content: String,
    val timeAgo: String
)