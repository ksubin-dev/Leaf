package com.leafy.features.note.ui.sections.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.common.DetailInfoRow
import com.leafy.features.note.ui.common.DetailSectionCard
import com.leafy.shared.R
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.ui.utils.LeafyTimeUtils
import com.subin.leafy.domain.model.BrewingRecipe

@Composable
fun BrewingRecipeSection(
    recipe: BrewingRecipe,
    modifier: Modifier = Modifier
) {
    DetailSectionCard(
        title = "우림 조건 (Brewing Conditions)",
        modifier = modifier
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BrewingInfoCard(
                    iconRes = R.drawable.ic_temp,
                    label = "물 온도",
                    value = "${recipe.waterTemp}°C",
                    modifier = Modifier.weight(1f)
                )
                BrewingInfoCard(
                    iconRes = R.drawable.ic_leaf,
                    label = "찻잎 양",
                    value = "${recipe.leafAmount}g",
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BrewingInfoCard(
                    iconRes = R.drawable.ic_timer,
                    label = "우림 시간",
                    value = LeafyTimeUtils.formatSecondsToHangul(recipe.brewTimeSeconds),
                    modifier = Modifier.weight(1f)
                )
                BrewingInfoCard(
                    iconRes = R.drawable.ic_repeat,
                    label = "우림 횟수",
                    value = "${recipe.infusionCount}회",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (recipe.teaware.isNotBlank()) {
            DetailInfoRow(label = "사용 다구", value = recipe.teaware)
        }
        DetailInfoRow(label = "물 양", value = "${recipe.waterAmount}ml")
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun BrewingRecipeSectionPreview() {
    val mockRecipe = BrewingRecipe(
        waterTemp = 95,
        leafAmount = 2.5f,
        waterAmount = 150,
        brewTimeSeconds = 270,
        infusionCount = 3,
        teaware = "개완 (Gaiwan)"
    )

    LeafyTheme {
        BrewingRecipeSection(recipe = mockRecipe)
    }
}