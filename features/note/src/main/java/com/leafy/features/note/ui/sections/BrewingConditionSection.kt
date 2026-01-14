package com.leafy.features.note.ui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.components.LeafyDropdownField
import com.leafy.features.note.ui.components.LeafyFieldLabel
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun BrewingConditionSection(
    modifier: Modifier = Modifier,
    waterTemp: String,
    leafAmount: String,
    brewTime: String,
    brewCount: String,
    teawareType: String,
    onWaterTempChange: (String) -> Unit,
    onLeafAmountChange: (String) -> Unit,
    onBrewTimeChange: (String) -> Unit,
    onBrewCountChange: (String) -> Unit,
    onTeawareTypeChange: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(modifier = modifier) {
        // 섹션 타이틀
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = SharedR.drawable.ic_temp),
                contentDescription = "Brewing Conditions",
                tint = colors.primary,
                modifier = Modifier
                    .height(18.dp)
                    .padding(end = 6.dp)
            )
            Text(
                text = "Brewing Conditions",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = colors.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 물 온도 + 찻잎량
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                LeafyFieldLabel(text = "Water Temp (℃)")
                OutlinedTextField(
                    value = waterTemp,
                    onValueChange = onWaterTempChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    singleLine = true
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                LeafyFieldLabel(text = "Leaf Amount (g)")
                OutlinedTextField(
                    value = leafAmount,
                    onValueChange = onLeafAmountChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 우림 시간
        LeafyFieldLabel(text = "Brewing Time")
        OutlinedTextField(
            value = brewTime,
            onValueChange = onBrewTimeChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            singleLine = true,
            trailingIcon = {
                Icon(
                    painter = painterResource(id = SharedR.drawable.ic_timer),
                    contentDescription = "Brew timer",
                    tint = colors.primary
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 우림 횟수 + 다기 종류
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                LeafyFieldLabel(text = "Infusion Count")
                OutlinedTextField(
                    value = brewCount,
                    onValueChange = onBrewCountChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    singleLine = true
                )
            }

            LeafyDropdownField(
                label = "Teaware",
                options = listOf("찻주전자", "개완", "머그", "다도 세트"),
                selected = teawareType,
                onSelectedChange = onTeawareTypeChange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BrewingConditionSectionPreview() {
    LeafyTheme {
        BrewingConditionSection(
            waterTemp = "85",
            leafAmount = "3",
            brewTime = "2분 30초",
            brewCount = "1",
            teawareType = "찻주전자",
            onWaterTempChange = {},
            onLeafAmountChange = {},
            onBrewTimeChange = {},
            onBrewCountChange = {},
            onTeawareTypeChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}