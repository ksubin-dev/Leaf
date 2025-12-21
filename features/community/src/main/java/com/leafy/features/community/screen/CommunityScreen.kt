package com.leafy.features.community.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.features.community.ui.CommunityUiEffect
import com.leafy.features.community.ui.CommunityUiState
import com.leafy.features.community.ui.CommunityViewModel
import com.leafy.features.community.ui.component.CustomExploreTabRow
import com.leafy.features.community.ui.section.*
import com.subin.leafy.domain.model.ExploreTab

@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel,
    onNoteClick: (String) -> Unit,
    onMasterClick: (String) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 1. 일회성 이벤트(Effect) 처리: 토스트 등
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CommunityUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 2. 탭 바
            CustomExploreTabRow(
                selectedTab = uiState.selectedTab,
                onTabSelected = { viewModel.onTabSelected(it) }
            )

            // 3. 메인 컨텐츠
            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading && uiState.popularNotes.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    when (uiState.selectedTab) {
                        ExploreTab.TRENDING -> TrendingTabContent(
                            uiState = uiState,
                            onNoteClick = { onNoteClick(it.title) },
                            onMasterClick = { onMasterClick(it.name) },
                            onFollowToggle = { master, isFollowing ->
                                // TODO: viewModel.toggleFollow(master.name) 호출
                            }
                        )
                        ExploreTab.FOLLOWING -> ExploreFollowingFeedSection(
                            notes = uiState.followingFeed,
                            onNoteClick = { onNoteClick(it.title) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

/**
 * Trending 탭 레이아웃: 유저님이 만든 모든 섹션 컴포넌트 통합
 */
@Composable
private fun TrendingTabContent(
    uiState: CommunityUiState,
    onNoteClick: (com.leafy.features.community.ui.component.ExploreNoteSummaryUi) -> Unit,
    onMasterClick: (com.leafy.features.community.ui.component.ExploreTeaMasterUi) -> Unit,
    onFollowToggle: (com.leafy.features.community.ui.component.ExploreTeaMasterUi, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(36.dp) // 섹션 사이 간격
    ) {
        // 섹션 1: 이번 주 인기 노트
        ExploreTrendingTopSection(
            notes = uiState.popularNotes,
            onNoteClick = onNoteClick,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // 섹션 2: 지금 급상승 중
        ExploreTrendingRisingSection(
            notes = uiState.risingNotes,
            onNoteClick = onNoteClick,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // 섹션 3: 인기 태그
        ExploreTrendingPopularTagsSection(
            tags = uiState.popularTags,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // 섹션 4: 많이 저장된 노트
        ExploreTrendingSavedSection(
            notes = uiState.mostSavedNotes,
            onNoteClick = onNoteClick,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // 섹션 5: 티 마스터 추천 (수정된 람다 연결)
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
 * [Following 탭] - 팔로잉 피드 리스트
 */
@Composable
private fun FollowingTabContent(
    uiState: CommunityUiState,
    onNoteClick: (com.leafy.features.community.ui.component.ExploreFollowingNoteUi) -> Unit
) {
    if (uiState.followingFeed.isEmpty()) {
        // 팔로잉 데이터가 없을 때의 처리
        // EmptyFollowingView()
    } else {
        ExploreFollowingFeedSection(
            notes = uiState.followingFeed,
            onNoteClick = onNoteClick,
            modifier = Modifier.fillMaxSize()
        )
    }
}