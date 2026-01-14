package com.subin.leafy.data.mapper

import com.subin.leafy.data.datasource.local.room.entity.NoteEntity
import com.subin.leafy.data.model.dto.BrewingNoteDto
import com.subin.leafy.domain.model.*

// =================================================================
// 1. Network DTO 매핑 (Firestore ↔ Domain)
// =================================================================

// [1] DTO -> Domain
fun BrewingNoteDto.toBrewingDomain() = BrewingNote(
    id = this.id,
    ownerId = this.ownerId,
    isPublic = this.isPublic,
    teaInfo = TeaInfo(
        name = this.teaName,
        brand = this.teaBrand,
        type = runCatching { TeaType.valueOf(this.teaType) }.getOrDefault(TeaType.ETC),
        origin = this.teaOrigin,
        leafStyle = this.teaLeafStyle,
        grade = this.teaGrade
    ),
    recipe = BrewingRecipe(
        waterTemp = this.waterTemp,
        leafAmount = this.leafAmount,
        waterAmount = this.waterAmount,
        brewTimeSeconds = this.brewTimeSeconds,
        infusionCount = this.infusionCount,
        teaware = this.teaware
    ),
    evaluation = SensoryEvaluation(
        flavorTags = this.flavorNotes.mapNotNull { tag ->
            runCatching { FlavorTag.valueOf(tag) }.getOrNull()
        },
        sweetness = this.sweetness,
        sourness = this.sourness,
        bitterness = this.bitterness,
        astringency = this.astringency,
        umami = this.umami,
        body = runCatching { BodyType.valueOf(this.bodyType) }.getOrDefault(BodyType.MEDIUM),
        finishLevel = this.finishLevel,
        memo = this.memo
    ),
    rating = RatingInfo(stars = this.stars, purchaseAgain = this.purchaseAgain),
    metadata = NoteMetadata(
        weather = this.weather?.let { runCatching { WeatherType.valueOf(it) }.getOrNull() },
        mood = this.mood,
        imageUrls = this.imageUrls
    ),
    stats = PostStatistics(
        likeCount = this.likeCount,
        bookmarkCount = this.bookmarkCount,
        commentCount = this.commentCount,
        viewCount = this.viewCount
    ),
    myState = PostSocialState(isLiked = false, isBookmarked = false),

    date = this.date,
    createdAt = this.createdAt
)

// [2] Domain -> DTO
fun BrewingNote.toDto() = BrewingNoteDto(
    id = this.id,
    ownerId = this.ownerId,
    isPublic = this.isPublic,

    // TeaInfo
    teaName = this.teaInfo.name,
    teaBrand = this.teaInfo.brand,
    teaType = this.teaInfo.type.name,
    teaOrigin = this.teaInfo.origin,
    teaLeafStyle = this.teaInfo.leafStyle,
    teaGrade = this.teaInfo.grade,

    // Recipe
    waterTemp = this.recipe.waterTemp,
    leafAmount = this.recipe.leafAmount,
    waterAmount = this.recipe.waterAmount,
    brewTimeSeconds = this.recipe.brewTimeSeconds,
    infusionCount = this.recipe.infusionCount,
    teaware = this.recipe.teaware,

    // Evaluation
    flavorNotes = this.evaluation.flavorTags.map { it.name },
    sweetness = this.evaluation.sweetness,
    sourness = this.evaluation.sourness,
    bitterness = this.evaluation.bitterness,
    astringency = this.evaluation.astringency,
    umami = this.evaluation.umami,
    bodyType = this.evaluation.body.name,
    finishLevel = this.evaluation.finishLevel,
    memo = this.evaluation.memo,

    // Metadata
    stars = this.rating.stars,
    purchaseAgain = this.rating.purchaseAgain,
    weather = this.metadata.weather?.name,
    mood = this.metadata.mood,
    imageUrls = this.metadata.imageUrls,

    // Stats
    likeCount = this.stats.likeCount,
    bookmarkCount = this.stats.bookmarkCount,
    commentCount = this.stats.commentCount,
    viewCount = this.stats.viewCount,

    date = this.date,
    createdAt = this.createdAt
)


// =================================================================
// 2. Local Entity 매핑 (Room ↔ Domain)
// =================================================================

// [3] Entity -> Domain
fun NoteEntity.toDomain() = BrewingNote(
    id = this.id,
    ownerId = this.ownerId,
    isPublic = this.isPublic,

    teaInfo = TeaInfo(
        name = this.teaName,
        brand = this.teaBrand,
        type = runCatching { TeaType.valueOf(this.teaType) }.getOrDefault(TeaType.ETC),
        origin = this.teaOrigin,
        leafStyle = this.teaLeafStyle,
        grade = this.teaGrade
    ),
    recipe = BrewingRecipe(
        waterTemp = this.waterTemp,
        leafAmount = this.leafAmount,
        waterAmount = this.waterAmount,
        brewTimeSeconds = this.brewTimeSeconds,
        infusionCount = this.infusionCount,
        teaware = this.teaware
    ),
    evaluation = SensoryEvaluation(
        flavorTags = this.flavorNotes.mapNotNull { tag ->
            runCatching { FlavorTag.valueOf(tag) }.getOrNull()
        },
        sweetness = this.sweetness,
        sourness = this.sourness,
        bitterness = this.bitterness,
        astringency = this.astringency,
        umami = this.umami,
        body = runCatching { BodyType.valueOf(this.bodyType) }.getOrDefault(BodyType.MEDIUM),
        finishLevel = this.finishLevel,
        memo = this.memo
    ),
    rating = RatingInfo(stars = this.stars, purchaseAgain = this.purchaseAgain ?: false),
    metadata = NoteMetadata(
        weather = this.weather?.let { runCatching { WeatherType.valueOf(it) }.getOrNull() },
        mood = this.mood,
        imageUrls = this.imageUrls
    ),
    stats = PostStatistics(
        likeCount = this.likeCount,
        bookmarkCount = this.bookmarkCount,
        commentCount = this.commentCount,
        viewCount = this.viewCount
    ),
    myState = PostSocialState(isLiked = false, isBookmarked = false),

    createdAt = this.createdAt,
    date = this.date,
)

// [4] Domain -> Entity
fun BrewingNote.toEntity() = NoteEntity(
    id = this.id,
    ownerId = this.ownerId,
    isPublic = this.isPublic,

    teaName = this.teaInfo.name,
    teaBrand = this.teaInfo.brand,
    teaType = this.teaInfo.type.name,
    teaOrigin = this.teaInfo.origin,
    teaLeafStyle = this.teaInfo.leafStyle,
    teaGrade = this.teaInfo.grade,

    waterTemp = this.recipe.waterTemp,
    leafAmount = this.recipe.leafAmount,
    waterAmount = this.recipe.waterAmount,
    brewTimeSeconds = this.recipe.brewTimeSeconds,
    infusionCount = this.recipe.infusionCount,
    teaware = this.recipe.teaware,

    flavorNotes = this.evaluation.flavorTags.map { it.name },
    sweetness = this.evaluation.sweetness,
    sourness = this.evaluation.sourness,
    bitterness = this.evaluation.bitterness,
    astringency = this.evaluation.astringency,
    umami = this.evaluation.umami,
    bodyType = this.evaluation.body.name,
    finishLevel = this.evaluation.finishLevel,
    memo = this.evaluation.memo,

    stars = this.rating.stars,
    purchaseAgain = this.rating.purchaseAgain,
    weather = this.metadata.weather?.name,
    mood = this.metadata.mood,
    imageUrls = this.metadata.imageUrls,

    likeCount = this.stats.likeCount,
    bookmarkCount = this.stats.bookmarkCount,
    commentCount = this.stats.commentCount,
    viewCount = this.stats.viewCount,

    date = this.date,
    createdAt = this.createdAt
)


// =================================================================
// 3. Helper Functions (리스트 변환 & UI/커뮤니티 모델)
// =================================================================

// 리스트 변환 헬퍼
// DTO 리스트 -> Domain 리스트
fun List<BrewingNoteDto>.toDomainList() = this.map { it.toBrewingDomain() }

// Entity 리스트 -> Domain 리스트 (LocalNoteDataSourceImpl에서 사용)
fun List<NoteEntity>.toNoteDomainList() = this.map { it.toDomain() }

// [UI용] 요약 정보(Record)로 변환
fun BrewingNoteDto.toRecord() = BrewingRecord(
    id = this.id,
    teaName = this.teaName,
    metaInfo = "${this.waterTemp}℃ · ${this.brewTimeSeconds}s · ${this.infusionCount}회",
    imageUrl = this.imageUrls.firstOrNull(),
    createdAt = this.createdAt
)

fun NoteEntity.toRecord() = BrewingRecord(
    id = this.id,
    teaName = this.teaName,
    metaInfo = "${this.waterTemp}℃ · ${this.brewTimeSeconds}s · ${this.infusionCount}회",
    imageUrl = this.imageUrls.firstOrNull(),
    createdAt = this.createdAt
)

// 리스트 변환 헬퍼 (UI)
fun List<BrewingNoteDto>.toRecordListFromDto() = this.map { it.toRecord() }
fun List<NoteEntity>.toRecordListFromEntity() = this.map { it.toRecord() }


// DTO -> CommunityPost
fun BrewingNoteDto.toCommunityPost(
    authorName: String,
    authorProfile: String?,
    isFollowingAuthor: Boolean = false
) = CommunityPost(
    id = "POST_${this.id}",
    author = PostAuthor(
        id = this.ownerId,
        nickname = authorName,
        profileImageUrl = authorProfile,
        isFollowing = isFollowingAuthor
    ),
    imageUrls = this.imageUrls,
    title = "${this.teaBrand} ${this.teaName}",
    content = this.memo,
    originNoteId = this.id,
    teaType = runCatching { TeaType.valueOf(this.teaType) }.getOrDefault(TeaType.ETC),
    rating = this.stars,
    tags = this.flavorNotes,
    brewingSummary = "${this.waterTemp}℃ · ${this.brewTimeSeconds}s · ${this.leafAmount}g",

    stats = PostStatistics(
        likeCount = this.likeCount,
        commentCount = this.commentCount,
        bookmarkCount = this.bookmarkCount,
        viewCount = this.viewCount
    ),
    myState = PostSocialState(isLiked = false, isBookmarked = false),
    createdAt = this.createdAt,
    topComment = null
)