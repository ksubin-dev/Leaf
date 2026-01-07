package com.subin.leafy.domain.model

data class BrewingRecord(
    val id: String,           // 클릭 시 상세 페이지 이동용 ID
    val teaName: String,      // 카드 메인 타이틀 (예: 목책 철관음)
    val metaInfo: String,     // 설명 요약 (예: "대만 · 중배화" 또는 "오후 2:30")
    val imageUrl: String? = null, // 카드 오른쪽 사진
    val createdAt: Long       // UI에는 안 보여도 '최신순 정렬'을 위해 꼭 필요한 데이터
)