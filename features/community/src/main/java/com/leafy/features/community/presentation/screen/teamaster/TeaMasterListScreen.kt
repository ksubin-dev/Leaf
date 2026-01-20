package com.leafy.features.community.presentation.screen.teamaster

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.component.CommunityTeaMasterCard
import com.leafy.shared.ui.model.UserUiModel
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun TeaMasterListScreen(
    viewModel: TeaMasterListViewModel,
    onBackClick: () -> Unit,
    onMasterClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    TeaMasterListContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onMasterClick = onMasterClick,
        onFollowToggle = viewModel::toggleFollow
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeaMasterListContent(
    uiState: TeaMasterListUiState,
    onBackClick: () -> Unit,
    onMasterClick: (String) -> Unit,
    onFollowToggle: (UserUiModel) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "티 마스터 추천",
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
        } else if (uiState.masters.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("추천할 티 마스터가 없습니다.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(uiState.masters) { index, master ->
                    CommunityTeaMasterCard(
                        master = master,
                        currentUserId = uiState.currentUserId,
                        onClick = { onMasterClick(master.userId) },
                        onFollowToggle = { onFollowToggle(master) }
                    )

                    if (index < uiState.masters.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TeaMasterListScreenPreview() {
    LeafyTheme {
        val dummyMasters = List(10) { index ->
            UserUiModel(
                userId = "$index",
                nickname = "티 마스터 $index",
                title = if (index % 2 == 0) "홍차 전문가" else "녹차 마니아",
                profileImageUrl = null,
                isFollowing = index % 3 == 0,
                followerCount = "${100 + index * 10}",
                expertTags = if (index % 2 == 0) listOf("홍차", "블렌딩") else emptyList()
            )
        }

        TeaMasterListContent(
            uiState = TeaMasterListUiState(
                isLoading = false,
                masters = dummyMasters,
                currentUserId = "999"
            ),
            onBackClick = {},
            onMasterClick = {},
            onFollowToggle = {}
        )
    }
}