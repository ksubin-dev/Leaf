package com.leafy.features.note.ui

import com.subin.leafy.domain.model.*

fun BrewingNote.toUiState(): NoteUiState {
    return NoteUiState(
        // 1. Tasting Context
        dateTime = context.dateTime,
        weather = context.weather,
        withPeople = context.withPeople ?: "",
        dryLeafUri = context.dryLeafUri,
        liquorUri = context.liquorUri,
        teawareUri = context.teawareUri,
        additionalUri = context.additionalUri,

        // 2. Tea Information
        teaName = teaInfo.name,
        brandName = teaInfo.brand ?: "",
        teaType = teaInfo.type ?: "Black",
        leafStyle = teaInfo.leafStyle ?: "Loose Leaf",
        leafProcessing = teaInfo.processing ?: "Whole Leaf",
        teaGrade = teaInfo.grade ?: "",

        // 3. Brewing Condition
        waterTemp = condition.waterTemp,
        leafAmount = condition.leafAmount,
        brewTime = condition.brewTime,
        brewCount = condition.brewCount,
        teaware = condition.teaware,

        // 4. Sensory Evaluation
        selectedTags = evaluation.selectedTags,
        sweetness = evaluation.sweetness,
        sourness = evaluation.sourness,
        bitterness = evaluation.bitterness,
        saltiness = evaluation.saltiness,
        umami = evaluation.umami,
        bodyType = evaluation.bodyType,
        finishLevel = evaluation.finishLevel,
        memo = evaluation.memo ?: "",

        // 5. Final Rating
        rating = ratingInfo.stars,
        purchaseAgain = ratingInfo.purchaseAgain,

        isLoading = false
    )
}

/**
 * NoteUiState (UI) -> BrewingNote (Domain)
 * 사용자가 UI에서 입력한 상태를 저장 가능한 도메인 모델로 변환합니다.
 */
fun NoteUiState.toDomain(ownerId: String, id: String): BrewingNote {
    return BrewingNote(
        id = id,
        ownerId = ownerId,
        teaInfo = TeaInfo(
            name = teaName,
            brand = brandName,
            type = teaType,
            leafStyle = leafStyle,
            processing = leafProcessing,
            grade = teaGrade
        ),
        condition = BrewingCondition(
            waterTemp = waterTemp,
            leafAmount = leafAmount,
            brewTime = brewTime,
            brewCount = brewCount,
            teaware = teaware
        ),
        evaluation = SensoryEvaluation(
            selectedTags = selectedTags,
            sweetness = sweetness,
            sourness = sourness,
            bitterness = bitterness,
            saltiness = saltiness,
            umami = umami,
            bodyType = bodyType,
            finishLevel = finishLevel,
            memo = memo
        ),
        ratingInfo = RatingInfo(
            stars = rating,
            purchaseAgain = purchaseAgain
        ),
        context = NoteContext(
            dateTime = dateTime,
            weather = weather,
            withPeople = withPeople,
            dryLeafUri = dryLeafUri,
            liquorUri = liquorUri,
            teawareUri = teawareUri,
            additionalUri = additionalUri
        )
    )
}