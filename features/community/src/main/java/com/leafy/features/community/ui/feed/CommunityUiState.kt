package com.leafy.features.community.ui.feed

import com.leafy.features.community.ui.model.CommentUiModel
import com.leafy.features.community.ui.model.CommunityPostUiModel
import com.leafy.features.community.ui.model.UserUiModel

data class CommunityUiState(
    // =================================================================
    // 1. 글로벌 상태 (탭, 로딩, 에러)
    // =================================================================
    val selectedTab: CommunityTab = CommunityTab.TRENDING, // 추천 vs 팔로잉
    val isLoading: Boolean = false,       // 전체 화면 로딩 (처음 진입 시)
    val isRefreshing: Boolean = false,    // 당겨서 새로고침 중
    val errorMessage: String? = null,     // 스낵바 에러 메시지

    // =================================================================
    // 2. [추천 탭] 데이터 (가로 스크롤 섹션들)
    // =================================================================
    // 이번 주 인기 노트 (가로 리스트)
    val popularPosts: List<CommunityPostUiModel> = emptyList(),

    // 명예의 전당 / 가장 많이 저장된 노트 (가로 리스트)
    val mostBookmarkedPosts: List<CommunityPostUiModel> = emptyList(),

    // 추천 티 마스터 (동그란 프로필 리스트)
    val teaMasters: List<UserUiModel> = emptyList(),

    // =================================================================
    // 3. [팔로잉 탭] 데이터 (세로 피드)
    // =================================================================
    // 내가 팔로우한 사람들의 글 목록
    val followingPosts: List<CommunityPostUiModel> = emptyList(),

    // 팔로잉 목록이 0명이라 피드가 비었을 때 (Empty View 표시용)
    val isFollowingEmpty: Boolean = false,

    // =================================================================
    // 4. [인터랙션] 댓글 및 상세 보기
    // =================================================================
    // 댓글 바텀 시트 표시 여부
    val showCommentSheet: Boolean = false,

    // 댓글을 보려고 선택한 게시글 ID
    val selectedPostId: String? = null,
    val commentInput: String = "",
    // 불러온 댓글 목록
    val comments: List<CommentUiModel> = emptyList(),

    // 댓글 로딩 중 (댓글 입력 시 등)
    val isCommentLoading: Boolean = false

) {
    // UI에서 에러 여부를 쉽게 확인하기 위한 헬퍼
    val hasError: Boolean get() = errorMessage != null

    // 현재 탭 확인 헬퍼
    val isTrendingTab: Boolean get() = selectedTab == CommunityTab.TRENDING
}