package com.subin.leafy.domain.model

import com.subin.leafy.domain.model.id.NoteId
import java.time.LocalDate

data class BrewingRecord(
    val id: NoteId,               // BrewingNote의 id와 매칭
    val date: LocalDate,        // 캘린더 정렬용
    val teaName: String,        // TeaInfo.name에서 가져옴
    val metaInfo: String,       // BrewingCondition 요약 (예: "3회 우림 · 95°C")
    val rating: Int             // RatingInfo.stars에서 가져옴
)