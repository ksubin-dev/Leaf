package com.leafy.features.note.ui.sections.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.common.LeafySegmentedButton
import com.leafy.features.note.ui.common.LeafySlider
import com.leafy.features.note.ui.common.NoteInputTextField
import com.leafy.features.note.ui.common.NoteSectionHeader
import com.leafy.shared.R
import com.leafy.shared.ui.component.LeafyChip
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.BodyType
import com.subin.leafy.domain.model.FlavorTag

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SensoryEvalSection(
    flavorTags: List<FlavorTag>,
    onFlavorTagToggle: (FlavorTag) -> Unit,
    sweetness: Float,
    onSweetnessChange: (Float) -> Unit,
    sourness: Float,
    onSournessChange: (Float) -> Unit,
    bitterness: Float,
    onBitternessChange: (Float) -> Unit,
    astringency: Float,
    onAstringencyChange: (Float) -> Unit,
    umami: Float,
    onUmamiChange: (Float) -> Unit,
    body: BodyType,
    onBodyChange: (BodyType) -> Unit,
    finish: Float,
    onFinishChange: (Float) -> Unit,
    memo: String,
    onMemoChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxWidth()) {
        NoteSectionHeader(
            icon = painterResource(id = R.drawable.ic_note_section_sensory),
            title = "감각 평가"
        )

        Text(
            text = "향 (Flavor & Aroma)",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.secondary,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            FlavorTag.entries.forEach { tag ->
                LeafyChip(
                    text = tag.label,
                    isSelected = flavorTags.contains(tag),
                    onClick = { onFlavorTagToggle(tag) }
                )
            }
        }

        Text(
            text = "맛의 강도 (0 ~ 5)",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.secondary,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
        )

        LeafySlider(label = "단맛", value = sweetness, onValueChange = onSweetnessChange)
        LeafySlider(label = "신맛", value = sourness, onValueChange = onSournessChange)
        LeafySlider(label = "쓴맛", value = bitterness, onValueChange = onBitternessChange)
        LeafySlider(label = "떫은맛", value = astringency, onValueChange = onAstringencyChange)
        LeafySlider(label = "감칠맛", value = umami, onValueChange = onUmamiChange)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "바디감 (Body)",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.secondary,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )

        LeafySegmentedButton(
            options = BodyType.entries,
            selectedOption = body,
            onOptionSelected = onBodyChange,
            labelMapper = {
                when(it) {
                    BodyType.LIGHT -> "가벼움"
                    BodyType.MEDIUM -> "보통"
                    BodyType.FULL -> "묵직함"
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "여운 (Finish)",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.secondary,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
        )
        LeafySlider(label = "지속성", value = finish, onValueChange = onFinishChange)

        Spacer(modifier = Modifier.height(24.dp))

        NoteInputTextField(
            value = memo,
            onValueChange = onMemoChange,
            label = "시음 노트",
            placeholder = "맛과 향, 느낌을 자유롭게 기록하세요...",
            singleLine = false,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 250.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun SensoryEvalSectionPreview() {
    LeafyTheme {
        var selectedTags by remember { mutableStateOf(listOf(FlavorTag.FLORAL, FlavorTag.FRUITY)) }
        var sweetness by remember { mutableFloatStateOf(3f) }
        var sourness by remember { mutableFloatStateOf(1f) }
        var bitterness by remember { mutableFloatStateOf(2f) }
        var astringency by remember { mutableFloatStateOf(0f) }
        var umami by remember { mutableFloatStateOf(4f) }
        var body by remember { mutableStateOf(BodyType.MEDIUM) }
        var finish by remember { mutableFloatStateOf(2.5f) }
        var memo by remember { mutableStateOf("맛이 아주 훌륭하고 향긋합니다.") }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SensoryEvalSection(
                flavorTags = selectedTags,
                onFlavorTagToggle = { tag ->
                    // 태그 토글 로직 (선택/해제)
                    selectedTags = if (selectedTags.contains(tag)) {
                        selectedTags - tag
                    } else {
                        selectedTags + tag
                    }
                },
                sweetness = sweetness,
                onSweetnessChange = { sweetness = it },
                sourness = sourness,
                onSournessChange = { sourness = it },
                bitterness = bitterness,
                onBitternessChange = { bitterness = it },
                astringency = astringency,
                onAstringencyChange = { astringency = it },
                umami = umami,
                onUmamiChange = { umami = it },
                body = body,
                onBodyChange = { body = it },
                finish = finish,
                onFinishChange = { finish = it },
                memo = memo,
                onMemoChange = { memo = it }
            )
        }
    }
}