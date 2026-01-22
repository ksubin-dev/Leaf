package com.leafy.features.note.ui.sections.create

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
import com.leafy.features.note.ui.common.NoteInputTextField
import com.leafy.features.note.ui.common.NoteSectionHeader
import com.leafy.shared.R
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LeafyDropdownField
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.TeawareType

@Composable
fun BrewingRecipeSection(
    modifier: Modifier = Modifier,
    waterTemp: String,
    leafAmount: String,
    waterAmount: String,
    brewTime: String,
    infusionCount: String,
    teaware: TeawareType,
    onWaterTempChange: (String) -> Unit,
    onLeafAmountChange: (String) -> Unit,
    onWaterAmountChange: (String) -> Unit,
    onBrewTimeChange: (String) -> Unit,
    onInfusionCountChange: (String) -> Unit,
    onTeawareChange: (TeawareType) -> Unit,
    onTimerClick: () -> Unit
) {

    Column(modifier = modifier.fillMaxWidth()) {
        NoteSectionHeader(
            icon = painterResource(id = R.drawable.ic_temp),
            title = "우림 조건"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NoteInputTextField(
                value = waterTemp,
                onValueChange = onWaterTempChange,
                label = "온도(℃)",
                placeholder = "85",
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            NoteInputTextField(
                value = leafAmount,
                onValueChange = onLeafAmountChange,
                label = "찻잎(g)",
                placeholder = "3",
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            NoteInputTextField(
                value = waterAmount,
                onValueChange = onWaterAmountChange,
                label = "물(ml)",
                placeholder = "150",
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
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
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(onClick = singleClick { onTimerClick() }),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_timer),
                    contentDescription = "Open Timer",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NoteInputTextField(
                value = infusionCount,
                onValueChange = onInfusionCountChange,
                label = "횟수",
                placeholder = "1",
                modifier = Modifier.weight(0.3f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Box(
                modifier = Modifier
                    .weight(0.65f)
                    .padding(bottom = 4.dp)
            ) {
                LeafyDropdownField(
                    label = "사용 다구",
                    options = TeawareType.entries.toList(),
                    selectedOption = teaware,
                    onOptionSelected = onTeawareChange,
                    labelMapper = { it.label },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun BrewingRecipeSectionPreview() {
    LeafyTheme {
        var temp by remember { mutableStateOf("") }
        var leaf by remember { mutableStateOf("") }
        var water by remember { mutableStateOf("") }
        var time by remember { mutableStateOf("") }
        var count by remember { mutableStateOf("1") }
        var ware by remember { mutableStateOf(TeawareType.MUG) }

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
                onTimerClick = {}
            )
        }
    }
}