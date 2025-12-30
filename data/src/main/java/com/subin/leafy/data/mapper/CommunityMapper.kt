package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.CommunityPostDTO
import com.subin.leafy.data.model.dto.CommunityTagDTO
import com.subin.leafy.data.model.dto.TeaMasterDTO
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.CommunityTag
import com.subin.leafy.domain.model.TeaMaster

/**
 * DTO -> Domain 모델 변환 (Data Layer -> Domain Layer)
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
    imageUrl = this.imageUrl,
    rating = this.rating,
    metaInfo = this.metaInfo,
    brewingSteps = this.brewingSteps,
    likeCount = this.likeCount,
    savedCount = this.savedCount,
    isLiked = this.isLiked,
    isSaved = this.isSaved,
    createdAt = this.createdAt
)

// TeaMaster 변환 추가
fun TeaMasterDTO.toDomain() = TeaMaster(
    id = this.id,
    name = this.name,
    title = this.title,
    profileImageUrl = this.profileImageUrl,
    isFollowing = this.isFollowing
)

// CommunityTag 변환 추가
fun CommunityTagDTO.toDomain() = CommunityTag(
    id = this.id,
    label = this.label,
    isTrendingUp = this.isTrendingUp
)

/**
 * Domain 모델 -> Firestore용 Map 변환 (저장용)
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
    "savedCount" to this.savedCount,
    "isLiked" to this.isLiked,
    "isSaved" to this.isSaved,
    "createdAt" to this.createdAt
)

/**
 * 리스트 변환 유틸리티
 */
// 리스트 변환 유틸리티들
fun List<CommunityPostDTO>.toDomainList() = this.map { it.toDomain() }
fun List<TeaMasterDTO>.toDomainMasterList() = this.map { it.toDomain() }
fun List<CommunityTagDTO>.toDomainTagList() = this.map { it.toDomain() }