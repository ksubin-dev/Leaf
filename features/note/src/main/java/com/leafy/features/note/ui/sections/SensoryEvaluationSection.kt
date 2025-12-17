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
import com.leafy.features.note.ui.components.*
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SensoryEvaluationSection(
    modifier: Modifier = Modifier,
    selectedTags: Set<String>,
    sweetIntensity: Float,
    sourIntensity: Float,
    bitterIntensity: Float,
    saltyIntensity: Float,
    umamiIntensity: Float,
    bodyIndex: Int,
    finishValue: Float,
    notes: String,
    onTagsChange: (Set<String>) -> Unit,
    onSweetnessChange: (Float) -> Unit,
    onSournessChange: (Float) -> Unit,
    onBitternessChange: (Float) -> Unit,
    onSaltyChange: (Float) -> Unit,
    onUmamiChange: (Float) -> Unit,
    onBodyIndexChange: (Int) -> Unit,
    onFinishValueChange: (Float) -> Unit,
    onNotesChange: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(modifier = modifier) {
        // 섹션 타이틀
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = SharedR.drawable.ic_note_section_sensory),
                contentDescription = "Sensory Evaluation",
                tint = colors.primary,
                modifier = Modifier.height(18.dp).padding(end = 6.dp)
            )
            Text(
                text = "Sensory Evaluation",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
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
                val backgroundColor = if (isSelected) colors.primary else colors.background
                val borderColor = if (isSelected) backgroundColor else colors.outlineVariant
                val textColor = if (isSelected) colors.onPrimary else colors.onBackground

                Box(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(backgroundColor)
                        .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                        .clickable {
                            val newTags = if (isSelected) selectedTags - tag else selectedTags + tag
                            onTagsChange(newTags)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = tag, style = MaterialTheme.typography.labelMedium, color = textColor)
                }
            }
        }

        // ---------- Taste Intensity ----------
        Spacer(modifier = Modifier.height(20.dp))
        LeafyFieldLabel(text = "Taste Intensity")
        Spacer(modifier = Modifier.height(8.dp))

        TasteSliderRow(label = "Sweet", value = sweetIntensity, onValueChange = onSweetnessChange)
        TasteSliderRow(label = "Sour", value = sourIntensity, onValueChange = onSournessChange)
        TasteSliderRow(label = "Bitter", value = bitterIntensity, onValueChange = onBitternessChange)
        TasteSliderRow(label = "Salty", value = saltyIntensity, onValueChange = onSaltyChange)
        TasteSliderRow(label = "Umami", value = umamiIntensity, onValueChange = onUmamiChange)

        // ---------- Body & Finish ----------
        Spacer(modifier = Modifier.height(20.dp))

        LeafyFieldLabel(text = "Body (바디감)")
        Spacer(modifier = Modifier.height(8.dp))

        BodySelectionSegmentedRow(selectedIndex = bodyIndex, onSelect = onBodyIndexChange)

        Spacer(modifier = Modifier.height(20.dp))

        FinishSliderRow(level = finishValue, onLevelChange = onFinishValueChange)

        // ---------- Notes ----------
        Spacer(modifier = Modifier.height(20.dp))
        LeafyFieldLabel(text = "Notes")
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            singleLine = false,
            minLines = 3,
            maxLines = 6,
            placeholder = { Text(text = "Add your tasting notes...", color = colors.tertiary) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SensoryEvaluationSectionPreview() {
    LeafyTheme {
        SensoryEvaluationSection(
            selectedTags = setOf("Sweet", "Floral"),
            sweetIntensity = 4f,
            sourIntensity = 1f,
            bitterIntensity = 2f,
            saltyIntensity = 0f,
            umamiIntensity = 3f,
            bodyIndex = 1,
            finishValue = 0.5f,
            notes = "Very smooth tea.",
            onTagsChange = {},
            onSweetnessChange = {},
            onSournessChange = {},
            onBitternessChange = {},
            onSaltyChange = {},
            onUmamiChange = {},
            onBodyIndexChange = {},
            onFinishValueChange = {},
            onNotesChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}