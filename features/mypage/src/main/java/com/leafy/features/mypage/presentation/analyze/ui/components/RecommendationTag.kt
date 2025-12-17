package com.leafy.features.mypage.presentation.analyze.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RecommendationTag(tag: String) {

    val colors = MaterialTheme.colorScheme

    val backgroundColor = when (tag) {
        "비슷한 취향" -> colors.primary
        "새로운 발견" -> colors.error
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when (tag) {
        "비슷한 취향" -> colors.onPrimary
        "새로운 발견" -> colors.onError
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Text(
        text = tag,
        color = contentColor,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}