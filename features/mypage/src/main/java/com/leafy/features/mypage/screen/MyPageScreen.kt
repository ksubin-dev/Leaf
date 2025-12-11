package com.leafy.features.mypage.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.mypage.data.MyPageTab
import com.leafy.features.mypage.ui.component.MyPageTopAppBar
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.component.UserProfileContent
import com.leafy.shared.ui.theme.LeafyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    var selectedTab by remember { mutableStateOf(MyPageTab.CALENDAR) }
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
                .verticalScroll(rememberScrollState())
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

            SecondaryScrollableTabRow(
                selectedTabIndex = tabs.indexOf(selectedTab),
                modifier = Modifier.fillMaxWidth(),
                containerColor = colors.surface,
                edgePadding = 16.dp
            ) {
                tabs.forEach { tab ->
                    Tab(
                        selected = tab == selectedTab,
                        onClick = { selectedTab = tab },
                        modifier = Modifier.padding(horizontal = 4.dp),

                        // 아이콘 컴포넌트
                        icon = if (tab.iconRes != null) {
                            {
                                Icon(
                                    painter = painterResource(id = tab.iconRes),
                                    contentDescription = tab.name,
                                    modifier = Modifier.size(20.dp),
                                    tint = if (tab == selectedTab) colors.primary else colors.onSurfaceVariant
                                )
                            }
                        } else null,

                        // 텍스트 컴포넌트
                        text = {
                            Text(
                                text = tab.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (tab == selectedTab) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (tab == selectedTab) colors.primary else colors.onSurfaceVariant // 선택된 탭 텍스트 색상 변경
                            )
                        }
                    )
                }
            }
            // 3. 탭 콘텐츠 영역 (선택된 탭에 따라 콘텐츠 표시)
            when (selectedTab) {
                MyPageTab.CALENDAR -> {
                    // TODO: MyPageCalendarTab 컴포넌트 호출 예정
                    Text(
                        text = "Calendar Tab Content Here (캘린더 탭 내용)",
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {
                    Text(
                        text = "${selectedTab.name.lowercase().replaceFirstChar { it.uppercase() }} Tab Content",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MyPageScreenPreview() {
    LeafyTheme {
        MyPageScreen(onSettingsClick = {})
    }
}