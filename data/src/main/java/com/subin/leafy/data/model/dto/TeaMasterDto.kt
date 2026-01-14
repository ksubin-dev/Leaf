package com.subin.leafy.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class TeaMasterDto(
    val id: String = "",
    val nickname: String = "",
    val title: String = "",            // 예: "티 소믈리에 1급", "말차 장인"
    val profileImageUrl: String? = null,

    //신뢰도 지표
    val followerCount: Int = 0,

    // 전문 분야 (Enum List -> String List로 저장)
    // 예: ["GREEN", "MATCH"]
    val expertTypes: List<String> = emptyList()
)