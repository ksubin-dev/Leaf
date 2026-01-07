package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.BrewingNoteDto
import com.subin.leafy.domain.model.*

// [1] DTO -> 상세 BrewingNote (불러오기)
fun BrewingNoteDto.toBrewingDomain() = BrewingNote(
    id = this.id,
    ownerId = this.userId,
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
        flavorNotes = this.flavorNotes.toSet(),
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
        weather = runCatching { WeatherType.valueOf(this.weather) }.getOrDefault(WeatherType.CLOUDY),
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
    createdAt = this.createdAt
)

// [2] DTO -> 요약 BrewingRecord (마이페이지/캘린더용)
fun BrewingNoteDto.toRecord() = BrewingRecord(
    id = this.id,
    teaName = this.teaName,
    metaInfo = "${this.waterTemp}℃ · ${this.brewTimeSeconds}s · ${this.infusionCount}회",
    imageUrl = this.imageUrls.firstOrNull(),
    createdAt = this.createdAt
)

// [3] DTO -> CommunityPost (커뮤니티 공유용)
fun BrewingNoteDto.toCommunityPost(
    authorName: String,
    authorProfile: String?,
    isFollowingAuthor: Boolean = false
) = CommunityPost(
    id = "POST_${this.id}",
    author = PostAuthor(
        id = this.userId,
        nickname = authorName,
        profileImageUrl = authorProfile,
        isFollowing = isFollowingAuthor
    ),
    title = "${this.teaBrand} ${this.teaName}", // 이미지 속 "농평오룡차" 등 제목
    content = this.memo,
    imageUrls = this.imageUrls,
    originNoteId = this.id,
    teaType = runCatching { TeaType.valueOf(this.teaType) }.getOrDefault(TeaType.ETC),
    rating = this.stars,
    tags = this.flavorNotes,
    brewingSummary = "${this.waterTemp}℃ · ${this.brewTimeSeconds}s · ${this.leafAmount}g · ${this.infusionCount}회",
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

// [4] Domain -> DTO (서버 저장용 - 전체 필드 매핑)
fun BrewingNote.toDto() = BrewingNoteDto(
    id = this.id,
    userId = this.ownerId,

    // 1. TeaInfo
    teaName = this.teaInfo.name,
    teaBrand = this.teaInfo.brand,
    teaType = this.teaInfo.type.name,
    teaOrigin = this.teaInfo.origin,
    teaLeafStyle = this.teaInfo.leafStyle,
    teaGrade = this.teaInfo.grade,

    // 2. BrewingRecipe
    waterTemp = this.recipe.waterTemp,
    leafAmount = this.recipe.leafAmount,
    waterAmount = this.recipe.waterAmount,
    brewTimeSeconds = this.recipe.brewTimeSeconds,
    infusionCount = this.recipe.infusionCount,
    teaware = this.recipe.teaware,

    // 3. SensoryEvaluation
    flavorNotes = this.evaluation.flavorNotes.toList(),
    sweetness = this.evaluation.sweetness,
    sourness = this.evaluation.sourness,
    bitterness = this.evaluation.bitterness,
    astringency = this.evaluation.astringency,
    umami = this.evaluation.umami,
    bodyType = this.evaluation.body.name,
    finishLevel = this.evaluation.finishLevel,
    memo = this.evaluation.memo,

    // 4. Rating & Metadata
    stars = this.rating.stars,
    purchaseAgain = this.rating.purchaseAgain,
    weather = this.metadata.weather.name,
    mood = this.metadata.mood,
    imageUrls = this.metadata.imageUrls,

    // 5. Social Interaction (현재 수치 보존)
    likeCount = this.stats.likeCount,
    bookmarkCount = this.stats.bookmarkCount,
    commentCount = this.stats.commentCount,
    viewCount = this.stats.viewCount,

    createdAt = this.createdAt
)

fun List<BrewingNoteDto>.toDomainList() = this.map { it.toBrewingDomain() }

// 2. DTO 리스트 -> 요약 리스트 (마이페이지/캘린더 뷰 로드할 때)
fun List<BrewingNoteDto>.toRecordList() = this.map { it.toRecord() }

// 3. 도메인 리스트 -> DTO 리스트
fun List<BrewingNote>.toDtoList() = this.map { it.toDto() }

/**
 * 💡 팁: 커뮤니티용 리스트 변환은 보통 Repository나 UseCase에서
 * 각 게시물마다 작성자 정보를 가져온 뒤 개별적으로 .toCommunityPost()를 호출합니다.
 * 따라서 여기서는 위 두 가지 기본 헬퍼만 유지하는 게 가장 깔끔합니다.
 */
