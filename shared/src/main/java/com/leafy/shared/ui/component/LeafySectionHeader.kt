package com.leafy.shared.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun LeafySectionHeader(
    modifier: Modifier = Modifier,
    title: String,
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.Bold
    ),
    trailingContent: @Composable (() -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme

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

        trailingContent?.invoke()
    }
}

@Preview(showBackground = true)
@Composable
private fun LeafySectionHeaderPreview() {
    LeafyTheme {
        Column {
            LeafySectionHeader(
                title = "이번 주 인기 노트"
            )

            LeafySectionHeader(
                title = "새로운 블렌딩",
                trailingContent = {
                    Text(
                        text = "더보기 →",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            )
        }
    }
}