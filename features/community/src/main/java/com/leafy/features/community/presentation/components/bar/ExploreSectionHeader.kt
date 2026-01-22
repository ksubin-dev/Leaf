package com.leafy.shared.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.theme.LeafyTheme


@Composable
fun LeafySectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.Bold
    ),
    showMore: Boolean = true,
    moreLabel: String = "더보기 →",
    onMoreClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    val safeOnMoreClick = singleClick { onMoreClick() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = titleStyle,
            color = colors.onBackground
        )

        Spacer(modifier = Modifier.weight(1f))

        if (showMore) {
            Text(
                text = moreLabel,
                style = MaterialTheme.typography.labelMedium,
                color = colors.secondary,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = safeOnMoreClick
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExploreSectionHeaderPreview() {
    LeafyTheme {
        LeafySectionHeader(
            title = "이번 주 인기 노트"
        )
    }
}