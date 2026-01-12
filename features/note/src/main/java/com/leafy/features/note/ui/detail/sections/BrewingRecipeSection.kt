package com.leafy.features.note.ui.detail.sections

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
        // 1. 상단 4개 정보 카드 (Grid 형태)
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
            // 두 번째 줄: 시간 | 횟수
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BrewingInfoCard(
                    iconRes = R.drawable.ic_timer,
                    label = "우림 시간",
                    value = formatSeconds(recipe.brewTimeSeconds),
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

// 초 단위를 "1분 30초" 형태로 변환하는 함수
private fun formatSeconds(seconds: Int): String {
    val min = seconds / 60
    val sec = seconds % 60
    return if (min > 0) "${min}분 ${sec}초" else "${sec}초"
}

// ----------------------------------------------------------------------
// Preview
// ----------------------------------------------------------------------
@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun BrewingRecipeSectionPreview() {
    val mockRecipe = BrewingRecipe(
        waterTemp = 95,
        leafAmount = 2.5f,
        waterAmount = 150,
        brewTimeSeconds = 270, // 4분 30초
        infusionCount = 3,
        teaware = "개완 (Gaiwan)"
    )

    LeafyTheme {
        BrewingRecipeSection(recipe = mockRecipe)
    }
}