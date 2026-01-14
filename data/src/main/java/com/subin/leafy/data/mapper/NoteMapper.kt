package com.subin.leafy.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.subin.leafy.data.model.dto.BrewingNoteDTO
import com.subin.leafy.domain.model.*
import com.subin.leafy.domain.model.id.NoteId
import com.subin.leafy.domain.model.id.UserId
import java.time.Instant
import java.time.ZoneId
import java.util.Date

/**
 * DTO -> 상세 BrewingNote (상세 화면용)
 */
fun BrewingNoteDTO.toDomainNote() = BrewingNote(
    id = NoteId(this._id),
    ownerId = UserId(this.userId),
    teaInfo = TeaInfo(
        name = this.teaName,
        brand = this.teaBrand,
        type = this.teaType,
        leafStyle = this.leafStyle,
        processing = this.processing,
        grade = this.teaGrade
    ),
    condition = BrewingCondition(
        waterTemp = this.waterTemp,
        leafAmount = this.leafAmount,
        brewTime = this.brewTime,
        brewCount = this.brewCount,
        teaware = this.teaware
    ),
    evaluation = SensoryEvaluation(
        selectedTags = this.selectedTags.toSet(),
        sweetness = this.sweetness,
        sourness = this.sourness,
        bitterness = this.bitterness,
        saltiness = this.saltiness,
        umami = this.umami,
        bodyType = runCatching { BodyType.valueOf(this.bodyType) }.getOrDefault(BodyType.MEDIUM),
        finishLevel = this.finishLevel,
        memo = this.memo
    ),
    ratingInfo = RatingInfo(
        stars = this.stars,
        purchaseAgain = this.purchaseAgain
    ),
    context = NoteContext(
        weather = runCatching { WeatherType.valueOf(this.weather) }.getOrDefault(WeatherType.INDOOR),
        withPeople = this.withPeople,
        dryLeafUri = this.dryLeafUri,
        liquorUri = this.liquorUri,
        teawareUri = this.teawareUri,
        additionalUri = this.additionalUri
    ),
    createdAt = Date(this.createdAt)
)

/**
 * DTO -> 요약 BrewingRecord (마이페이지 캘린더용)
 */
@RequiresApi(Build.VERSION_CODES.O)
fun BrewingNoteDTO.toDomainRecord() = BrewingRecord(
    id = NoteId(this._id),
    date = Instant.ofEpochMilli(this.createdAt)
        .atZone(ZoneId.systemDefault())
        .toLocalDate(),
    teaName = this.teaName,
    metaInfo = "${this.waterTemp} · ${this.brewTime} · ${this.brewCount}회 우림",
    rating = this.stars
)
/**
 * BrewingNote(Domain) -> BrewingNoteDTO (Data Layer 저장용)
 */
fun BrewingNote.toDTO() = BrewingNoteDTO(
    _id = this.id.value,
    userId = this.ownerId.value,
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
fun BrewingNote.toFirestoreMap(): Map<String, Any?> = mapOf(
    "_id" to this.id.value,
    "userId" to this.ownerId.value,
    "teaName" to this.teaInfo.name,
    "teaBrand" to this.teaInfo.brand,
    "teaType" to this.teaInfo.type,
    "leafStyle" to this.teaInfo.leafStyle,
    "processing" to this.teaInfo.processing,
    "teaGrade" to this.teaInfo.grade,
    "waterTemp" to this.condition.waterTemp,
    "leafAmount" to this.condition.leafAmount,
    "brewTime" to this.condition.brewTime,
    "brewCount" to this.condition.brewCount,
    "teaware" to this.condition.teaware,
    "selectedTags" to this.evaluation.selectedTags.toList(),
    "sweetness" to this.evaluation.sweetness,
    "sourness" to this.evaluation.sourness,
    "bitterness" to this.evaluation.bitterness,
    "saltiness" to this.evaluation.saltiness,
    "umami" to this.evaluation.umami,
    "bodyType" to this.evaluation.bodyType.name,
    "finishLevel" to this.evaluation.finishLevel,
    "memo" to this.evaluation.memo,
    "stars" to this.ratingInfo.stars,
    "purchaseAgain" to this.ratingInfo.purchaseAgain,
    "weather" to this.context.weather.name,
    "withPeople" to this.context.withPeople,
    "dryLeafUri" to this.context.dryLeafUri,
    "liquorUri" to this.context.liquorUri,
    "teawareUri" to this.context.teawareUri,
    "additionalUri" to this.context.additionalUri,
    "createdAt" to this.createdAt.time
)
/**
 * BrewingNote(상세) -> BrewingRecord(요약)
 * mockDb(도메인 모델 리스트)를 캘린더용으로 변환할 때 사용합니다.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun BrewingNote.toRecord() = BrewingRecord(
    id = this.id,
    date = this.createdAt.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate(),
    teaName = this.teaInfo.name,
    metaInfo = "${this.condition.waterTemp} · ${this.condition.brewTime} · ${this.condition.brewCount}회 우림",
    rating = this.ratingInfo.stars
)

/**
 * 리스트 변환 유틸리티
 */
@RequiresApi(Build.VERSION_CODES.O)
fun List<BrewingNoteDTO>.toDomainRecordList() = this.map { it.toDomainRecord() }