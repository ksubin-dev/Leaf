package com.leafy.features.community.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leafy.features.community.ui.component.CustomExploreTabRow
import com.leafy.features.community.ui.component.NoteCommentBottomSheet
import com.leafy.features.community.ui.feed.CommunityFeedSideEffect
import com.leafy.features.community.ui.feed.CommunityFeedViewModel
import com.leafy.features.community.ui.feed.CommunityFeedViewModelFactory
import com.leafy.features.community.ui.feed.CommunityTab
import com.leafy.features.community.ui.feed.CommunityUiState
import com.leafy.features.community.ui.feed.section.CommunityFollowingFeedSection
import com.leafy.features.community.ui.feed.section.CommunityMostBookmarkedSection
import com.leafy.features.community.ui.feed.section.CommunityPopularSection
import com.leafy.features.community.ui.feed.section.CommunityTeaMasterSection
import com.leafy.features.community.ui.model.CommunityPostUiModel
import com.leafy.features.community.ui.model.UserUiModel
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    postUseCases: PostUseCases,
    userUseCases: UserUseCases,
    onPostClick: (String) -> Unit, // 상세 화면 이동
    onMasterClick: (String) -> Unit, // 프로필 화면 이동
    onMorePopularClick: () -> Unit = {}, // 인기글 더보기
    onMoreBookmarkClick: () -> Unit = {}, // 북마크 더보기
    onMoreMasterClick: () -> Unit = {} // 마스터 더보기
) {
    // 1. ViewModel 생성 (Factory 사용)
    val viewModel: CommunityFeedViewModel = viewModel(
        factory = CommunityFeedViewModelFactory.provide(postUseCases, userUseCases)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // 2. Side Effect 처리 (에러 스낵바, 키보드 내리기)
    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is CommunityFeedSideEffect.HideKeyboard -> {
                    keyboardController?.hide()
                }
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.onMessageShown()
        }
    }

    // 3. UI 구조 (Scaffold)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 상단 탭 (추천 / 팔로잉)
            // CommunityTabRow는 기존에 있던 CustomExploreTabRow를 재활용하거나 새로 만드세요.
            // 여기선 이름만 CommunityTabRow로 가정합니다.
            CustomExploreTabRow(
                selectedTab = uiState.selectedTab,
                onTabSelected = viewModel::onTabSelected
            )

            // 메인 콘텐츠
            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading && uiState.popularPosts.isEmpty() && uiState.followingPosts.isEmpty()) {
                    // 처음 로딩 중 (데이터 없음)
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    when (uiState.selectedTab) {
                        CommunityTab.TRENDING -> TrendingContent(
                            uiState = uiState,
                            onPostClick = { onPostClick(it.postId) },
                            onMasterClick = { onMasterClick(it.userId) },
                            onBookmarkClick = { viewModel.toggleBookmark(it.postId) },
                            onFollowToggle = { viewModel.toggleFollow(it.userId) },
                            onMorePopularClick = onMorePopularClick,
                            onMoreBookmarkClick = onMoreBookmarkClick,
                            onMoreMasterClick = onMoreMasterClick
                        )
                        CommunityTab.FOLLOWING -> FollowingContent(
                            uiState = uiState,
                            onPostClick = { onPostClick(it.postId) },
                            onLikeClick = { viewModel.toggleLike(it.postId) },
                            onBookmarkClick = { viewModel.toggleBookmark(it.postId) },
                            onCommentClick = { viewModel.showComments(it.postId) }
                        )
                    }
                }
            }
        }
    }

    // 4. 댓글 바텀 시트
    if (uiState.showCommentSheet) {
        NoteCommentBottomSheet(
            onDismissRequest = viewModel::hideComments,
            comments = uiState.comments,
            isLoading = uiState.isCommentLoading,
            commentInput = uiState.commentInput,
            onInputChange = viewModel::updateCommentInput,
            onSendComment = viewModel::addComment,
            onDeleteComment = viewModel::deleteComment
        )
    }
}

// -----------------------------------------------------------------------------
// 내부 컴포넌트: 추천 탭 (Trending)
// -----------------------------------------------------------------------------
@Composable
private fun TrendingContent(
    uiState: CommunityUiState,
    onPostClick: (CommunityPostUiModel) -> Unit,
    onMasterClick: (UserUiModel) -> Unit,
    onBookmarkClick: (CommunityPostUiModel) -> Unit,
    onFollowToggle: (UserUiModel) -> Unit,
    onMorePopularClick: () -> Unit,
    onMoreBookmarkClick: () -> Unit,
    onMoreMasterClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // 1. 이번 주 인기 노트 (가로 스크롤)
        CommunityPopularSection(
            posts = uiState.popularPosts,
            onPostClick = onPostClick,
            onMoreClick = onMorePopularClick
        )

        // 2. 가장 많이 저장된 노트 (세로 리스트 3개)
        CommunityMostBookmarkedSection(
            posts = uiState.mostBookmarkedPosts,
            onPostClick = onPostClick,
            onBookmarkClick = onBookmarkClick,
            onMoreClick = onMoreBookmarkClick
        )

        // 3. 이번 달 티 마스터 (세로 리스트 3명)
        CommunityTeaMasterSection(
            masters = uiState.teaMasters,
            onMasterClick = onMasterClick,
            onFollowToggle = onFollowToggle,
            onMoreClick = onMoreMasterClick
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// -----------------------------------------------------------------------------
// 내부 컴포넌트: 팔로잉 탭 (Following)
// -----------------------------------------------------------------------------
@Composable
private fun FollowingContent(
    uiState: CommunityUiState,
    onPostClick: (CommunityPostUiModel) -> Unit,
    onLikeClick: (CommunityPostUiModel) -> Unit,
    onBookmarkClick: (CommunityPostUiModel) -> Unit,
    onCommentClick: (CommunityPostUiModel) -> Unit
) {
    if (uiState.isFollowingEmpty) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "팔로우한 이웃의 소식이 아직 없습니다.\n티 마스터를 팔로우해보세요!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    } else {
        CommunityFollowingFeedSection(
            posts = uiState.followingPosts,
            onPostClick = onPostClick,
            onLikeClick = onLikeClick,
            onCommentClick = onCommentClick,
            onBookmarkClick = onBookmarkClick,
            modifier = Modifier.fillMaxSize()
        )
    }
}