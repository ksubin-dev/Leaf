package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.CommentDTO
import com.subin.leafy.data.model.dto.CommunityPostDTO
import com.subin.leafy.data.model.dto.TeaMasterDTO
import com.subin.leafy.domain.model.Comment
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.TeaMaster
import java.util.Date

/**
 * DTO -> Domain 모델 변환
 */

fun CommunityPostDTO.toDomain() = CommunityPost(
    id = this._id,
    authorId = this.authorId,
    authorName = this.authorName,
    authorProfileUrl = this.authorProfileUrl,
    title = this.title,
    subtitle = this.subtitle,
    content = this.content,
    teaTag = this.teaTag,
    imageUrl = this.imageUrl ?: this.liquorUri ?: this.dryLeafUri ?: this.teawareUri,
    rating = this.rating,
    metaInfo = this.metaInfo,
    brewingSteps = this.brewingSteps,
    likeCount = this.likeCount,
    bookmarkCount = this.savedCount,
    commentCount = this.commentCount,
    viewCount = this.viewCount,
    isLiked = this.isLiked,
    isBookmarked = this.isSaved,
    createdAt = this.createdAt
)

fun TeaMasterDTO.toDomain() = TeaMaster(
    id = this.id,
    name = this.name,
    title = this.title,
    profileImageUrl = this.profileImageUrl,
    isFollowing = this.isFollowing
)



fun CommentDTO.toDomain() = Comment(
    id = this.id,
    postId = this.postId,
    authorId = this.authorId,
    authorName = this.authorName,
    authorProfileUrl = this.authorProfileUrl,
    content = this.content,
    createdAt = this.createdAt ?: Date()
)

/**
 * Domain 모델 -> Firestore용 Map 변환 (CUD 작업용)
 * Firestore 서버에 데이터를 저장할 때 사용할 키-값 쌍 정의
 */
fun CommunityPost.toFirestoreMap(): Map<String, Any?> = mapOf(
    "_id" to this.id,
    "authorId" to this.authorId,
    "authorName" to this.authorName,
    "authorProfileUrl" to this.authorProfileUrl,
    "title" to this.title,
    "subtitle" to this.subtitle,
    "content" to this.content,
    "teaTag" to this.teaTag,
    "imageUrl" to this.imageUrl,
    "rating" to this.rating,
    "metaInfo" to this.metaInfo,
    "brewingSteps" to this.brewingSteps,
    "likeCount" to this.likeCount,
    "bookmarkCount" to this.bookmarkCount,
    "viewCount" to this.viewCount,
    "isLiked" to this.isLiked,
    "isBookmarked" to this.isBookmarked,
    "createdAt" to this.createdAt
)

/**
 * 리스트 변환 유틸리티
 */
fun List<CommunityPostDTO>.toDomainList() = this.map { it.toDomain() }
fun List<TeaMasterDTO>.toDomainMasterList() = this.map { it.toDomain() }
fun List<CommentDTO>.toDomainCommentList() = this.map { it.toDomain() }