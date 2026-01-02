package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.BrewingNoteDTO
import com.subin.leafy.domain.model.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * 날짜 변환을 위한 유틸리티 객체
 */
object TimeUtils {
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun toDateString(date: Date): String = dateFormatter.format(date)

    fun fromDateString(dateString: String): Date {
        return runCatching { dateFormatter.parse(dateString) }.getOrNull() ?: Date()
    }
}

/**
 * DTO -> 상세 BrewingNote (상세 화면용)
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
    context = NoteContext(
        dateTime = this.dateTime,
        weather = runCatching { WeatherType.valueOf(this.weather) }.getOrDefault(WeatherType.INDOOR),
        withPeople = this.withPeople, dryLeafUri = this.dryLeafUri,
        liquorUri = this.liquorUri, teawareUri = this.teawareUri, additionalUri = this.additionalUri
    ),
    createdAt = Date(this.createdAt)
)

/**
 * DTO -> 요약 BrewingRecord (마이페이지 캘린더용)
 */
fun BrewingNoteDTO.toDomainRecord() = BrewingRecord(
    id = this._id,
    dateString = this.dateTime.ifBlank { TimeUtils.toDateString(Date(this.createdAt)) },
    teaName = this.teaName,
    metaInfo = "${this.waterTemp} · ${this.brewTime} · ${this.brewCount}회 우림",
    rating = this.stars,
    imageUrl = liquorUri ?: dryLeafUri ?: teawareUri ?: additionalUri
)

/**
 * BrewingNote(Domain) -> BrewingNoteDTO (Data Layer 저장용)
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
    createdAt = this.createdAt.time
)

/**
 * 상세 BrewingNote -> Firestore용 Map (저장용)
 */
fun BrewingNote.toFirestoreMap(): Map<String, Any?> = this.toDTO().let { dto ->
    mapOf(
        "_id" to dto._id, "userId" to dto.userId, "teaName" to dto.teaName,
        "teaBrand" to dto.teaBrand, "teaType" to dto.teaType, "leafStyle" to dto.leafStyle,
        "processing" to dto.processing, "teaGrade" to dto.teaGrade, "waterTemp" to dto.waterTemp,
        "leafAmount" to dto.leafAmount, "brewTime" to dto.brewTime, "brewCount" to dto.brewCount,
        "teaware" to dto.teaware, "selectedTags" to dto.selectedTags, "sweetness" to dto.sweetness,
        "sourness" to dto.sourness, "bitterness" to dto.bitterness, "saltiness" to dto.saltiness,
        "umami" to dto.umami, "bodyType" to dto.bodyType, "finishLevel" to dto.finishLevel,
        "memo" to dto.memo, "stars" to dto.stars, "purchaseAgain" to dto.purchaseAgain,
        "dateTime" to dto.dateTime, "weather" to dto.weather, "withPeople" to dto.withPeople,
        "dryLeafUri" to dto.dryLeafUri, "liquorUri" to dto.liquorUri, "teawareUri" to dto.teawareUri,
        "additionalUri" to dto.additionalUri, "createdAt" to dto.createdAt
    )
}

/**
 * 상세 BrewingNote -> BrewingRecord(요약)
 */
fun BrewingNote.toRecord() = BrewingRecord(
    id = this.id,
    dateString = this.context.dateTime.ifBlank { TimeUtils.toDateString(this.createdAt) },
    teaName = this.teaInfo.name,
    metaInfo = "${this.condition.waterTemp} · ${this.condition.brewTime} · ${this.condition.brewCount}회 우림",
    rating = this.ratingInfo.stars
)

/**
 * 리스트 변환 유틸리티
 */
fun List<BrewingNoteDTO>.toDomainRecordList() = this.map { it.toDomainRecord() }