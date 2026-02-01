package com.leafy.features.home.presentation.home

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.leafy.features.home.presentation.components.HomeTopAppBar
import com.leafy.features.home.presentation.home.components.HeroTeaImage
import com.leafy.shared.common.singleClick
import com.leafy.shared.navigation.MainNavigationRoute
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.HomeBanner
import com.subin.leafy.domain.model.QuickBrewingGuide

@Composable
fun HomeRoute(
    navController: NavController,
    onMoreRankingClick: (RankingFilter) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is HomeSideEffect.NavigateTo -> {
                    navController.navigate(effect.route)
                }
                is HomeSideEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        effect.message.asString(context),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refreshNotificationState()
    }

    HomeScreen(
        uiState = uiState,
        onSearchClick = {
            navController.navigate(MainNavigationRoute.Search)
        },
        onNotificationClick = {
            navController.navigate(MainNavigationRoute.Notification)
        },
        onProfileClick = {
            uiState.currentUserId?.takeIf { it.isNotBlank() }?.let { myId ->
                navController.navigate(MainNavigationRoute.UserProfile(userId = myId))
            }
        },
        onBannerClick = { linkUrl -> /* TODO: 웹뷰나 상세 이동 */ },
        onRankingFilterClick = viewModel::onRankingFilterSelected,
        onRankingItemClick = { postId ->
            navController.navigate(MainNavigationRoute.CommunityDetail(postId))
        },
        onMoreRankingClick = onMoreRankingClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    onBannerClick: (String) -> Unit,
    onRankingFilterClick: (RankingFilter) -> Unit,
    onRankingItemClick: (String) -> Unit,
    onMoreRankingClick: (RankingFilter) -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            HomeTopAppBar(
                userProfileUrl = uiState.userProfileUrl,
                hasUnreadNotifications = uiState.hasUnreadNotifications,
                onSearchClick = onSearchClick,
                onNotificationClick = onNotificationClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(bottom = 24.dp)
            ) {
                uiState.banner?.let { banner ->
                    HeroTeaImage(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .height(200.dp),
                        imageUrl = banner.imageUrl,
                        title = banner.title,
                        description = banner.description,
                        label = banner.label,
                        onImageClick = singleClick { onBannerClick(banner.linkUrl) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                uiState.quickGuide?.let { guide ->
                    QuickBrewingGuideSection(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        temperature = guide.temperature,
                        time = "${guide.steepingTimeSeconds / 60} min",
                        amount = "${guide.amountGrams.toInt()}g"
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                PopularTop3Section(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    selectedFilter = uiState.selectedFilter,
                    rankingList = uiState.rankingList,
                    isLoading = uiState.isRankingLoading,
                    onFilterClick = onRankingFilterClick,
                    onItemClick = onRankingItemClick,
                    onMoreClick = { onMoreRankingClick(uiState.selectedFilter) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    LeafyTheme {
        HomeScreen(
            uiState = HomeUiState(
                isLoading = false,
                userProfileUrl = null,
                banner = HomeBanner(
                    id = "1",
                    title = "이달의 차",
                    description = "제주 햇녹차의 싱그러움을 만나보세요",
                    imageUrl = "",
                    linkUrl = "",
                    label = "Limited"
                ),
                quickGuide = QuickBrewingGuide(
                    id = "1",
                    temperature = 85,
                    steepingTimeSeconds = 180,
                    amountGrams = 3.0f
                ),
                rankingList = listOf(),
                selectedFilter = RankingFilter.THIS_WEEK
            ),
            onSearchClick = {},
            onNotificationClick = {},
            onProfileClick = {},
            onBannerClick = {},
            onRankingFilterClick = {},
            onRankingItemClick = {},
            onMoreRankingClick = {}
        )
    }
}