package com.leafy.features.community.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.features.community.ui.CommunityUiEffect
import com.leafy.features.community.ui.CommunityUiState
import com.leafy.features.community.ui.CommunityViewModel
import com.leafy.features.community.ui.component.CustomExploreTabRow
import com.leafy.features.community.ui.component.ExploreNoteUi
import com.leafy.features.community.ui.component.ExploreTeaMasterUi
import com.leafy.features.community.ui.section.*
import com.subin.leafy.domain.model.ExploreTab

@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel,
    onNoteClick: (String) -> Unit,
    onMasterClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CommunityUiEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 탭 바
            CustomExploreTabRow(
                selectedTab = uiState.selectedTab,
                onTabSelected = { viewModel.onTabSelected(it) }
            )

            // 메인 컨텐츠
            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading && uiState.popularNotes.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    when (uiState.selectedTab) {
                        ExploreTab.TRENDING -> TrendingTabContent(
                            uiState = uiState,
                            onNoteClick = { onNoteClick(it.id) },
                            onMasterClick = { onMasterClick(it.id) },
                            onFollowToggle = { master, isFollowing ->
                                // TODO: viewModel.toggleFollow(master.id) 호출
                            }
                        )
                        ExploreTab.FOLLOWING -> FollowingTabContent(
                            uiState = uiState,
                            onNoteClick = { onNoteClick(it.id) },
                            onLikeClick = { note -> viewModel.toggleLike(note.id) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Trending 탭 레이아웃
 */
@Composable
private fun TrendingTabContent(
    uiState: CommunityUiState,
    onNoteClick: (ExploreNoteUi) -> Unit,
    onMasterClick: (ExploreTeaMasterUi) -> Unit,
    onFollowToggle: (ExploreTeaMasterUi, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(36.dp)
    ) {
        // 섹션 1: 이번 주 인기 노트
        ExploreTrendingTopSection(
            notes = uiState.popularNotes,
            onNoteClick = onNoteClick
        )

        // 섹션 2: 지금 급상승 중
        ExploreTrendingRisingSection(
            notes = uiState.risingNotes,
            onNoteClick = onNoteClick
        )

        // 섹션 3: 인기 태그
        ExploreTrendingPopularTagsSection(
            tags = uiState.popularTags,
            onTagClick = { /* TODO: 태그 검색 결과 이동 */ }
        )

        // 섹션 4: 많이 저장된 노트
        ExploreTrendingSavedSection(
            notes = uiState.mostSavedNotes,
            onNoteClick = onNoteClick,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // 섹션 5: 티 마스터 추천
        ExploreTrendingTeaMasterSection(
            masters = uiState.teaMasters,
            onMasterClick = onMasterClick,
            onFollowToggle = onFollowToggle,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

/**
 * Following 탭 레이아웃 (스크롤 가능한 피드)
 */
@Composable
private fun FollowingTabContent(
    uiState: CommunityUiState,
    onNoteClick: (ExploreNoteUi) -> Unit,
    onLikeClick: (ExploreNoteUi) -> Unit
) {
    if (uiState.followingFeed.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "팔로우한 마스터의 소식이 없습니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        ExploreFollowingFeedSection(
            notes = uiState.followingFeed,
            onNoteClick = onNoteClick,
            onLikeClick = onLikeClick,
            modifier = Modifier.fillMaxSize()
        )
    }
}