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
import com.leafy.features.mypage.data.MyPageTab
import com.leafy.features.mypage.ui.component.MyPageTopAppBar
import com.leafy.features.analyze.screen.AnalyzeScreen
import com.leafy.features.analyze.data.BrewingPatternData
import com.leafy.features.analyze.data.TeaTypeRecord
import com.leafy.features.analyze.data.TeaRecommendation
import com.leafy.features.analyze.data.TopTeaRanking
import com.leafy.features.badges.data.BadgeItem
import com.leafy.features.badges.screen.BadgesScreen
import com.leafy.features.calendar.screen.CalendarScreen
import com.leafy.features.collection.data.TeaCollectionItem
import com.leafy.features.wishlist.screen.WishlistScreen
import com.leafy.features.wishlist.data.WishlistItem
import com.leafy.features.wishlist.data.SavedCommunityNote
import com.leafy.features.collection.screen.CollectionScreen
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.component.UserProfileContent
import com.leafy.shared.ui.theme.LeafyTheme


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyPageScreen(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit,
    onEditProfileClick: () -> Unit
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
            UserProfileContent(
                username = "TeaLover_Jane",
                bio = "Tea Enthusiast",
                notesCount = 127,
                postsCount = 43,
                followerCount = 120,
                followingCount = 45,
                rating = 4.2,
                badgesCount = 12,
                profileImageRes = SharedR.drawable.ic_profile_1,
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

            when (selectedTab) {
                MyPageTab.CALENDAR -> {
                    CalendarScreen(modifier = Modifier.fillMaxWidth().weight(1f))
                }

                MyPageTab.ANALYTICS -> {
                    val dummyAnalyzeRecords = createDummyAnalyzeRecords()
                    AnalyzeScreen(
                        brewingData = BrewingPatternData("85°C", "3분 30초", "4회", "오후 (14:00 - 17:00)"),
                        teaTypeRecords = dummyAnalyzeRecords.teaTypeRecords,
                        recommendations = dummyAnalyzeRecords.recommendations,
                        topTeas = dummyAnalyzeRecords.topTeas,
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }

                MyPageTab.WISHLIST -> {
                    val dummyWishlistData = createDummyWishlistData()
                    WishlistScreen(
                        wishlistItems = dummyWishlistData.wishlistItems,
                        savedNotes = dummyWishlistData.savedNotes,
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }

                MyPageTab.COLLECTION -> {
                    val dummyCollectionItems = createDummyCollectionData()
                    CollectionScreen(
                        items = dummyCollectionItems,
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }

                MyPageTab.BADGES -> {
                    val dummyBadges = createDummyBadgesData()
                    BadgesScreen(
                        badges = dummyBadges,
                        onMoreClick = { /* TODO: 뱃지 전체보기 이동 */ },
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }
            }
        }
    }
}


private data class AnalyzeData(
    val teaTypeRecords: List<TeaTypeRecord>,
    val recommendations: List<TeaRecommendation>,
    val topTeas: List<TopTeaRanking>
)

@Composable
private fun createDummyAnalyzeRecords(): AnalyzeData {
    val dummyTeaRecords = listOf(
        TeaTypeRecord("녹차", 28),
        TeaTypeRecord("홍차", 35),
        TeaTypeRecord("우롱차", 18),
        TeaTypeRecord("백차", 12),
        TeaTypeRecord("말차", 5),
        TeaTypeRecord("황차", 2)
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
    return AnalyzeData(dummyTeaRecords, dummyRecommendations, dummyTopTeas)
}

private data class WishlistData(
    val wishlistItems: List<WishlistItem>,
    val savedNotes: List<SavedCommunityNote>
)

private fun createDummyCollectionData(): List<TeaCollectionItem> {
    return listOf(
        TeaCollectionItem("1", "Earl Grey Supreme", "Twinings", "Plenty", SharedR.drawable.ic_sample_collection_tea_1),
        TeaCollectionItem("2", "Sencha Green", "Ippodo Tea", "Low", SharedR.drawable.ic_sample_collection_tea_2),
        TeaCollectionItem("3", "High Mountain Oolong", "Mountain Tea", "Empty", SharedR.drawable.ic_sample_collection_tea_3),
        TeaCollectionItem("4", "Chamomile Dreams", "Celestial Seasonings", "Plenty", SharedR.drawable.ic_sample_collection_tea_4),
        TeaCollectionItem("5", "Assam Black", "Vahdam Teas", "Plenty", SharedR.drawable.ic_sample_tea_5),
        TeaCollectionItem("6", "Jasmine Green", "Teavana", "Low", SharedR.drawable.ic_sample_tea_8),
        TeaCollectionItem("7", "Peppermint Herbal", "Bigelow", "Plenty", SharedR.drawable.ic_sample_tea_7),
    )
}

private fun createDummyBadgesData(): List<BadgeItem> {
    return listOf(
        BadgeItem(
            id = "1",
            title = "녹차 러버",
            description = "녹차 10회 기록",
            isAcquired = true,
            iconRes = SharedR.drawable.ic_badges_1

        ),
        BadgeItem(
            id = "2",
            title = "100번째 기록",
            description = "총 100번째 시음 노트 작성",
            isAcquired = false,
            progress = "잠금 해제 필요",
            iconRes = SharedR.drawable.ic_badges_2

        ),
        BadgeItem(
            id = "3",
            title = "티 마스터",
            description = "모든 차 종류 섭렵",
            isAcquired = false,
            progress = "현재 진행률 5/10",
            iconRes = SharedR.drawable.ic_badge

        ),
    )
}

private fun createDummyWishlistData(): WishlistData {
    val dummyWishlistItems = listOf(
        WishlistItem("1", "다즐링 퍼스트 플러시", "인도 | 홍차", SharedR.drawable.ic_sample_tea_4),
        WishlistItem("2", "백모단 화이트티", "중국 | 백차", SharedR.drawable.ic_sample_tea_5),
        WishlistItem("3", "루이보스 바닐라", "남아공 | 허브티", SharedR.drawable.ic_sample_tea_6),
    )
    val dummySavedNotes = listOf(
        SavedCommunityNote(
            "n1", "Dragon Well Green Tea", "Fresh, grassy notes with subtle sweetness...", 4.5f, 124,
            SharedR.drawable.ic_sample_tea_13, SharedR.drawable.ic_profile_1
        ),
        SavedCommunityNote(
            "n2", "Earl Grey Supreme", "Citrusy bergamot with floral lavender hints...", 4.0f, 98,
            SharedR.drawable.ic_sample_tea_14, SharedR.drawable.ic_profile_2
        ),
        SavedCommunityNote(
            "n3", "Ti Kuan Yin Oolong", "Complex floral aroma with roasted finish...", 4.8f, 87,
            SharedR.drawable.ic_sample_tea_15, SharedR.drawable.ic_profile_3
        )
    )
    return WishlistData(dummyWishlistItems, dummySavedNotes)
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

