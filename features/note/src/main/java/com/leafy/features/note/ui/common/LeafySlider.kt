package com.leafy.features.note.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.theme.LeafyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeafySlider(
    label: String,
    value: Float, // 0f ~ 5f
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..5f
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. 라벨 (고정 너비)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(60.dp)
        )

        // 2. 슬라이더 (손잡이 제거 & 점 제거)
        Slider(
            value = value,
            onValueChange = { newValue ->
                onValueChange(kotlin.math.round(newValue))
            },
            valueRange = valueRange,
            steps = 0, // 점(Tick) 제거
            colors = SliderDefaults.colors(
                thumbColor = Color.Transparent, // 썸 색상 투명하게
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant, // 빈 부분 (회색)
            ),
            thumb = {
                Spacer(modifier = Modifier.size(0.dp))
            },
            modifier = Modifier.weight(1f)
        )

        // 3. 값 표시 (우측 끝)
        Text(
            text = value.toInt().toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.End,
            modifier = Modifier
                .width(30.dp)
                .padding(start = 4.dp)
        )
    }
}


// ----------------------------------------------------------------------
// 3. 프리뷰 컴포넌트 정의
// ----------------------------------------------------------------------

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun LeafySliderPreview() {
    LeafyTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "감각 평가 슬라이더 미리보기",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 1. 단맛 (Sweetness) 테스트
            // 프리뷰에서 움직여볼 수 있게 상태(State)를 만듭니다.
            var sweetValue by remember { mutableFloatStateOf(3f) }
            LeafySlider(
                label = "단맛",
                value = sweetValue,
                onValueChange = { sweetValue = it }
            )

            // 2. 떫은맛 (Astringency) 테스트
            var astringencyValue by remember { mutableFloatStateOf(1f) }
            LeafySlider(
                label = "떫은맛",
                value = astringencyValue,
                onValueChange = { astringencyValue = it }
            )

            // 3. 바디감 (Body) 테스트 (값이 0일 때)
            var bodyValue by remember { mutableFloatStateOf(0f) }
            LeafySlider(
                label = "바디감",
                value = bodyValue,
                onValueChange = { bodyValue = it }
            )
        }
    }
}