package com.leafy.features.community.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.component.LeafyProfileImage

@Composable
fun StackedProfileImages(
    profileUrls: List<String?>,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    maxVisible: Int = 3
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            profileUrls.take(maxVisible).forEachIndexed { index, url ->
                LeafyProfileImage(
                    imageUrl = url,
                    size = size,
                    borderWidth = 1.5.dp,
                    borderColor = Color.White,
                    modifier = Modifier.offset(x = (index * (size.value * 0.6f)).dp)
                )
            }
        }

        if (profileUrls.size > maxVisible) {
            Spacer(modifier = Modifier.width((maxVisible * size.value * 0.6f + 8).dp))
            Text(
                text = "${profileUrls.size}명이 좋아합니다",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}