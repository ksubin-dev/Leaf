package com.leafy.features.mypage.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// MyPage 관련
import com.leafy.features.mypage.data.MyPageTab
import com.leafy.features.mypage.ui.component.MyPageCalendarTab
import com.leafy.features.mypage.ui.component.MyPageTopAppBar

// AnalyzeScreen 관련 (새로운 import)
import com.leafy.features.analyze.data.BrewingPatternData
import com.leafy.features.analyze.data.TeaTypeRecord
import com.leafy.features.analyze.data.TeaRecommendation
import com.leafy.features.analyze.data.TopTeaRanking
import com.leafy.features.analyze.screen.AnalyzeScreen

// 공통 및 디자인 시스템
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.component.UserProfileContent
import com.leafy.shared.ui.theme.LeafyTheme

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyPageScreen(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    var selectedTab by remember { mutableStateOf(MyPageTab.ANALYTICS) } // 시작 탭을 ANALYTICS로 설정

    val tabs = MyPageTab.values()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MyPageTopAppBar(onSettingsClick = onSettingsClick)
        },
        containerColor = colors.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. 프로필 정보 (공통 컴포넌트)
            UserProfileContent(
                username = "TeaLover_Jane",
                bio = "Tea Enthusiast",
                sessionsCount = 127,
                notesCount = 43,
                followerCount = 120,
                followingCount = 45,
                rating = 4.2,
                badgesCount = 12,
                profileImageRes = SharedR.drawable.ic_profile_1,
                onEditProfileClick = { /* TODO: 프로필 수정 이동 */ }
            )

            // 2. 탭 선택 영역
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
                    MyPageCalendarTab(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }

                MyPageTab.ANALYTICS -> {
                    val dummyTeaRecords = listOf(
                        TeaTypeRecord("녹차", 28, colors.primary),
                        TeaTypeRecord("홍차", 35, colors.error),
                        TeaTypeRecord("우롱차", 18, colors.secondary),
                        TeaTypeRecord("백차", 12, colors.errorContainer),
                        TeaTypeRecord("말차", 5, colors.primaryContainer),
                        TeaTypeRecord("황차", 2, colors.secondaryContainer)
                    )

                    val dummyRecommendations = listOf(
                        TeaRecommendation("1", "Dragon Well Green", "Tea lover", 4.5f, "비슷한 취향", ""),
                        TeaRecommendation("2", "Iron Goddess Oolong", "Teavana", 4.7f, "새로운 발견", "")
                    )

                    val dummyTopTeas = listOf(
                        TopTeaRanking(1, "Milky Oolong", 12, 4.8f, ""),
                        TopTeaRanking(2, "Chamomile Blend", 9, 4.7f, ""),
                        TopTeaRanking(3, "Darjeeling First Flush", 7, 4.6f, "")
                    )

                    AnalyzeScreen(
                        brewingData = BrewingPatternData("85°C", "3분 30초", "4회", "오후 (14:00 - 17:00)"),
                        teaTypeRecords = dummyTeaRecords,
                        recommendations = dummyRecommendations,
                        topTeas = dummyTopTeas,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }

                // 나머지 탭 (COLLECTION, WISHLIST, BADGES)
                else -> {
                    Text(
                        text = "${selectedTab.name.lowercase().replaceFirstChar { it.uppercase() }} 탭 콘텐츠 (구현 예정)",
                        modifier = Modifier.padding(16.dp).weight(1f),
                        color = colors.onBackground
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
        MyPageScreen(onSettingsClick = {})
    }
}