package com.leafy.features.note.ui.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.common.NoteDropdownField
import com.leafy.features.note.ui.common.NoteInputTextField
import com.leafy.features.note.ui.common.NoteSectionHeader
import com.leafy.shared.R
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun BrewingRecipeSection(
    waterTemp: String,
    onWaterTempChange: (String) -> Unit,
    leafAmount: String,
    onLeafAmountChange: (String) -> Unit,
    waterAmount: String,
    onWaterAmountChange: (String) -> Unit,
    brewTime: String,
    onBrewTimeChange: (String) -> Unit,
    infusionCount: String,
    onInfusionCountChange: (String) -> Unit,
    teaware: String,
    onTeawareChange: (String) -> Unit,
    onTimerClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val teawareOptions = listOf("찻주전자", "개완", "머그컵", "유리포트", "기타")

    Column(modifier = modifier.fillMaxWidth()) {
        NoteSectionHeader(
            icon = painterResource(id = R.drawable.ic_temp),
            title = "우림 조건"
        )

        // 1. Row: 온도 | 찻잎 양
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NoteInputTextField(
                value = waterTemp,
                onValueChange = onWaterTempChange,
                label = "물 온도 (℃)",
                placeholder = "85",
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            NoteInputTextField(
                value = leafAmount,
                onValueChange = onLeafAmountChange,
                label = "찻잎 (g)",
                placeholder = "3",
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        // 2. Row: 물 양 | 시간 (+ 타이머 버튼)
        Row(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            NoteInputTextField(
                value = waterAmount,
                onValueChange = onWaterAmountChange,
                label = "물 양 (ml)",
                placeholder = "150",
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // 시간 입력 필드 + 타이머 버튼을 묶는 Row
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                NoteInputTextField(
                    value = brewTime,
                    onValueChange = onBrewTimeChange,
                    label = "시간 (초)",
                    placeholder = "180",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Box(
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onTimerClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_timer),
                        contentDescription = "Open Timer",
                        tint = Color.White
                    )
                }
            }
        }

        // 3. Row: 횟수 | 다구
        Row(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NoteInputTextField(
                value = infusionCount,
                onValueChange = onInfusionCountChange,
                label = "우림 횟수",
                placeholder = "1",
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            NoteDropdownField(
                label = "사용 다구",
                options = teawareOptions,
                selectedOption = teaware.ifEmpty { teawareOptions[0] },
                onOptionSelected = onTeawareChange,
                labelMapper = { it },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun BrewingRecipeSectionPreview() {
    LeafyTheme {
        // 프리뷰에서 입력 상태를 테스트하기 위한 변수들
        var temp by remember { mutableStateOf("") }
        var leaf by remember { mutableStateOf("") }
        var water by remember { mutableStateOf("") }
        var time by remember { mutableStateOf("") }
        var count by remember { mutableStateOf("1") }
        var ware by remember { mutableStateOf("찻주전자") }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            BrewingRecipeSection(
                waterTemp = temp,
                onWaterTempChange = { temp = it },
                leafAmount = leaf,
                onLeafAmountChange = { leaf = it },
                waterAmount = water,
                onWaterAmountChange = { water = it },
                brewTime = time,
                onBrewTimeChange = { time = it },
                infusionCount = count,
                onInfusionCountChange = { count = it },
                teaware = ware,
                onTeawareChange = { ware = it },
                onTimerClick = {
                    // 프리뷰에서는 클릭 시 로그를 찍거나 아무 동작 안 함
                }
            )
        }
    }
}