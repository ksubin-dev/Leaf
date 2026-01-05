package com.leafy.features.community.ui.section

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.community.ui.component.ExploreSavedNoteCard
import com.leafy.features.community.ui.component.ExploreNoteUi
import com.leafy.shared.ui.component.LeafySectionHeader
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun ExploreTrendingSavedSection(
    modifier: Modifier = Modifier,
    notes: List<ExploreNoteUi>,
    onNoteClick: (ExploreNoteUi) -> Unit = {},
    onSaveToggle: (ExploreNoteUi) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        LeafySectionHeader(
            title = "가장 많이 저장된 노트",
            titleStyle = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            onMoreClick = { /* 전체보기 이동 */ }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            notes.forEach { note ->
                ExploreSavedNoteCard(
                    note = note,
                    onClick = { onNoteClick(note) },
                    onSaveClick = { onSaveToggle(note) }
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun ExploreTrendingSavedSectionPreview() {
    LeafyTheme {
        val sample = listOf(
            ExploreNoteUi(
                id = "1",
                title = "다즐링 퍼스트 플러시",
                subtitle = "인도 | 홍차",
                imageUrl = null,
                rating = 4.1f,
                savedCount = 1200
            ),
            ExploreNoteUi(
                id = "2",
                title = "백모단 화이트티",
                subtitle = "중국 | 백차",
                imageUrl = null,
                rating = 4.3f,
                savedCount = 987
            ),
            ExploreNoteUi(
                id = "3",
                title = "루이보스 바닐라",
                subtitle = "남아공 | 허브티",
                imageUrl = null,
                rating = 4.2f,
                savedCount = 854
            ),
        )
        ExploreTrendingSavedSection(
            modifier = Modifier.padding(16.dp),
            notes = sample
        )
    }
}