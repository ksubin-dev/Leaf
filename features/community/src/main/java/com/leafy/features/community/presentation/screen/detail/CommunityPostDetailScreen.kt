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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.features.community.presentation.components.bar.CommentInputBar
import com.leafy.features.community.presentation.components.item.CommentItem
import com.leafy.features.community.presentation.common.model.CommentUiModel
import com.leafy.features.community.presentation.common.model.CommunityPostUiModel
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LeafyDialog
import com.leafy.shared.ui.theme.LeafyTheme

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


@Preview(showBackground = true)
@Composable
private fun CommunityPostDetailScreenPreview() {
    val dummyPost = CommunityPostUiModel(
        postId = "1",
        authorId = "user1",
        authorName = "홍차왕자",
        authorProfileUrl = null,
        isFollowingAuthor = false,
        title = "주말 오후의 얼그레이 티타임",
        content = "날씨가 좋아서 오랜만에 베란다에서 차를 마셨습니다.",
        imageUrls = listOf(""),
        tags = listOf("#얼그레이", "#티타임", "#주말"),
        originNoteId = "note_123",
        timeAgo = "2시간 전",
        teaType = "홍차",
        brewingSummary = "95℃ · 3m · 3g",
        rating = 5,
        brewingChips = listOf("95℃", "3m", "3g"),
        likeCount = "24",
        commentCount = "2",
        viewCount = "150",
        bookmarkCount = "5",
        isLiked = true,
        isBookmarked = false
    )

    val dummyComments = listOf(
        CommentUiModel("1", "user2", "녹차조아", null, "찻잔이 너무 예쁘네요!", "1시간 전", false),
        CommentUiModel("2", "me", "나야나", null, "감사합니다 ^^", "방금 전", true)
    )

    LeafyTheme {
        CommunityPostDetailScreen(
            post = dummyPost,
            comments = dummyComments,
            commentInput = "댓글...",
            currentUserProfileUrl = null,
            isLoading = false,
            onNavigateBack = {},
            onInputChange = {},
            onSendComment = {},
            onDeleteComment = {},
            onLikeClick = {},
            onBookmarkClick = {},
            onOriginNoteClick = {},
            onUserProfileClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun CommunityPostDetailLoadingPreview() {
    LeafyTheme {
        CommunityPostDetailScreen(
            post = null,
            comments = emptyList(),
            commentInput = "",
            currentUserProfileUrl = null,
            isLoading = true,
            onNavigateBack = {},
            onInputChange = {},
            onSendComment = {},
            onDeleteComment = {},
            onLikeClick = {},
            onBookmarkClick = {},
            onOriginNoteClick = {},
            onUserProfileClick = {}
        )
    }
}