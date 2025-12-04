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
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme


/**
 * Community 탭 메인 화면
 *
 * - 상단 탭: [Trending] / [Following]
 * - Trending 탭:
 *      - 이번 주 인기 노트
 *      - 지금 급상승 중
 *      - 가장 많이 저장된 노트
 *      - 이번 달 티 마스터 추천
 * - Following 탭:
 *      - 팔로우한 유저들의 최신 브루잉 노트 피드
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    // ───── 탭 상태 ─────
    var selectedTab by remember { mutableStateOf(ExploreTab.TRENDING) }

    // ───── 더미 데이터 (나중에 ViewModel로 교체) ─────
    val trendingTopNotes = remember {
        listOf(
            ExploreNoteSummaryUi(
                title = "프리미엄 제주 녹차",
                subtitle = "깔끔하고 상쾌한 맛의 일품",
                imageRes = SharedR.drawable.ic_sample_tea_1,
                rating = 4.8f,
                reviewCount = 234,
                profileImageRes = SharedR.drawable.ic_profile_1
            ),
            ExploreNoteSummaryUi(
                title = "다즐링 퍼스트 플러시",
                subtitle = "인도 | 홍차",
                imageRes = SharedR.drawable.ic_sample_tea_2,
                rating = 4.6f,
                reviewCount = 189,
                profileImageRes = SharedR.drawable.ic_profile_2
            ),
            ExploreNoteSummaryUi(
                title = "카모마일 허브티",
                subtitle = "부드러운 꽃향과 허브 향",
                imageRes = SharedR.drawable.ic_sample_tea_3,
                rating = 4.5f,
                reviewCount = 142,
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
                reviewCount = 120
            ),
            ExploreNoteSummaryUi(
                title = "카모마일 허브티",
                subtitle = "편안한 밤을 위한 한 잔",
                imageRes = SharedR.drawable.ic_sample_tea_3,
                rating = 4.6f,
                reviewCount = 98
            ),
            ExploreNoteSummaryUi(
                title = "루이보스 바닐라",
                subtitle = "부드러운 루이보스 · 허브티",
                imageRes = SharedR.drawable.ic_sample_tea_1,
                rating = 4.5f,
                reviewCount = 87
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
                reviewCount = 1200
            ),
            ExploreNoteSummaryUi(
                title = "백모단 화이트티",
                subtitle = "중국 | 백차",
                imageRes = SharedR.drawable.ic_sample_tea_7,
                rating = 4.7f,
                reviewCount = 987
            ),
            ExploreNoteSummaryUi(
                title = "루이보스 바닐라",
                subtitle = "남아공 | 허브티",
                imageRes = SharedR.drawable.ic_sample_tea_6,
                rating = 4.2f,
                reviewCount = 854
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
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
        CustomExploreTabRow( // 💡 ExploreTabRow 대신 CustomExploreTabRow로 변경
            selectedTab = selectedTab, // 현재 선택된 탭 전달
            onTabSelected = { newTab ->
                selectedTab = newTab // 탭 클릭 시 상태 업데이트
            },
            // 필요한 경우 Modifier 추가
        )

        when (selectedTab) {
            ExploreTab.TRENDING -> {
                // Trending 탭: LazyColumn 하나만 스크롤 담당
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
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
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                )
            }
        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CommunityScreenPreview() {
    LeafyTheme {
        CommunityScreen()
    }
}