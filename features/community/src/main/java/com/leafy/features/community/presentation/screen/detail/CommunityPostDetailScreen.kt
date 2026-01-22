package com.leafy.features.community.presentation.screen.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.features.community.presentation.components.bar.CommentInputBar
import com.leafy.features.community.presentation.components.item.CommentItem
import com.leafy.shared.ui.model.CommentUiModel
import com.leafy.shared.ui.model.CommunityPostUiModel
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LeafyDialog

@Composable
fun CommunityPostDetailRoute(
    viewModel: CommunityPostDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToNoteDetail: (String) -> Unit,
    onNavigateToUserProfile: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refresh()
    }

    LaunchedEffect(uiState.post, uiState.isLoading, uiState.errorMessage) {
        if (!uiState.isLoading && uiState.post == null && uiState.errorMessage != null) {
            onNavigateBack()
        }
    }

    CommunityPostDetailScreen(
        post = uiState.post,
        comments = uiState.comments,
        commentInput = uiState.commentInput,
        currentUserProfileUrl = uiState.currentUserProfileUrl,
        isLoading = uiState.isLoading,
        onNavigateBack = onNavigateBack,
        onInputChange = viewModel::updateCommentInput,
        onSendComment = viewModel::sendComment,
        onDeleteComment = viewModel::deleteComment,
        onLikeClick = viewModel::toggleLike,
        onBookmarkClick = viewModel::toggleBookmark,
        onOriginNoteClick = onNavigateToNoteDetail,
        onUserProfileClick = onNavigateToUserProfile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityPostDetailScreen(
    post: CommunityPostUiModel?,
    comments: List<CommentUiModel>,
    commentInput: String,
    currentUserProfileUrl: String?,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onInputChange: (String) -> Unit,
    onSendComment: () -> Unit,
    onDeleteComment: (String) -> Unit,
    onLikeClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onOriginNoteClick: (String) -> Unit,
    onUserProfileClick: (String) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val keyboardController = LocalSoftwareKeyboardController.current

    var commentIdToDelete by remember { mutableStateOf<String?>(null) }

    if (commentIdToDelete != null) {
        LeafyDialog(
            onDismissRequest = { commentIdToDelete = null },
            title = "댓글 삭제",
            text = "정말 이 댓글을 삭제하시겠습니까?",
            confirmText = "삭제",
            dismissText = "취소",
            onConfirmClick = {
                onDeleteComment(commentIdToDelete!!)
                commentIdToDelete = null
            }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("게시글", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = singleClick { onNavigateBack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    IconButton(onClick = singleClick { /* 더보기 로직 */ }) {
                        Icon(Icons.Rounded.MoreVert, contentDescription = "더보기")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            CommentInputBar(
                input = commentInput,
                onInputChange = onInputChange,
                onSend = {
                    onSendComment()
                    keyboardController?.hide()
                },
                currentUserProfileUrl = currentUserProfileUrl,
                isLoading = isLoading
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        if (post == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    PostDetailContent(
                        post = post,
                        onLikeClick = onLikeClick,
                        onBookmarkClick = onBookmarkClick,
                        onOriginNoteClick = onOriginNoteClick,
                        onUserProfileClick = onUserProfileClick
                    )
                }

                item {
                    HorizontalDivider(thickness = 8.dp, color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    Text(
                        text = "댓글 ${comments.size}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                    )
                }

                if (comments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("첫 댓글을 남겨주세요!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(
                        items = comments,
                        key = { it.commentId }
                    ) { comment ->
                        CommentItem(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            comment = comment,
                            onDeleteClick = { commentIdToDelete = comment.commentId },
                            onProfileClick = onUserProfileClick
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 68.dp, end = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
    }
}
