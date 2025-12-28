package com.leafy.features.note.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.leafy.shared.ui.component.DottedRoundedBox
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.R as SharedR

/**
 * New Brewing Note 화면의 Photos 섹션에서 사용하는
 * 1칸(정사각형)짜리 포토 슬롯 컴포넌트.
 */
@Composable
fun NotePhotoSlot(
    modifier: Modifier = Modifier,
    title: String,
    imageUri: String?,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    
    DottedRoundedBox(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        cornerRadiusDp = 16f,
        borderWidthDp = 3f,
        borderColor = if (imageUri.isNullOrBlank()) colors.primary else colors.primary.copy(alpha = 0.3f)
    ) {
        // 내부 내용물
        if (!imageUri.isNullOrBlank()) {
            AsyncImage(
                model = imageUri,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.height(28.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Normal
                    ),
                    color = colors.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotePhotoSlotPreview() {
    LeafyTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            NotePhotoSlot(
                title = "Dry Leaf",
                iconRes = SharedR.drawable.ic_leaf,
                onClick = {},
                modifier = TODO(),
                imageUri = TODO()
            )
        }
    }
}