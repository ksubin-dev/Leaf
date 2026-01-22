package com.leafy.shared.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.leafy.shared.common.singleClick
import com.leafy.shared.R as SharedR

@Composable
fun LeafyProfileImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.Transparent,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    contentDescription: String? = "프로필 이미지",
    onClick: (() -> Unit)? = null
) {
    val model = if (imageUrl.isNullOrBlank()) null else imageUrl

    val safeOnClick = if (onClick != null) {
        singleClick { onClick() }
    } else {
        null
    }

    val containerModifier = modifier
        .size(size)
        .clip(CircleShape)
        .background(backgroundColor)
        .then(
            if (borderWidth > 0.dp) Modifier.border(borderWidth, borderColor, CircleShape)
            else Modifier
        )
        .then(
            if (safeOnClick != null) Modifier.clickable(onClick = safeOnClick)
            else Modifier
        )

    Box(
        modifier = containerModifier,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            painter = painterResource(id = SharedR.drawable.ic_leaf),
            contentDescription = null,
            tint = iconTint
        )

        if (model != null) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(model)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop
            )
        }
    }
}