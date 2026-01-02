package com.leafy.features.note.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.common.DetailInfoRow
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.BrewingCondition
import com.leafy.shared.R as SharedR

@Composable
fun BrewingConditionsContent(
    condition: BrewingCondition,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BrewingItemCard(
                    icon = ImageVector.vectorResource(id = SharedR.drawable.ic_temp),
                    label = "Temperature",
                    value = formatValueWithUnit(condition.waterTemp, "°C"),
                    modifier = Modifier.weight(1f)
                )
                BrewingItemCard(
                    icon = ImageVector.vectorResource(id = SharedR.drawable.ic_leaf),
                    label = "Leaf Amount",
                    value = formatValueWithUnit(condition.leafAmount, "g"),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BrewingItemCard(
                    icon = ImageVector.vectorResource(id = SharedR.drawable.ic_timer),
                    label = "Steeping Time",
                    value = condition.brewTime.ifBlank { "-" },
                    modifier = Modifier.weight(1f)
                )
                BrewingItemCard(
                    icon = ImageVector.vectorResource(id = SharedR.drawable.ic_repeat),
                    label = "Infusions",
                    value = if (condition.brewCount.isNotBlank()) "${condition.brewCount}회" else "-",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (condition.teaware.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            DetailInfoRow(label = "Teaware", value = condition.teaware)
        }
    }
}

private fun formatValueWithUnit(value: String, unit: String): String {
    if (value.isBlank()) return "-"
    return if (value.contains(unit)) value else "$value$unit"
}

@Preview(showBackground = true)
@Composable
fun BrewingConditionsDetailSectionPreview() {
    LeafyTheme {
        BrewingConditionsContent(
            condition = BrewingCondition(
                waterTemp = "95",
                leafAmount = "2.5",
                brewTime = "4:30",
                brewCount = "3",
                teaware = "Ceramic Gaiwan"
            )
        )
    }
}