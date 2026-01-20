package com.leafy.features.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leafy.features.search.components.SearchEmptyState
import com.leafy.shared.common.clickableSingle
import com.leafy.shared.ui.component.CommunityFeedCard
import com.leafy.shared.ui.component.CommunityTeaMasterCard

@Composable
fun SearchResultSection(
    uiState: SearchUiState,
    modifier: Modifier = Modifier,
    onPostClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onLoadMore: () -> Unit,
) {
    val listState = rememberLazyListState()

    val shouldLoadMore = remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= totalItems - 5
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) onLoadMore()
    }

    val isCurrentListEmpty = when(uiState.selectedTab) {
        SearchTab.POSTS -> uiState.postResults.isEmpty()
        SearchTab.USERS -> uiState.userResults.isEmpty()
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (!uiState.isLoading && uiState.query.isNotEmpty() && isCurrentListEmpty) {
            SearchEmptyState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (uiState.selectedTab == SearchTab.POSTS) {
                    items(uiState.postResults, key = { it.postId }) { post ->
                        CommunityFeedCard(
                            post = post,
                            onClick = { onPostClick(post.postId) },
                            onProfileClick = { onUserClick(post.authorId) }
                        )
                    }
                } else {
                    items(uiState.userResults, key = { it.userId }) { user ->
                        CommunityTeaMasterCard(
                            master = user,
                            onClick = { onUserClick(user.userId) }
                        )
                    }
                }

                if (uiState.isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }
}