package com.leafy.features.community.presentation.screen.popular

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.community.presentation.common.model.CommunityPostUiModel
import com.leafy.features.community.presentation.screen.popular.component.PinterestPostCard
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun PopularPostListScreen(
    viewModel: PopularPostListViewModel,
    onBackClick: () -> Unit,
    onPostClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    PopularPostListContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onPostClick = onPostClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopularPostListContent(
    uiState: PopularPostListUiState,
    onBackClick: () -> Unit,
    onPostClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "이번 주 인기 노트",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = singleClick { onBackClick() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 12.dp,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(uiState.posts) { post ->
                    PinterestPostCard(
                        post = post,
                        onClick = { onPostClick(post.postId) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PopularPostListScreenPreview() {
    LeafyTheme {
        val dummyPosts = listOf(
            CommunityPostUiModel(
                postId = "1", authorId = "u1", authorName = "티러버", authorProfileUrl = null,
                isFollowingAuthor = false, title = "비오는 날 어울리는 진한 수금귀",
                content = "내용...", imageUrls = listOf("https://dummy"),
                timeAgo = "1일 전", teaType = "우롱차", brewingSummary = null, rating = 5,
                likeCount = "124", commentCount = "10", viewCount = "100", bookmarkCount = "10",
                isLiked = false, isBookmarked = false
            ),
            CommunityPostUiModel(
                postId = "2", authorId = "u2", authorName = "홍차전문가", authorProfileUrl = null,
                isFollowingAuthor = false, title = "다즐링 세컨드 플러쉬의 매력",
                content = "내용...", imageUrls = emptyList(),
                timeAgo = "2일 전", teaType = "홍차", brewingSummary = null, rating = 4,
                likeCount = "50", commentCount = "5", viewCount = "100", bookmarkCount = "10",
                isLiked = false, isBookmarked = false
            ),
        )

        PopularPostListContent(
            uiState = PopularPostListUiState(
                isLoading = false,
                posts = dummyPosts
            ),
            onBackClick = {},
            onPostClick = {}
        )
    }
}