package com.leafy.features.community.ui

import com.leafy.features.community.ui.component.ExploreFollowingNoteUi
import com.leafy.features.community.ui.component.ExploreNoteSummaryUi
import com.leafy.features.community.ui.component.ExploreTagUi
import com.leafy.features.community.ui.component.ExploreTeaMasterUi
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.CommunityTag
import com.subin.leafy.domain.model.TeaMaster
import com.leafy.shared.R as SharedR

/**
 * 도메인 모델 리스트 -> UI 모델 리스트 변환 (확장 함수)
 */

// 1. 인기/라이징/저장 노트 요약용
fun List<CommunityPost>.toSummaryUi(): List<ExploreNoteSummaryUi> = this.map {
    ExploreNoteSummaryUi(
        title = it.title,
        subtitle = it.subtitle,
        imageRes = it.imageRes,
        rating = it.rating,
        savedCount = it.savedCount,
        profileImageRes = it.authorProfileUrl ?: SharedR.drawable.ic_profile_1,
        authorName = it.authorName,
        likeCount = it.likeCount,
        isLiked = it.isLiked
    )
}

// 2. 티 마스터 섹션용
fun List<TeaMaster>.toMasterUi(): List<ExploreTeaMasterUi> = this.map {
    ExploreTeaMasterUi(
        profileImageRes = it.profileImageRes,
        name = it.name,
        title = it.title,
        isFollowing = it.isFollowing
    )
}

// 3. 인기 태그 섹션용
fun List<CommunityTag>.toTagUi(): List<ExploreTagUi> = this.map {
    ExploreTagUi(
        label = it.label,
        isTrendingUp = it.isTrendingUp
    )
}

// 4. 팔로우 탭 피드용 (FollowingNoteCard 등에 사용)
fun List<CommunityPost>.toFollowingUi(): List<ExploreFollowingNoteUi> = this.map { post ->
    ExploreFollowingNoteUi(
        authorName = post.authorName,
        authorAvatarRes = post.authorProfileUrl ?: SharedR.drawable.ic_profile_1,
        timeAgo = post.createdAt,
        tagLabel = post.teaTag, // 예: "Oolong"
        imageRes = post.imageRes,
        title = post.title,
        meta = post.subtitle, // "대만 · 중배화 · 반구형"
        description = post.content,
        // 도메인의 복잡한 정보를 UI용 칩 리스트로 가공
        brewingChips = listOf("${post.rating}★", post.metaInfo),
        rating = post.rating,
        reviewLabel = "Verified", // 조건에 따른 라벨 처리 가능
        comment = "커뮤니티 글의 상세 코멘트입니다.",
        likerAvatarResList = listOf(SharedR.drawable.ic_profile_2), // 샘플
        likeCountText = "${post.likeCount}명이 좋아합니다"
    )
}