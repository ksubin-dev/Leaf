package com.leafy.features.note.ui.sections.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.common.DetailInfoRow
import com.leafy.features.note.ui.common.DetailSectionCard
import com.leafy.shared.ui.component.LeafyChip
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.BodyType
import com.subin.leafy.domain.model.FlavorTag
import com.subin.leafy.domain.model.SensoryEvaluation

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SensoryEvaluationSection(
    evaluation: SensoryEvaluation,
    modifier: Modifier = Modifier
) {
    DetailSectionCard(
        title = "감각 평가 (Sensory Evaluation)",
        modifier = modifier
    ) {
        if (evaluation.flavorTags.isNotEmpty()) {
            Text(
                text = "향 (Flavor Notes)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                evaluation.flavorTags.forEach { tag ->
                    LeafyChip(
                        text = tag.label,
                        isSelected = true,
                        onClick = {}
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(bottom = 12.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }

        SensoryIntensityBar(label = "단맛", value = evaluation.sweetness)
        SensoryIntensityBar(label = "신맛", value = evaluation.sourness)
        SensoryIntensityBar(label = "쓴맛", value = evaluation.bitterness)
        SensoryIntensityBar(label = "떫은맛", value = evaluation.astringency)
        SensoryIntensityBar(label = "감칠맛", value = evaluation.umami)

        Spacer(modifier = Modifier.height(20.dp))

        val bodyLabel = when(evaluation.body) {
            BodyType.LIGHT -> "가벼움 (Light)"
            BodyType.MEDIUM -> "보통 (Medium)"
            BodyType.FULL -> "묵직함 (Full)"
        }
        DetailInfoRow(label = "바디감", value = bodyLabel)

        val finishLabel = "${evaluation.finishLevel} / 5"
        DetailInfoRow(label = "여운", value = finishLabel)

        Spacer(modifier = Modifier.height(24.dp))

        if (evaluation.memo.isNotBlank()) {
            Text(
                text = "시음 노트 (Tasting Notes)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Text(
                    text = evaluation.memo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun SensoryEvaluationSectionPreview() {
    val mockEvaluation = SensoryEvaluation(
        flavorTags = listOf(FlavorTag.FLORAL, FlavorTag.FRUITY, FlavorTag.GREENISH),
        sweetness = 4,
        sourness = 1,
        bitterness = 2,
        astringency = 3,
        umami = 5,
        body = BodyType.FULL,
        finishLevel = 4,
        memo = "첫맛은 꽃향기가 강하고, 끝맛에 꿀 같은 단맛이 길게 남습니다. 바디감이 묵직해서 아침에 마시기 좋습니다."
    )

    LeafyTheme {
        SensoryEvaluationSection(evaluation = mockEvaluation)
    }
}