package com.leafy.features.community.ui.component

/**
 * 커뮤니티의 모든 노트를 표현하는 통합 UI 모델
 */
data class ExploreNoteUi(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String? = null,
    val rating: Float = 0f,
    val savedCount: Int = 0,
    val likeCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,

    // 작성자 정보
    val authorName: String? = null,
    val authorProfileUrl: String? = null,
    val timeAgo: String = "",

    // 상세 정보 (Following 탭 및 상세용)
    val metaInfo: String = "",              // "대만 · 중배화" 등
    val description: String = "",           // 짧은 본문
    val brewingChips: List<String> = emptyList(),
    val reviewLabel: String = "",           // "Rebrew 가능" 등
    val comment: String = "",               // 베스트 댓글 등
    val likerProfileUrls: List<String> = emptyList() // 좋아요 누른 사람들 URL 리스트
)

/**
 * 티 마스터 UI 모델
 */
data class ExploreTeaMasterUi(
    val id: String,
    val name: String,
    val title: String,
    val profileImageUrl: String? = null,
    val isFollowing: Boolean = false
)

/**
 * 태그 UI 모델
 */
data class ExploreTagUi(
    val id: String = "",
    val label: String,
    val isTrendingUp: Boolean = true
)