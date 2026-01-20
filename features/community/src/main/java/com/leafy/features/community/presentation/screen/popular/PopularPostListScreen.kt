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
import com.leafy.shared.ui.model.CommunityPostUiModel
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
