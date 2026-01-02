package com.leafy.features.community.ui

import com.leafy.features.community.ui.component.ExploreNoteUi
import com.leafy.features.community.ui.component.ExploreTagUi
import com.leafy.features.community.ui.component.ExploreTeaMasterUi
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.CommunityTag
import com.subin.leafy.domain.model.TeaMaster
import com.leafy.shared.R as SharedR

/**
 * 1 & 4 통합: 커뮤니티 노트 (인기/라이징/팔로잉 통합)
 */
fun List<CommunityPost>.toNoteUi(): List<ExploreNoteUi> = this.map { post ->
    ExploreNoteUi(
        id = post.id,
        title = post.title,
        subtitle = post.subtitle,
        imageUrl = post.imageUrl,
        rating = post.rating,
        savedCount = post.savedCount,
        likeCount = post.likeCount,
        isLiked = post.isLiked,
        isSaved = post.isSaved,

        // 작성자 및 피드 정보
        authorName = post.authorName,
        authorProfileUrl = post.authorProfileUrl,
        timeAgo = post.createdAt,

        // 상세 섹션용 정보 가공
        metaInfo = post.subtitle,
        description = post.content,
        brewingChips = post.brewingSteps.ifEmpty { listOf(post.metaInfo) },
        reviewLabel = if (post.rating >= 4.5f) "Highly Rated" else "Verified",
        comment = "차의 풍미가 아주 좋습니다.",
        likerProfileUrls = emptyList() // 나중에 서버에서 받아올 수 있음
    )
}

/**
 * 2. 티 마스터 섹션용
 */
fun List<TeaMaster>.toMasterUi(): List<ExploreTeaMasterUi> = this.map {
    ExploreTeaMasterUi(
        id = it.id,
        name = it.name,
        title = it.title,
        profileImageUrl = it.profileImageUrl,
        isFollowing = it.isFollowing
    )
}

/**
 * 3. 인기 태그 섹션용
 */
fun List<CommunityTag>.toTagUi(): List<ExploreTagUi> = this.map {
    ExploreTagUi(
        id = it.id,
        label = it.label,
        isTrendingUp = it.isTrendingUp
    )
}