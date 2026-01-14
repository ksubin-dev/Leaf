package com.leafy.features.community.screen
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.leafy.features.community.ui.*
//import com.leafy.features.community.ui.component.*
//import com.leafy.features.community.ui.section.*
//import com.subin.leafy.domain.model.ExploreContent
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CommunityScreen(
//    viewModel: CommunityViewModel,
//    onNoteClick: (String) -> Unit,
//    onMasterClick: (String) -> Unit
//) {
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//    val snackbarHostState = remember { SnackbarHostState() }
//
//    // 댓글 바텀시트 상태 관리
//    var showCommentSheet by remember { mutableStateOf(false) }
//    var selectedPostId by remember { mutableStateOf("") }
//
//    LaunchedEffect(Unit) {
//        viewModel.effect.collect { effect ->
//            when (effect) {
//                is CommunityUiEffect.ShowSnackbar -> {
//                    snackbarHostState.showSnackbar(message = effect.message)
//                }
//                is CommunityUiEffect.NavigateToComments -> {
//                    selectedPostId = effect.postId
//                    viewModel.loadComments(effect.postId)
//                    showCommentSheet = true
//                }
//            }
//        }
//    }
//
//    Scaffold(
//        modifier = Modifier.fillMaxSize(),
//        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//        ) {
//            CustomExploreTabRow(
//                selectedTab = uiState.selectedTab,
//                onTabSelected = { viewModel.onTabSelected(it) }
//            )
//
//            Box(modifier = Modifier.fillMaxSize()) {
//                if (uiState.isLoading && uiState.popularNotes.isEmpty()) {
//                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//                } else {
//                    when (uiState.selectedTab) {
//                        ExploreContent.TRENDING -> TrendingTabContent(
//                            uiState = uiState,
//                            onNoteClick = { onNoteClick(it.id) },
//                            onMasterClick = { onMasterClick(it.id) },
//                            onFollowToggle = { master, isFollowing ->
//                                viewModel.toggleFollow(master.id, isFollowing)
//                            }
//                        )
//                        ExploreContent.FOLLOWING -> FollowingTabContent(
//                            uiState = uiState,
//                            onNoteClick = { onNoteClick(it.id) },
//                            onLikeClick = { note -> viewModel.toggleLike(note.id, note.isLiked) },
//                            onCommentClick = { note ->
//                                selectedPostId = note.id
//                                viewModel.loadComments(note.id)
//                                showCommentSheet = true
//                            }
//                        )
//                    }
//                }
//            }
//        }
//    }
//
//    if (showCommentSheet) {
//        NoteCommentBottomSheet(
//            onDismissRequest = { showCommentSheet = false },
//            comments = uiState.comments,
//            onSendComment = { content -> viewModel.sendComment(selectedPostId, content) },
//            onReplyClick = { /* 필요 시 구현 */ }
//        )
//    }
//}
//
//@Composable
//private fun TrendingTabContent(
//    uiState: CommunityUiState,
//    onNoteClick: (ExploreNoteUi) -> Unit,
//    onMasterClick: (ExploreTeaMasterUi) -> Unit,
//    onFollowToggle: (ExploreTeaMasterUi, Boolean) -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
//            .padding(vertical = 20.dp),
//        verticalArrangement = Arrangement.spacedBy(36.dp)
//    ) {
//        ExploreTrendingTopSection(
//            notes = uiState.popularNotes,
//            onNoteClick = onNoteClick
//        )
//
//        ExploreTrendingSavedSection(
//            notes = uiState.mostSavedNotes,
//            onNoteClick = onNoteClick,
//            modifier = Modifier.padding(horizontal = 16.dp)
//        )
//
//        ExploreTrendingTeaMasterSection(
//            masters = uiState.teaMasters,
//            onMasterClick = onMasterClick,
//            onFollowToggle = onFollowToggle,
//            modifier = Modifier.padding(horizontal = 16.dp)
//        )
//
//        Spacer(modifier = Modifier.height(20.dp))
//    }
//}
//
//@Composable
//private fun FollowingTabContent(
//    uiState: CommunityUiState,
//    onNoteClick: (ExploreNoteUi) -> Unit,
//    onLikeClick: (ExploreNoteUi) -> Unit,
//    onCommentClick: (ExploreNoteUi) -> Unit
//) {
//    if (uiState.followingFeed.isEmpty()) {
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            Text(
//                text = "팔로우한 마스터의 소식이 없습니다.",
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//        }
//    } else {
//        ExploreFollowingFeedSection(
//            notes = uiState.followingFeed,
//            onNoteClick = onNoteClick,
//            onLikeClick = onLikeClick,
//            onCommentClick = onCommentClick,
//            modifier = Modifier.fillMaxSize()
//        )
//    }
//}
//
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewCommunityScreen() {
//    MaterialTheme {
//        val mockState = CommunityUiState(
//            isLoading = false,
//            selectedTab = ExploreContent.TRENDING,
//            popularNotes = listOf(
//                ExploreNoteUi(id = "1", title = "우전 녹차", subtitle = "2024년 첫 수확", rating = 4.8f)
//            ),
//            teaMasters = listOf(
//                ExploreTeaMasterUi(id = "1", name = "차 마스터", title = "보이차 전문가", isFollowing = false)
//            )
//        )
//
//        TrendingTabContent(
//            uiState = mockState,
//            onNoteClick = {},
//            onMasterClick = {},
//            onFollowToggle = { _, _ -> }
//        )
//    }
//}