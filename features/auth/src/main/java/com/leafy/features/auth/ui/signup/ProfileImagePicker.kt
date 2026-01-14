package com.leafy.features.auth.ui.signup

import android.net.Uri
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
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.component.LeafyProfileImage

@Composable
fun ProfileImagePicker(
    modifier: Modifier = Modifier,
    profileImageUri: Uri?,
    onImageClick: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        LeafyProfileImage(
            imageUrl = profileImageUri?.toString(),
            modifier = Modifier,
            size = 120.dp,
            borderWidth = 1.dp,
            borderColor = MaterialTheme.colorScheme.outlineVariant,
            onClick = onImageClick,
            contentDescription = "프로필 사진 선택"
        )
        Box(
            modifier = Modifier
                .offset(x = (-4).dp, y = (-4).dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

