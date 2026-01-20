package com.leafy.features.community.presentation.screen.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leafy.features.community.presentation.components.bar.CustomExploreTabRow
import com.leafy.features.community.presentation.screen.feed.section.CommunityFollowingFeedSection
import com.leafy.features.community.presentation.screen.feed.section.CommunityMostBookmarkedSection
import com.leafy.features.community.presentation.screen.feed.section.CommunityPopularSection
import com.leafy.features.community.presentation.screen.feed.section.CommunityTeaMasterSection
import com.leafy.shared.ui.model.CommunityPostUiModel
import com.leafy.shared.ui.model.UserUiModel
import com.leafy.features.community.presentation.components.bar.CommunityTab
import com.leafy.shared.common.singleClick
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    postUseCases: PostUseCases,
    userUseCases: UserUseCases,
    onPostClick: (String) -> Unit,
    onMasterClick: (String) -> Unit,
    onMorePopularClick: () -> Unit = {},
    onMoreBookmarkClick: () -> Unit = {},
    onMoreMasterClick: () -> Unit = {}
) {

    val viewModel: CommunityFeedViewModel = viewModel(
        factory = CommunityFeedViewModelFactory.provide(postUseCases, userUseCases)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is CommunityFeedSideEffect.HideKeyboard -> {
                    keyboardController?.hide()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { message ->
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null && !uiState.popularPosts.isEmpty()) {
            snackbarHostState.showSnackbar(uiState.errorMessage!!)
            viewModel.onMessageShown()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CustomExploreTabRow(
                selectedTab = uiState.selectedTab,
                onTabSelected = viewModel::onTabSelected
            )

            Box(modifier = Modifier.fillMaxSize()) {
                val isDataEmpty = when(uiState.selectedTab) {
                    CommunityTab.TRENDING -> uiState.popularPosts.isEmpty() && uiState.mostBookmarkedPosts.isEmpty()
                    CommunityTab.FOLLOWING -> uiState.followingPosts.isEmpty()
                }

                if (uiState.isLoading && isDataEmpty) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                else if (uiState.errorMessage != null && isDataEmpty) {
                    ErrorRetryView(
                        message = "데이터를 불러오지 못했습니다.",
                        onRetry = viewModel::refresh,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else {
                    when (uiState.selectedTab) {
                        CommunityTab.TRENDING -> TrendingContent(
                            uiState = uiState,
                            onPostClick = { onPostClick(it.postId) },
                            onMasterClick = { onMasterClick(it.userId) },
                            onBookmarkClick = { viewModel.toggleBookmark(it.postId) },
                            onFollowToggle = { viewModel.toggleFollow(it.userId) },
                            onMorePopularClick = onMorePopularClick,
                            onMoreBookmarkClick = onMoreBookmarkClick,
                            onMoreMasterClick = onMoreMasterClick,
                            onProfileClick = onMasterClick,
                        )
                        CommunityTab.FOLLOWING -> FollowingContent(
                            uiState = uiState,
                            onPostClick = { onPostClick(it.postId) },
                            onLikeClick = { viewModel.toggleLike(it.postId) },
                            onBookmarkClick = { viewModel.toggleBookmark(it.postId) },
                            onCommentClick = { onPostClick(it.postId) },
                            onProfileClick = onMasterClick
                        )
                    }
                }
            }
        }
    }

//    if (uiState.showCommentSheet) {
//        NoteCommentBottomSheet(
//            onDismissRequest = viewModel::hideComments,
//            comments = uiState.comments,
//            currentUserProfileUrl = uiState.currentUserProfileUrl,
//            isLoading = uiState.isCommentLoading,
//            commentInput = uiState.commentInput,
//            onInputChange = viewModel::updateCommentInput,
//            onSendComment = viewModel::addComment,
//            onDeleteComment = viewModel::deleteComment,
//            onUserProfileClick = onMasterClick
//        )
//    }
}

@Composable
private fun TrendingContent(
    uiState: CommunityUiState,
    onPostClick: (CommunityPostUiModel) -> Unit,
    onMasterClick: (UserUiModel) -> Unit,
    onProfileClick: (String) -> Unit,
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
            .padding(top = 24.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {

        CommunityPopularSection(
            posts = uiState.popularPosts,
            onPostClick = onPostClick,
            onMoreClick = onMorePopularClick,
            onProfileClick = onProfileClick
        )

        CommunityMostBookmarkedSection(
            posts = uiState.mostBookmarkedPosts,
            onPostClick = onPostClick,
            onBookmarkClick = onBookmarkClick,
            onMoreClick = onMoreBookmarkClick
        )

        CommunityTeaMasterSection(
            masters = uiState.teaMasters,
            currentUserId = uiState.currentUserId,
            onMasterClick = onMasterClick,
            onFollowToggle = onFollowToggle,
            onMoreClick = onMoreMasterClick
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ErrorRetryView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 아이콘 (선택 사항)
        // Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color.Gray)

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = singleClick { onRetry() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("다시 시도")
        }
    }
}

@Composable
private fun FollowingContent(
    uiState: CommunityUiState,
    onPostClick: (CommunityPostUiModel) -> Unit,
    onLikeClick: (CommunityPostUiModel) -> Unit,
    onBookmarkClick: (CommunityPostUiModel) -> Unit,
    onCommentClick: (CommunityPostUiModel) -> Unit,
    onProfileClick: (String) -> Unit
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
                textAlign = TextAlign.Center
            )
        }
    } else {
        CommunityFollowingFeedSection(
            posts = uiState.followingPosts,
            modifier = Modifier.fillMaxSize(),
            onPostClick = onPostClick,
            onLikeClick = onLikeClick,
            onCommentClick = onCommentClick,
            onBookmarkClick = onBookmarkClick,
            onProfileClick = onProfileClick
        )
    }
}