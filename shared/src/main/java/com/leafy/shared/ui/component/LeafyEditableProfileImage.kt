package com.leafy.shared.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LeafyEditableProfileImage(
    modifier: Modifier = Modifier,
    imageUrl: Any?,
    size: Dp = 120.dp,
    onImageClick: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {

        LeafyProfileImage(
            imageUrl = imageUrl.toString(),
            modifier = Modifier,
            size = size,
            borderWidth = 1.dp,
            borderColor = MaterialTheme.colorScheme.outlineVariant,
            onClick = onImageClick,
            contentDescription = "프로필 사진 변경"
        )

        val iconContainerSize = (size.value * 0.26f).dp.coerceAtLeast(24.dp)
        val iconSize = (iconContainerSize.value * 0.6f).dp

        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 0.dp)
                .size(iconContainerSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}
