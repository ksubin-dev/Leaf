package com.leafy.features.community.ui.section


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leafy.features.community.ui.component.ExploreFollowingNoteCard
import com.leafy.features.community.ui.component.ExploreNoteUi

/**
 * 내가 팔로우한 사람들의 피드 리스트 섹션
 */
@Composable
fun ExploreFollowingFeedSection(
    modifier: Modifier = Modifier,
    notes: List<ExploreNoteUi>,
    onNoteClick: (ExploreNoteUi) -> Unit = {},
    onLikeClick: (ExploreNoteUi) -> Unit = {},
    onCommentClick: (ExploreNoteUi) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(items = notes, key = { it.id }) { note ->
            ExploreFollowingNoteCard(
                note = note,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onNoteClick(note) },
                onLikeClick = { onLikeClick(note) },
                onCommentClick = { onCommentClick(note) }
            )
        }
    }
}