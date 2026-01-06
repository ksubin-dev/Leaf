package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.BrewingNoteDTO
import com.subin.leafy.domain.model.*
import com.leafy.shared.ui.utils.LeafyTimeUtils
import java.util.Date
import java.time.Instant
import java.time.ZoneId

/**
 * DTO -> 상세 BrewingNote (Domain 계층으로 변환)
 */
fun BrewingNoteDTO.toDomainNote() = BrewingNote(
    id = this._id,
    ownerId = this.userId,
    teaInfo = TeaInfo(
        name = this.teaName, brand = this.teaBrand, type = this.teaType,
        leafStyle = this.leafStyle, processing = this.processing, grade = this.teaGrade
    ),
    condition = BrewingCondition(
        waterTemp = this.waterTemp, leafAmount = this.leafAmount,
        brewTime = this.brewTime, brewCount = this.brewCount, teaware = this.teaware
    ),
    evaluation = SensoryEvaluation(
        selectedTags = this.selectedTags.toSet(),
        sweetness = this.sweetness, sourness = this.sourness,
        bitterness = this.bitterness, saltiness = this.saltiness,
        umami = this.umami,
        bodyType = runCatching { BodyType.valueOf(this.bodyType) }.getOrDefault(BodyType.MEDIUM),
        finishLevel = this.finishLevel, memo = this.memo
    ),
    ratingInfo = RatingInfo(stars = this.stars, purchaseAgain = this.purchaseAgain),
    createdAt = Date(this.createdAt),
    context = NoteContext(
        dateTime = this.dateTime,
        weather = runCatching { WeatherType.valueOf(this.weather) }.getOrDefault(WeatherType.INDOOR),
        withPeople = this.withPeople, dryLeafUri = this.dryLeafUri,
        liquorUri = this.liquorUri, teawareUri = this.teawareUri, additionalUri = this.additionalUri
    ),
    likeCount = this.likeCount,
    bookmarkCount = this.bookmarkCount,
    viewCount = this.viewCount,
    commentCount = this.commentCount
)

/**
 * DTO -> 요약 BrewingRecord (마이페이지 캘린더용)
 */
fun BrewingNoteDTO.toDomainRecord() = BrewingRecord(
    id = this._id,
    dateString = this.dateTime.ifBlank {
        LeafyTimeUtils.formatLongToString(this.createdAt)
    },
    teaName = this.teaName,
    metaInfo = "${this.waterTemp} · ${this.brewTime} · ${this.brewCount}회 우림",
    rating = this.stars,
    imageUrl = liquorUri ?: dryLeafUri ?: teawareUri ?: additionalUri
)

/**
 * BrewingNote(Domain) -> BrewingNoteDTO (저장용)
 */
fun BrewingNote.toDTO() = BrewingNoteDTO(
    _id = this.id,
    userId = this.ownerId,
    teaName = this.teaInfo.name,
    teaBrand = this.teaInfo.brand,
    teaType = this.teaInfo.type,
    leafStyle = this.teaInfo.leafStyle,
    processing = this.teaInfo.processing,
    teaGrade = this.teaInfo.grade,
    waterTemp = this.condition.waterTemp,
    leafAmount = this.condition.leafAmount,
    brewTime = this.condition.brewTime,
    brewCount = this.condition.brewCount,
    teaware = this.condition.teaware,
    selectedTags = this.evaluation.selectedTags.toList(),
    sweetness = this.evaluation.sweetness,
    sourness = this.evaluation.sourness,
    bitterness = this.evaluation.bitterness,
    saltiness = this.evaluation.saltiness,
    umami = this.evaluation.umami,
    bodyType = this.evaluation.bodyType.name,
    finishLevel = this.evaluation.finishLevel,
    memo = this.evaluation.memo,
    stars = this.ratingInfo.stars,
    purchaseAgain = this.ratingInfo.purchaseAgain,
    dateTime = this.context.dateTime,
    weather = this.context.weather.name,
    withPeople = this.context.withPeople,
    dryLeafUri = this.context.dryLeafUri,
    liquorUri = this.context.liquorUri,
    teawareUri = this.context.teawareUri,
    additionalUri = this.context.additionalUri,
    createdAt = this.createdAt.time,
    likeCount = this.likeCount,
    bookmarkCount = this.bookmarkCount,
    viewCount = this.viewCount,
    commentCount = this.commentCount
)

/**
 * 상세 BrewingNote -> BrewingRecord(요약본) 변환
 */
fun BrewingNote.toRecord() = BrewingRecord(
    id = this.id,
    dateString = this.context.dateTime.ifBlank { LeafyTimeUtils.nowToString() },
    teaName = this.teaInfo.name,
    metaInfo = "${this.condition.waterTemp} · ${this.condition.brewTime} · ${this.condition.brewCount}회 우림",
    rating = this.ratingInfo.stars,
    imageUrl = this.context.liquorUri ?: this.context.dryLeafUri ?: this.context.teawareUri ?: this.context.additionalUri
)
/**
 * BrewingNoteDTO -> CommunityPost (커뮤니티용 도메인 모델로 변환)
 */
fun BrewingNoteDTO.toCommunityDomain() = CommunityPost(
    id = this._id,
    authorId = this.userId,
    authorName = "사용자", // TODO: 나중에 유저 닉네임을 가져올 로직이 있다면 연결
    authorProfileUrl = null,
    title = this.teaName,
    subtitle = this.teaBrand,
    content = this.memo,
    teaTag = this.teaType,
    imageUrl = this.liquorUri ?: this.dryLeafUri ?: this.teawareUri,
    rating = this.stars.toFloat(),
    metaInfo = "${this.waterTemp}℃ / ${this.leafAmount}g / ${this.brewTime}",
    brewingSteps = emptyList(),
    likeCount = this.likeCount,
    bookmarkCount = this.bookmarkCount,
    commentCount = this.commentCount,
    viewCount = this.viewCount,
    isLiked = false,
    isBookmarked = false,
    createdAt = this.dateTime.ifBlank {
        LeafyTimeUtils.formatLongToString(this.createdAt)
    }
)
/**
 * 리스트 변환 헬퍼
 */
fun List<BrewingNoteDTO>.toDomainRecordList() = this.map { it.toDomainRecord() }
fun List<BrewingNoteDTO>.toDomainListFromBrewing() = this.map { it.toCommunityDomain() }

