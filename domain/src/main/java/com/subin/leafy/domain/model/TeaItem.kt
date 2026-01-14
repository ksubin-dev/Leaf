package com.subin.leafy.domain.model

data class TeaItem(
    val id: String,
    val ownerId: String,
    val name: String,           // 차 이름
    val brand: String,          // 브랜드
    val type: TeaType,
    val origin: String = "",    // 산지
    val leafStyle: String = "", // 찻잎 형태
    val grade: String = "",     // 등급
    val imageUrl: String? = null,
    val isFavorite: Boolean = false, // 즐겨찾기 (보관함 상단 노출용)
    val stockQuantity: String = "",  // 잔여량 (예: "50g" 또는 "3개 남음")
    val memo: String = "",            // 구매처나 간단한 메모
    val createdAt: Long = System.currentTimeMillis()
)