package com.leafy.features.badges.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.badges.data.BadgeItem
import com.leafy.features.badges.ui.components.BadgeCard
import com.leafy.shared.R as SharedR

@Composable
fun BadgesScreen(
    modifier: Modifier = Modifier,
    badges: List<BadgeItem>,
    onMoreClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "나의 뱃지 현황",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "더보기 →",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onMoreClick)
            )
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = "새로운 뱃지를 획득하여 티 마스터의 여정을 완성하세요!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (badges.isEmpty()) {
                Text("아직 획득한 뱃지가 없습니다.", modifier = Modifier.padding(vertical = 16.dp))
            } else {
                badges.forEach { badge ->
                    BadgeCard(badge = badge)
                }
            }
        }

        Spacer(Modifier.height(60.dp))
    }
}

// -----------------------------------------------------------
// Preview를 위한 가짜(Dummy) 데이터 (수정됨)
// -----------------------------------------------------------

private val dummyBadges = listOf(
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

@Preview(showBackground = true)
@Composable
private fun BadgesScreenPreview() {
    BadgesScreen(badges = dummyBadges, onMoreClick = {})
}