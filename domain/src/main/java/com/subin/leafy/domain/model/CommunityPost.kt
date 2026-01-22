package com.subin.leafy.domain.model

data class CommunityPost(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorProfileUrl: Int?, // 실제 앱에선 String(URL)이겠지만 지금은 리소스 ID 사용
    val title: String,
    val subtitle: String,
    val content: String,
    val teaTag: String,         // 예: "Oolong", "Green Tea"
    val imageRes: Int,
    val rating: Float,
    val metaInfo: String,       // 예: "대만 · 중배화"
    val brewingSteps: List<String>, // ["95℃", "3m", "5g"]
    val likeCount: Int,
    val savedCount: Int,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val createdAt: String,      // "2시간 전" 등
    val topComment: String? = null
)

// 2. 티 마스터 모델
data class TeaMaster(
    val id: String,
    val name: String,
    val title: String,          // 예: "녹차 & 말차 전문가"
    val profileImageRes: Int,
    val isFollowing: Boolean
)

// 3. 인기 태그 모델
data class CommunityTag(
    val id: String,
    val label: String,
    val isTrendingUp: Boolean = true
)