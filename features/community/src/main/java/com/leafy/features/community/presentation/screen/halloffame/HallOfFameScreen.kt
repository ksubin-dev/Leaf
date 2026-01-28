package com.leafy.features.community.presentation.screen.halloffame

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.features.community.presentation.components.card.CommunityCompactCard
import com.leafy.features.community.presentation.screen.halloffame.component.HallOfFameTopCard
import com.leafy.shared.ui.model.CommunityPostUiModel
import com.leafy.shared.common.singleClick
import com.subin.leafy.domain.model.RankingPeriod

@Composable
fun HallOfFameScreen(
    onBackClick: () -> Unit,
    onPostClick: (String) -> Unit,
    viewModel: HallOfFameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is HallOfFameSideEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message.asString(context))
                }
            }
        }
    }

    HallOfFameContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onTabSelected = viewModel::updatePeriod,
        onPostClick = onPostClick,
        onBookmarkClick = viewModel::toggleBookmark
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HallOfFameContent(
    uiState: HallOfFameUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onTabSelected: (RankingPeriod) -> Unit,
    onPostClick: (String) -> Unit,
    onBookmarkClick: (CommunityPostUiModel) -> Unit
) {
    val tabs = mapOf(
        RankingPeriod.WEEKLY to "주간 인기",
        RankingPeriod.MONTHLY to "이달의 베스트",
        RankingPeriod.ALL_TIME to "역대 기록"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "명예의 전당",
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) } // [연결]
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            PrimaryTabRow(
                selectedTabIndex = uiState.selectedPeriod.ordinal,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {},
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(uiState.selectedPeriod.ordinal),
                        color = MaterialTheme.colorScheme.primary,
                        width = Dp.Unspecified,
                        shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                    )
                }
            ) {
                RankingPeriod.entries.forEach { period ->
                    val isSelected = uiState.selectedPeriod == period
                    Tab(
                        selected = isSelected,
                        onClick = singleClick { onTabSelected(period) },
                        text = {
                            Text(
                                text = tabs[period] ?: "",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.posts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("해당 기간의 랭킹 데이터가 없습니다.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(uiState.posts) { index, post ->
                        val rank = index + 1
                        val isMonthlyTop = (uiState.selectedPeriod == RankingPeriod.MONTHLY && index == 0)

                        if (isMonthlyTop) {
                            HallOfFameTopCard(
                                post = post,
                                onClick = { onPostClick(post.postId) },
                                onBookmarkClick = { onBookmarkClick(post) }
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                        } else {
                            CommunityCompactCard(
                                post = post,
                                rank = rank,
                                onClick = { onPostClick(post.postId) },
                                onBookmarkClick = { onBookmarkClick(post) }
                            )
                        }
                    }
                }
            }
        }
    }
}