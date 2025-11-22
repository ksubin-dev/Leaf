package com.subin.leafy.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.theme.LeafyBottomBarBackground
import com.leafy.shared.ui.theme.LeafyGreen

@Composable
fun LeafyTimerButton(
    iconRes: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp          // 🔹 기본 크기 64x64
) {
    Surface(
        modifier = modifier
            .size(size)       // 🔹 전체 버튼 사이즈 고정
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                clip = false
            ),
        color = LeafyGreen,    // 항상 초록 배경
        shape = CircleShape,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = "Timer",
                // 아이콘은 버튼보다 조금 작게 (대략 절반~2/3 정도 느낌)
                modifier = Modifier.padding(4.dp),
                tint = LeafyBottomBarBackground   // 흰색 아이콘
            )
        }
    }
}