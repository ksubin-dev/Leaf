package com.leafy.features.community.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.community.data.ExploreTab
import com.leafy.features.community.ui.component.CustomExploreTabRow
import com.leafy.features.community.ui.component.ExploreFollowingNoteUi
import com.leafy.features.community.ui.component.ExploreNoteSummaryUi
import com.leafy.features.community.ui.component.ExploreTeaMasterUi
import com.leafy.features.community.ui.section.ExploreFollowingFeedSection
import com.leafy.features.community.ui.section.ExploreTrendingRisingSection
import com.leafy.features.community.ui.section.ExploreTrendingSavedSection
import com.leafy.features.community.ui.section.ExploreTrendingTeaMasterSection
import com.leafy.features.community.ui.section.ExploreTrendingTopSection
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme


/**
 * Community 탭 메인 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CommunityScreen(
    modifier: Modifier = Modifier
) {
    LeafyTheme {

    val colors = MaterialTheme.colorScheme
    // ───── 탭 상태 ─────
    var selectedTab by remember { mutableStateOf(ExploreTab.TRENDING) }

    // ───── 더미 데이터  ─────
        val trendingTopNotes = remember {
            listOf(
                ExploreNoteSummaryUi(
                    title = "프리미엄 제주 녹차",
                    subtitle = "깔끔하고 상쾌한 맛의 일품",
                    imageRes = SharedR.drawable.ic_sample_tea_1,
                    rating = 4.8f,
                    savedCount = 234,
                    profileImageRes = SharedR.drawable.ic_profile_1,

                ),
                ExploreNoteSummaryUi(
                    title = "다즐링 퍼스트 플러시",
                    subtitle = "인도 | 홍차",
                    imageRes = SharedR.drawable.ic_sample_tea_2,
                    rating = 4.6f,
                    savedCount = 189,
                    profileImageRes = SharedR.drawable.ic_profile_2
                ),
                ExploreNoteSummaryUi(
                    title = "카모마일 허브티",
                    subtitle = "부드러운 꽃향과 허브 향",
                    imageRes = SharedR.drawable.ic_sample_tea_3,
                    rating = 4.5f,
                    savedCount = 142,
                    profileImageRes = SharedR.drawable.ic_profile_3
                )
            )
        }

        val trendingRisingNotes = remember {
            listOf(
                ExploreNoteSummaryUi(
                    title = "자스민 그린티",
                    subtitle = "은은한 꽃향이 매력적",
                    imageRes = SharedR.drawable.ic_sample_tea_2,
                    rating = 4.7f,
                    savedCount = 120,
                    profileImageRes = SharedR.drawable.ic_profile_4,
                    authorName = "TeaLover",
                    likeCount = 35,
                    isLiked = true
                ),
                ExploreNoteSummaryUi(
                    title = "카모마일 허브티",
                    subtitle = "편안한 밤을 위한 한 잔",
                    imageRes = SharedR.drawable.ic_sample_tea_3,
                    rating = 4.6f,
                    savedCount = 98,
                    profileImageRes = SharedR.drawable.ic_profile_5,
                    authorName = "ZenMaster",
                    likeCount = 18,
                    isLiked = false
                ),
                ExploreNoteSummaryUi(
                    title = "루이보스 바닐라",
                    subtitle = "부드러운 루이보스 · 허브티",
                    imageRes = SharedR.drawable.ic_sample_tea_1,
                    rating = 4.5f,
                    savedCount = 87,
                    profileImageRes = SharedR.drawable.ic_profile_1,
                    authorName = "RooibosFan",
                    likeCount = 22,
                    isLiked = true
                )
            )
        }

    val trendingSavedNotes = remember {
        listOf(
            ExploreNoteSummaryUi(
                title = "다즐링 퍼스트 플러시",
                subtitle = "인도 | 홍차",
                imageRes = SharedR.drawable.ic_sample_tea_2,
                rating = 4.8f,
                savedCount = 1200
            ),
            ExploreNoteSummaryUi(
                title = "백모단 화이트티",
                subtitle = "중국 | 백차",
                imageRes = SharedR.drawable.ic_sample_tea_7,
                rating = 4.7f,
                savedCount = 987
            ),
            ExploreNoteSummaryUi(
                title = "루이보스 바닐라",
                subtitle = "남아공 | 허브티",
                imageRes = SharedR.drawable.ic_sample_tea_6,
                rating = 4.2f,
                savedCount = 854
            ),
        )
    }

    val trendingMasters = remember {
        listOf(
            ExploreTeaMasterUi(
                profileImageRes = SharedR.drawable.ic_profile_4,
                name = "그린티 마니아",
                title = "녹차 & 말차 전문가",
                isFollowing = false
            ),
            ExploreTeaMasterUi(
                profileImageRes = SharedR.drawable.ic_profile_5,
                name = "허브티 큐레이터",
                title = "허브티 & 웰니스 컨설턴트",
                isFollowing = false
            )
        )
    }

    val followingFeed = remember {
        listOf(
            ExploreFollowingNoteUi.sample1(),
            ExploreFollowingNoteUi.sample2(),
            ExploreFollowingNoteUi.sample3()
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        topBar = {
            Column {
                // ───── 상단 바 (Explore + 검색 아이콘) ─────
                androidx.compose.material3.TopAppBar(
                    title = {
                        Text(
                            text = "Explore",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = colors.primary
                        )
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: 검색 */ }) {
                            Icon(
                                painter = painterResource(id = SharedR.drawable.ic_search),
                                contentDescription = "Search",
                                tint = colors.primary
                            )
                        }
                    }
                )

                // ───── 탭 바 ─────
                CustomExploreTabRow(
                    selectedTab = selectedTab,
                    onTabSelected = { newTab -> selectedTab = newTab }
                )
            }
        }
    ) { paddingValues ->
        // 💡 콘텐츠 영역에 Scaffold가 제공하는 패딩(Top/Bottom)을 적용
        when (selectedTab) {
            ExploreTab.TRENDING -> {
                // Trending 탭: LazyColumn 하나만 스크롤 담당
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(vertical = 20.dp), // 상하 내부 패딩
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp), // 좌우 내부 패딩
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item { ExploreTrendingTopSection(notes = trendingTopNotes) }
                    item { ExploreTrendingRisingSection(notes = trendingRisingNotes) }
                    item { ExploreTrendingSavedSection(notes = trendingSavedNotes) }
                    item { ExploreTrendingTeaMasterSection(masters = trendingMasters) }
                }
            }

            ExploreTab.FOLLOWING -> {
                ExploreFollowingFeedSection(
                    notes = followingFeed,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                )
            }
         }
        }
    }
}
