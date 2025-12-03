package com.leafy.features.note.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.R as SharedR

/**
 * New Brewing Note 화면의 Photos 섹션에서 사용하는
 * 1칸(정사각형)짜리 포토 슬롯 컴포넌트.
 *
 * - 점선 테두리
 * - 가운데 아이콘 + 타이틀
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
            .aspectRatio(1f)               // 정사각형 유지
            .clickable(onClick = onClick)
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

/**
 * 재사용 가능한 점선 둥근 박스.
 * - 기본 테두리 색: LeafyGreen
 * - 기본 두께: 3dp
 * 필요하면 다른 화면에서 borderColor, borderWidthDp를 바꿔서도 사용할 수 있음.
 */
@Composable
fun DottedRoundedBox(
    modifier: Modifier = Modifier,
    cornerRadiusDp: Float = 16f,
    borderWidthDp: Float = 3f,
    content: @Composable BoxScope.() -> Unit
) {

    val colors = MaterialTheme.colorScheme
    val borderColor = colors.primary

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadiusDp.dp))
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidth = borderWidthDp.dp.toPx()
            val dashLength = 14f
            val gapLength = 6f
            val radiusPx = cornerRadiusDp.dp.toPx()

            drawRoundRect(
                color = borderColor,
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(radiusPx, radiusPx),
                style = Stroke(
                    width = strokeWidth,
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(dashLength, gapLength),
                        0f
                    )
                )
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            content = content
        )
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun NotePhotoSlotPreview() {
    LeafyTheme {

        val colors = MaterialTheme.colorScheme

        Box(
            modifier = Modifier
                .background(colors.background)
                .padding(16.dp)
        ) {
            NotePhotoSlot(
                title = "Dry Leaf",
                iconRes = SharedR.drawable.ic_leaf, // or NoteR.xxx
                onClick = {}
            )
        }
    }
}