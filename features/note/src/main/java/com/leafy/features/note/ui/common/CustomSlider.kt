package com.leafy.features.note.ui.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.theme.LeafyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    maxValue: Float = 5f
) {
    val colors = MaterialTheme.colorScheme
    val activeTrackColor = colors.primary
    val inactiveTrackColor = colors.outlineVariant
    val tickColor = colors.outline

    val trackHeight = 10.dp
    val thumbSize = 20.dp

    val interactionSource = remember { MutableInteractionSource() }

    val range = 0f..maxValue
    val steps = if (maxValue.toInt() == 1) 1 else maxValue.toInt() - 1

    val hideTicks = maxValue.toInt() == 1
    val tickColorToUse = if (hideTicks) Color.Transparent else tickColor


    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = range,
        steps = steps,
        modifier = modifier.height(48.dp),

        colors = SliderDefaults.colors(
            thumbColor = activeTrackColor,
            activeTrackColor = activeTrackColor,
            inactiveTrackColor = inactiveTrackColor,
        ),

        track = { sliderState ->
            SliderDefaults.Track(
                sliderState = sliderState,
                colors = SliderDefaults.colors(
                    activeTrackColor = activeTrackColor,
                    inactiveTrackColor = inactiveTrackColor,
                    activeTickColor = tickColorToUse,
                    inactiveTickColor = tickColorToUse,
                ),
                modifier = Modifier
                    .height(trackHeight)
                    .clip(RoundedCornerShape(trackHeight / 2))
            )
        },

        thumb = {
            SliderDefaults.Thumb(
                interactionSource = interactionSource,
                modifier = Modifier.size(thumbSize),
                colors = SliderDefaults.colors(thumbColor = activeTrackColor)
            )
        }
    )
}


// ----------------------------------------------------------------------
// 2. TasteSliderRow 컴포넌트
// ----------------------------------------------------------------------

@Composable
private fun TasteSliderRow(
    label: String,
    initialValue: Float,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    var value by remember { mutableFloatStateOf(initialValue) }

    val textColor = colors.onBackground

    Row(
        modifier = modifier.fillMaxWidth().height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = textColor),
            modifier = Modifier.width(72.dp)
        )

        CustomSlider(
            value = value,
            onValueChange = { value = it },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        )

        // 오른쪽 숫자
        Text(
            text = value.toInt().toString(),
            style = MaterialTheme.typography.bodyMedium.copy(color = textColor),
            modifier = Modifier.padding(start = 8.dp).width(20.dp)
        )
    }
}


// ----------------------------------------------------------------------
// 3. 프리뷰 컴포넌트 정의
// ----------------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun MyCustomSliderPreview() {
    LeafyTheme  {
        val colors = MaterialTheme.colorScheme

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // 다양한 값을 가진 슬라이더 목록 표시
            Text(
                text = "Taste Intensity (Custom Slider Preview)",
                style = MaterialTheme.typography.titleMedium,
                color = colors.secondary // 갈색 계열
            )
            Spacer(modifier = Modifier.height(16.dp))

            TasteSliderRow(label = "Sweet", initialValue = 4f)

            Spacer(modifier = Modifier.height(24.dp))

            //(Clean/Astringent 텍스트를 가진 슬라이더) , 양자택일
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                var finishValue by remember { mutableFloatStateOf(0f) }

                Text(text = "Clean", style = MaterialTheme.typography.labelSmall)
                CustomSlider(
                    value = finishValue,
                    onValueChange = { finishValue = it },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    maxValue = 1f
                )
                Text(text = "Astringent", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}