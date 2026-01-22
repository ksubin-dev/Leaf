package com.leafy.features.timer.ui.components

import android.view.Surface
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.utils.LeafyTimeUtils // 시간 포맷팅 유틸 가정

@Composable
fun CircularTimerDisplay(
    modifier: Modifier = Modifier,
    progress: Float,
    remainingSeconds: Int,
    currentStatusText: String
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    val formattedTime = LeafyTimeUtils.formatSecondsToTimer(remainingSeconds)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500),
        label = "TimerProgressAnimation"
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(modifier = Modifier.size(280.dp)) {
            drawCircle(
                color = trackColor,
                style = Stroke(width = 20f)
            )
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = 360 * animatedProgress,
                useCenter = false,
                style = Stroke(width = 25f, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = currentStatusText,
                style = MaterialTheme.typography.labelMedium,
                color = primaryColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview(showBackground = true, name = "타이머 상태별 모음")
@Composable
fun CircularTimerDisplayPreview() {
    LeafyTheme {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. 준비 완료 상태 (3분, 게이지 꽉 참)
                CircularTimerDisplay(
                    progress = 1.0f,
                    remainingSeconds = 180,
                    currentStatusText = "준비 완료"
                )

                // 2. 작동 중 상태 (1분 30초 남음, 게이지 50%)
                CircularTimerDisplay(
                    progress = 0.5f,
                    remainingSeconds = 90,
                    currentStatusText = "우리는 중..."
                )
                // 3. 완료 상태 (0초, 게이지 없음)
                CircularTimerDisplay(
                    progress = 0.0f,
                    remainingSeconds = 0,
                    currentStatusText = "우림 완료!"
                )

            }
        }
    }