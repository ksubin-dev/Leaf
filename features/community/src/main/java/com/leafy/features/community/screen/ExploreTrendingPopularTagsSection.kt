package com.leafy.features.community.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.community.ui.component.ExploreSectionHeader
import com.leafy.features.community.ui.component.ExploreTagChip
import com.leafy.features.community.ui.component.ExploreTagUi
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun ExploreTrendingPopularTagsSection(
    tags: List<ExploreTagUi>,
    modifier: Modifier = Modifier,
    onTagClick: (ExploreTagUi) -> Unit = {}
) {
    Column(modifier = modifier) {

        ExploreSectionHeader(
            title = "지금 인기 있는 태그",
            showMore = true,
            onMoreClick = { /* TODO: 태그 전체 보기 */ }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tags) { tag ->
                ExploreTagChip(
                    tag = tag,
                    onClick = onTagClick
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExploreTrendingPopularTagsSectionPreview() {
    LeafyTheme {
        val dummyTags = listOf(
            ExploreTagUi(label = "녹차"),
            ExploreTagUi(label = "우롱차"),
            ExploreTagUi(label = "가향홍차"),
            ExploreTagUi(label = "허브티"),
        )

        ExploreTrendingPopularTagsSection(tags = dummyTags)
    }
}