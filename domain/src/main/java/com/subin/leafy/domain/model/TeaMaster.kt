package com.subin.leafy.domain.model

data class TeaMaster(
    val id: String,
    val nickname: String,
    val title: String,           // 예: "말차 & 홍차 소믈리에"
    val profileImageUrl: String?,
    val isFollowing: Boolean = false,

    // 이 마스터를 팔로우하는 사람 수 (신뢰도 지표)
    val followerCount: Int = 0,

    //  이 마스터가 주로 활동하는 차 카테고리 (UI 칩으로 활용)
    val expertTypes: List<TeaType> = emptyList()
)