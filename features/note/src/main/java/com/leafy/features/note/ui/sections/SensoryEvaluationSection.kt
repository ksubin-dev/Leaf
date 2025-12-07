package com.leafy.features.note.ui.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.components.* // 분리된 슬라이더/버튼 컴포넌트 Import
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SensoryEvaluationSection(
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    // 상태 변수들을 여기에 정의 (ViewModel에서 주입될 예정)
    var selectedTags by remember { mutableStateOf(setOf("Sweet", "Smoky")) }
    var sweetIntensity by remember { mutableStateOf(4f) }
    var sourIntensity by remember { mutableStateOf(1f) }
    var bitterIntensity by remember { mutableStateOf(1f) }
    var saltyIntensity by remember { mutableStateOf(3f) }
    var umamiIntensity by remember { mutableStateOf(2f) }
    var bodyIndex by remember { mutableIntStateOf(1) }
    var finishValue by remember { mutableStateOf(0.5f) }
    var notes by remember { mutableStateOf("") }


    Column(modifier = modifier) {

        // 섹션 타이틀
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = SharedR.drawable.ic_note_section_sensory),
                contentDescription = "Sensory Evaluation",
                tint = colors.primary,
                modifier = Modifier
                    .height(18.dp)
                    .padding(end = 6.dp)
            )
            Text(
                text = "Sensory Evaluation",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = colors.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---------- Flavor & Aroma Tags ----------
        LeafyFieldLabel(text = "Flavor & Aroma Tags")

        Spacer(modifier = Modifier.height(8.dp))

        val allTags = listOf("Floral", "Sweet", "Woody", "Nutty", "Smoky", "Herbal", "Fruity")

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            allTags.forEach { tag ->
                val isSelected = tag in selectedTags
                val backgroundColor =
                    if (isSelected) colors.primary else colors.background
                val borderColor =
                    if (isSelected) backgroundColor else colors.outlineVariant
                val textColor =
                    if (isSelected) colors.onPrimary else colors.onBackground

                Box(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(backgroundColor)
                        .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                        .clickable {
                            selectedTags = if (isSelected) {
                                selectedTags - tag
                            } else {
                                selectedTags + tag
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelMedium,
                        color = textColor
                    )
                }
            }
        }

        // ---------- Taste Intensity ----------
        Spacer(modifier = Modifier.height(20.dp))
        LeafyFieldLabel(text = "Taste Intensity")
        Spacer(modifier = Modifier.height(8.dp))

        TasteSliderRow(
            label = "Sweet",
            value = sweetIntensity,
            onValueChange = { sweetIntensity = it }
        )
        TasteSliderRow(
            label = "Sour",
            value = sourIntensity,
            onValueChange = { sourIntensity = it }
        )
        TasteSliderRow(
            label = "Bitter",
            value = bitterIntensity,
            onValueChange = { bitterIntensity = it }
        )
        TasteSliderRow(
            label = "Salty",
            value = saltyIntensity,
            onValueChange = { saltyIntensity = it }
        )
        TasteSliderRow(
            label = "Umami",
            value = umamiIntensity,
            onValueChange = { umamiIntensity = it }
        )

        // ---------- Body & Finish ----------
        Spacer(modifier = Modifier.height(20.dp))

        // Body (바디감)
        LeafyFieldLabel(text = "Body (바디감)")
        Spacer(modifier = Modifier.height(8.dp))

        BodySelectionSegmentedRow( // 분리된 컴포넌트 사용
            selectedIndex = bodyIndex,
            onSelect = { bodyIndex = it }
        )

        Spacer(modifier = Modifier.height(20.dp))


        // Finish (후미)
        FinishSliderRow( // 분리된 컴포넌트 사용
            level = finishValue,
            onLevelChange = { finishValue = it }
        )

        // ---------- Notes ----------
        Spacer(modifier = Modifier.height(20.dp))
        LeafyFieldLabel(text = "Notes")
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            singleLine = false,
            minLines = 3,
            maxLines = 6,
            placeholder = {
                Text(
                    text = "Add your tasting notes...",
                    color = colors.tertiary
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SensoryEvaluationSectionPreview() {
    LeafyTheme {
        SensoryEvaluationSection(
            modifier = Modifier.padding(16.dp)
        )
    }
}