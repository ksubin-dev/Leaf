package com.leafy.features.mypage.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leafy.features.mypage.data.MyPageTab
import com.leafy.features.mypage.ui.component.MyPageTopAppBar
import com.leafy.features.mypage.presentation.analyze.screen.AnalyzeScreen
import com.leafy.features.mypage.presentation.badges.screen.BadgesScreen
import com.leafy.features.mypage.presentation.calendar.screen.CalendarScreen
import com.leafy.features.mypage.presentation.wishlist.screen.WishlistScreen
import com.leafy.features.mypage.presentation.collection.screen.CollectionScreen
import com.leafy.shared.ui.component.UserProfileContent
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.features.mypage.viewmodel.MyPageViewModel



@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyPageScreen(
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = viewModel(),
    onSettingsClick: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    var selectedTab by remember { mutableStateOf(MyPageTab.CALENDAR) }
    val uiState by viewModel.uiState.collectAsState()

    val tabs = MyPageTab.values()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MyPageTopAppBar(onSettingsClick = onSettingsClick)
        },
        containerColor = colors.background
    ) { innerPadding ->

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            UserProfileContent(
                username = uiState.username,
                bio = uiState.bio,
                notesCount = uiState.stats.notesCount,
                postsCount = uiState.stats.postsCount,
                followerCount = uiState.stats.followerCount,
                followingCount = uiState.stats.followingCount,
                rating = uiState.stats.rating.toDouble(),
                badgesCount = uiState.stats.badgesCount,
                profileImageRes = uiState.profileImageRes,
                onEditProfileClick = onEditProfileClick
            )

            SecondaryScrollableTabRow(
                selectedTabIndex = tabs.indexOf(selectedTab),
                modifier = Modifier.fillMaxWidth(),
                containerColor = colors.surface,
                edgePadding = 16.dp
            ) {
                tabs.forEach { tab ->
                    val isSelected = tab == selectedTab
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        modifier = Modifier.padding(horizontal = 4.dp),

                        icon = if (tab.iconRes != null) {
                            {
                                Icon(
                                    painter = painterResource(id = tab.iconRes),
                                    contentDescription = tab.name,
                                    modifier = Modifier.size(20.dp),
                                    tint = if (isSelected) colors.primary else colors.onSurfaceVariant
                                )
                            }
                        } else null,

                        text = {
                            Text(
                                text = tab.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) colors.primary else colors.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            // 3. 탭 콘텐츠 영역
            when (selectedTab) {
                MyPageTab.CALENDAR -> {
                    CalendarScreen(modifier = Modifier.fillMaxWidth().weight(1f))
                }

                MyPageTab.ANALYTICS -> {
                    AnalyzeScreen(
                        brewingData = uiState.analyzeData.brewingData,
                        teaTypeRecords = uiState.analyzeData.teaTypeRecords,
                        recommendations = uiState.analyzeData.recommendations,
                        topTeas = uiState.analyzeData.topTeas,
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }

                MyPageTab.WISHLIST -> {
                    WishlistScreen(
                        wishlistItems = uiState.wishlistData.wishlistItems,
                        savedNotes = uiState.wishlistData.savedNotes,
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }

                MyPageTab.COLLECTION -> {
                    CollectionScreen(
                        items = uiState.collectionItems,
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }

                MyPageTab.BADGES -> {
                    BadgesScreen(
                        badges = uiState.badges,
                        onMoreClick = { /* TODO: 뱃지 전체보기 이동 */ },
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MyPageScreenPreview() {
    LeafyTheme {
        MyPageScreen(
            onSettingsClick = {},
            onEditProfileClick = {}
        )
    }
}

