package com.leafy.features.note.ui.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.features.note.ui.common.DetailInfoRow
import com.leafy.features.note.ui.common.IntensityBar
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.BodyType
import com.subin.leafy.domain.model.SensoryEvaluation
import com.subin.leafy.domain.model.getFinishLabel

@Composable
fun SensoryEvaluationContent(
    evaluation: SensoryEvaluation,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // 1. Flavor Notes 섹션
        Text(
            text = "Flavor Notes",
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
            evaluation.selectedTags.forEach { tag ->
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = tag,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        IntensityBar(label = "Sweet", value = evaluation.sweetness)
        IntensityBar(label = "Sour", value = evaluation.sourness)
        IntensityBar(label = "Bitter", value = evaluation.bitterness)
        IntensityBar(label = "Salty", value = evaluation.saltiness)
        IntensityBar(label = "Umami", value = evaluation.umami)

        Spacer(modifier = Modifier.height(20.dp))

        DetailInfoRow(label = "Body", value = evaluation.bodyType.name)
        DetailInfoRow(label = "Finish", value = evaluation.getFinishLabel())

        Spacer(modifier = Modifier.height(24.dp))

        if (evaluation.memo.isNotBlank()) {
            Text(
                text = "Tasting Notes",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(0.5.dp,MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = evaluation.memo,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SensoryEvaluationDetailSectionPreview() {
    val mockEvaluation = SensoryEvaluation(
        selectedTags = setOf("Floral", "Sweet", "Fruity", "Woody"),
        sweetness = 7f,
        sourness = 2f,
        bitterness = 1f,
        saltiness = 0f,
        umami = 3f,
        bodyType = BodyType.FULL,
        finishLevel = 0.8f,
        memo = "The initial floral aroma is followed by a rich, honey-like sweetness. It has a very smooth full body and a long-lasting clean finish." //의 메모 스타일
    )

    LeafyTheme {
        Surface {
            SensoryEvaluationContent(evaluation = mockEvaluation)
        }
    }
}