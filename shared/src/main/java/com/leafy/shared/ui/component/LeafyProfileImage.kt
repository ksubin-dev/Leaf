package com.leafy.shared.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.leafy.shared.R as SharedR

@Composable
fun LeafyProfileImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.Transparent
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "Profile Image",
        placeholder = painterResource(id = SharedR.drawable.ic_profile_1),
        error = painterResource(id = SharedR.drawable.ic_profile_3),
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .then(
                if (borderWidth > 0.dp) Modifier.border(borderWidth, borderColor, CircleShape)
                else Modifier
            ),
        contentScale = ContentScale.Crop
    )
}