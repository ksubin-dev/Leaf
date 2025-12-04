package com.leafy.features.community.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun ExploreSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    showMore: Boolean = true,
    moreLabel: String = "더보기 →",
    onMoreClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = colors.onBackground
        )

        Spacer(modifier = Modifier.weight(1f))

        if (showMore) {
            Text(
                text = moreLabel,
                style = MaterialTheme.typography.bodySmall,
                color = colors.secondary,
                modifier = Modifier
                    .clickable(onClick = onMoreClick)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExploreSectionHeaderPreview() {
    LeafyTheme {
        ExploreSectionHeader(
            title = "이번 주 인기 노트"
        )
    }
}