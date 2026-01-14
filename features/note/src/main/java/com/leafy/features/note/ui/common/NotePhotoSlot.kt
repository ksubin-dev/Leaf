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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        borderColor = colors.primary
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
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
                onClick = {}
            )
        }
    }
}