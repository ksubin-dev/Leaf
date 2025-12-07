package com.leafy.features.home.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.shared.R
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.features.home.data.TeaRankingItem


@Composable
fun PopularTop3Section(
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    val items = listOf(
        TeaRankingItem(
            rank = 1,
            name = "Premium Sencha",
            typeCountry = "녹차 · 일본",
            rating = 4.8,
            ratingCount = 234,
            imageRes = R.drawable.img_rank_1,
            badgeColor = colors.primary
        ),
        TeaRankingItem(
            rank = 2,
            name = "Earl Grey Supreme",
            typeCountry = "홍차 · 영국",
            rating = 4.7,
            ratingCount = 189,
            imageRes = R.drawable.img_rank_2,
            badgeColor = colors.secondary
        ),
        TeaRankingItem(
            rank = 3,
            name = "Jasmine Pearl",
            typeCountry = "가향차 · 중국",
            rating = 4.6,
            ratingCount = 156,
            imageRes = R.drawable.img_rank_3,
            badgeColor = colors.secondary
        )
    )

    Column(modifier = modifier) {
        Text(
            text = "지금 인기 있는 시음 기록 Top 3",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            ),
            color = colors.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 카테고리 필터 칩 (이번 주 / 녹차 / 가향차 / ...)
        FilterChipRow()

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items.forEach { item ->
                RankedTeaRow(item = item)
            }
        }
    }
}

@Composable
private fun FilterChipRow() {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LeafyFilterChip(text = "이번 주", selected = true)
        LeafyFilterChip(text = "녹차", selected = false)
        LeafyFilterChip(text = "가향차", selected = false)
        LeafyFilterChip(text = "홍차", selected = false)
        LeafyFilterChip(text = "밀크티", selected = false)
    }
}


@Preview(showBackground = true)
@Composable
private fun PopularTop3SectionPreview() {
    LeafyTheme {
        PopularTop3Section(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}