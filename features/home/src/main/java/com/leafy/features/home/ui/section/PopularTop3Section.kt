package com.leafy.features.home.ui.section

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.features.home.ui.components.LeafyFilterChip
import com.leafy.features.home.ui.components.RankedTeaRow
import com.leafy.features.home.viewmodel.RankingFilter
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LeafySectionHeader
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.RankingItem
import com.subin.leafy.domain.model.TeaType

@Composable
fun PopularTop3Section(
    modifier: Modifier = Modifier,
    selectedFilter: RankingFilter,
    rankingList: List<RankingItem>,
    isLoading: Boolean,
    onFilterClick: (RankingFilter) -> Unit,
    onItemClick: (String) -> Unit,
    onMoreClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(modifier = modifier) {
        LeafySectionHeader(
            title = "지금 인기 있는 시음 기록 Top 3",
            titleStyle = MaterialTheme.typography.titleMedium.copy(
                fontSize = 20.sp
            ),
            showMore = true,
            onMoreClick = singleClick { onMoreClick() },
        )

        Spacer(modifier = Modifier.height(12.dp))

        FilterChipRow(
            selectedFilter = selectedFilter,
            onFilterClick = onFilterClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(0.5f))
            }
        } else if (rankingList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "집계된 랭킹 데이터가 없습니다.",
                    color = colors.onSurfaceVariant
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                rankingList.take(3).forEach { item ->
                    RankedTeaRow(
                        item = item,
                        rank = item.rank,
                        onClick = singleClick { onItemClick(item.postId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChipRow(
    selectedFilter: RankingFilter,
    onFilterClick: (RankingFilter) -> Unit
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RankingFilter.entries.forEach { filter ->
            LeafyFilterChip(
                text = filter.label,
                selected = filter == selectedFilter,
                onClick = singleClick { onFilterClick(filter) }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PopularTop3SectionPreview() {
    LeafyTheme {
        PopularTop3Section(
            selectedFilter = RankingFilter.THIS_WEEK,
            rankingList = listOf(
                RankingItem(1, "1", "Premium Sencha", TeaType.GREEN, 5, 234, null),
                RankingItem(2, "2", "Earl Grey", TeaType.BLACK, 4, 189, null)
            ),
            isLoading = false,
            onFilterClick = {},
            onItemClick = {},
            onMoreClick = {}
        )
    }
}