package com.leafy.features.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.home.ui.components.HeroTeaImage
import com.leafy.features.home.ui.components.HomeTopAppBar
import com.leafy.features.home.ui.section.PopularTop3Section
import com.leafy.features.home.ui.section.QuickBrewingGuideSection
import com.leafy.features.home.ui.section.RecentMyRecordsSection
import com.leafy.features.home.ui.components.TodayPickCard
import com.leafy.shared.R
import com.leafy.shared.ui.theme.LeafyTheme


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = false)
@Composable
fun HomeScreen() {
    LeafyTheme {
        val colors = MaterialTheme.colorScheme

        Scaffold(
            topBar = {
                HomeTopAppBar(
                    onSearchClick = { /* TODO: 검색 이동 */ },
                    onNotificationClick = { /* TODO: 알림 이동 */ },
                    onProfileClick = { /* TODO: 마이페이지 이동 */ }
                )
            },
            containerColor = colors.background
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp)
            ) {
                //히어로 이미지
                HeroTeaImage(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 빠른 브루잉 가이드
                QuickBrewingGuideSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))


                //오늘의 추천 차
                TodayPickCard(
                    title = "Ti Kuan Yin Oolong",
                    description = "Perfect for afternoon relaxation",
                    rating = 4.2,
                    imageRes = R.drawable.img_recent_1,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                //지금 인기 있는 시음 기록 Top 3
                PopularTop3Section(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                //최근 나의 기록
                RecentMyRecordsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }
        }
    }
}