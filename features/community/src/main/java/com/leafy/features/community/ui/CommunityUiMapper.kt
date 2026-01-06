package com.leafy.features.community.ui

import com.leafy.features.community.ui.component.ExploreNoteUi
import com.leafy.features.community.ui.component.ExploreTeaMasterUi
import com.subin.leafy.domain.model.Comment
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.TeaMaster

fun List<CommunityPost>.toNoteUi(): List<ExploreNoteUi> = this.map { post ->
    ExploreNoteUi(
        id = post.id,
        title = post.title,
        subtitle = post.subtitle,
        imageUrl = post.imageUrl,
        rating = post.rating,
        savedCount = post.bookmarkCount,
        likeCount = post.likeCount,
        commentCount = post.commentCount,
        isLiked = post.isLiked,
        isSaved = post.isBookmarked,

        // 작성자 및 피드 정보
        authorName = post.authorName,
        authorProfileUrl = post.authorProfileUrl,
        timeAgo = post.createdAt,

        // 상세 섹션용 정보 가공
        metaInfo = post.metaInfo,
        description = post.content,
        brewingChips = post.brewingSteps,
        reviewLabel = if (post.rating >= 4.5f) "Highly Rated" else "Verified",
        comment = post.topComment ?: "차의 풍미가 아주 좋습니다.",
        likerProfileUrls = emptyList()
    )
}

/**
 * 2. 티 마스터 섹션용 변환
 */
fun List<TeaMaster>.toMasterUi(): List<ExploreTeaMasterUi> = this.map { master ->
    ExploreTeaMasterUi(
        id = master.id,
        name = master.name,
        title = master.title,
        profileImageUrl = master.profileImageUrl,
        isFollowing = master.isFollowing
    )
}

/**
 * 3. 댓글 리스트용 변환
 */
fun List<Comment>.toCommentUi(): List<CommunityCommentUi> = this.map { comment ->
    CommunityCommentUi(
        id = comment.id,
        authorId = comment.authorId,
        authorName = comment.authorName,
        authorProfileUrl = comment.authorProfileUrl,
        content = comment.content,
        timeAgo = comment.createdAt.toString() // 필요 시 포맷팅 로직 추가
    )
}