package com.leafy.features.community.screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leafy.features.community.ui.component.ExploreFollowingNoteCard
import com.leafy.features.community.ui.component.ExploreFollowingNoteUi

/**
 * 내가 팔로우한 사람들의 피드 리스트
 */
@Composable
fun ExploreFollowingFeedSection(
    notes: List<ExploreFollowingNoteUi>,
    modifier: Modifier = Modifier,
    onNoteClick: (ExploreFollowingNoteUi) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 💡 수정된 부분: forEach 대신 LazyColumn의 items 람다 사용
        items(notes) { note ->
            ExploreFollowingNoteCard(
                note = note,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onNoteClick(note) }
            )
        }
    }
}