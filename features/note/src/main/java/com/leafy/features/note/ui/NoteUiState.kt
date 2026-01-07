package com.leafy.features.note.ui

import com.subin.leafy.domain.model.*

data class NoteUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false,

    val ownerId: String = "",
    // 1. Tasting Context
    val dateTime: String = "",
    val weather: WeatherType = WeatherType.INDOOR,
    val withPeople: String = "",
    val dryLeafUri: String? = null,
    val liquorUri: String? = null,
    val teawareUri: String? = null,
    val additionalUri: String? = null,

    // 2. Tea Information
    val teaName: String = "",
    val brandName: String = "",
    val teaType: String = "Black",
    val leafStyle: String = "Loose Leaf",
    val leafProcessing: String = "Whole Leaf",
    val teaGrade: String = "OP",

    // 3. Brewing Condition
    val waterTemp: String = "",
    val leafAmount: String = "",
    val brewTime: String = "",
    val brewCount: String = "1",
    val teaware: String = "찻주전자",

    // 4. Sensory Evaluation
    val selectedTags: Set<String> = emptySet(),
    val sweetness: Float = 0f,
    val sourness: Float = 0f,
    val bitterness: Float = 0f,
    val saltiness: Float = 0f,
    val umami: Float = 0f,
    val bodyType: BodyType = BodyType.MEDIUM,
    val finishLevel: Float = 0.5f,
    val memo: String = "",

    // 5. Final Rating
    val rating: Int = 0,
    val purchaseAgain: Boolean? = null,

    val likeCount: Int = 0,
    val bookmarkCount: Int = 0,
    val viewCount: Int = 0,
    val commentCount: Int = 0,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val createdAt: Long = 0L,
) {
    val isError: Boolean get() = errorMessage != null
    val socialSummary: String
        get() = "조회 $viewCount · 좋아요 $likeCount · 댓글 $commentCount"
    private val hasAtLeastOnePhoto: Boolean
        get() = !dryLeafUri.isNullOrBlank() ||
                !liquorUri.isNullOrBlank() ||
                !teawareUri.isNullOrBlank() ||
                !additionalUri.isNullOrBlank()

    val canSave: Boolean
        get() = teaName.isNotBlank() && !isLoading && hasAtLeastOnePhoto


}

/**
 * Domain -> UI 변환
 */
fun BrewingNote.toUiState(): NoteUiState {
    return NoteUiState(
        ownerId = this.ownerId,
        dateTime = context.dateTime,
        weather = context.weather,
        withPeople = context.withPeople ?: "",
        dryLeafUri = context.dryLeafUri,
        liquorUri = context.liquorUri,
        teawareUri = context.teawareUri,
        additionalUri = context.additionalUri,
        teaName = teaInfo.name,
        brandName = teaInfo.brand,
        teaType = teaInfo.type,
        leafStyle = teaInfo.leafStyle,
        leafProcessing = teaInfo.processing,
        teaGrade = teaInfo.grade,
        waterTemp = condition.waterTemp,
        leafAmount = condition.leafAmount,
        brewTime = condition.brewTime,
        brewCount = condition.brewCount,
        teaware = condition.teaware,
        selectedTags = evaluation.selectedTags,
        sweetness = evaluation.sweetness,
        sourness = evaluation.sourness,
        bitterness = evaluation.bitterness,
        saltiness = evaluation.saltiness,
        umami = evaluation.umami,
        bodyType = evaluation.bodyType,
        finishLevel = evaluation.finishLevel,
        memo = evaluation.memo,
        rating = ratingInfo.stars,
        purchaseAgain = ratingInfo.purchaseAgain,
        likeCount = this.likeCount,
        bookmarkCount = this.bookmarkCount,
        viewCount = this.viewCount,
        commentCount = this.commentCount,
        isLiked = this.isLiked,
        isBookmarked = this.isBookmarked,
        createdAt = this.createdAt.time,
        isLoading = false
    )
}

/**
 * UI -> Domain 변환
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
        ),
        createdAt = if (this.createdAt > 0L) java.util.Date(this.createdAt) else java.util.Date(),
        likeCount = this.likeCount,
        bookmarkCount = this.bookmarkCount,
        viewCount = this.viewCount,
        commentCount = this.commentCount,
        isLiked = this.isLiked,
        isBookmarked = this.isBookmarked
    )
}

/**
 * CommunityPost (도메인) -> NoteUiState (UI) 변환 함수 추가
 */
fun CommunityPost.toUiState(): NoteUiState {
    return NoteUiState(
        ownerId = this.authorId,
        teaName = this.title,
        brandName = this.subtitle,
        memo = this.content,
        teaType = this.teaTag,
        dryLeafUri = this.imageUrl,

        // 소셜 정보 반영
        likeCount = this.likeCount,
        bookmarkCount = this.bookmarkCount,
        viewCount = this.viewCount,
        commentCount = this.commentCount,
        isLiked = this.isLiked,
        isBookmarked = this.isBookmarked,

        waterTemp = this.brewingSteps.getOrNull(0) ?: "",
        brewTime = this.brewingSteps.getOrNull(1) ?: "",
        leafAmount = this.brewingSteps.getOrNull(2) ?: "",

        rating = this.rating.toInt(),
        isLoading = false
    )
}
