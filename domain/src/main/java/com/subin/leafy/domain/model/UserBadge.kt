package com.subin.leafy.domain.model

data class UserBadge(
    val id: String,          // 뱃지 고유 ID (예: "green_tea_lover_lv1")
    val name: String,        // 표시 이름 (예: "녹차 입문자")
    val description: String, // 설명 (예: "녹차를 5회 이상 기록했어요")
    val imageUrl: String,    // 뱃지 아이콘 URL
    val obtainedAt: Long     // 획득 날짜
)