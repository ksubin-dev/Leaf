package com.leafy.features.mypage.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.mypage.data.MyPageTab
import com.leafy.features.mypage.ui.component.MyPageCalendarTab
import com.leafy.features.mypage.ui.component.MyPageTopAppBar
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
                                color = if (tab == selectedTab) colors.primary else colors.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            // 3. 탭 콘텐츠 영역 (선택된 탭에 따라 콘텐츠 표시)
            when (selectedTab) {
                MyPageTab.CALENDAR -> {
                    MyPageCalendarTab(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
                else -> {
                    Text(
                        text = "${selectedTab.name.lowercase().replaceFirstChar { it.uppercase() }} Tab Content",
                        modifier = Modifier.padding(16.dp)
                        .weight(1f)
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