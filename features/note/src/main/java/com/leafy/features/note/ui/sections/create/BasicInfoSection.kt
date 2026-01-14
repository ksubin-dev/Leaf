package com.leafy.features.note.ui.sections.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.common.NoteDropdownField // 새로 만든 드롭다운 사용
import com.leafy.features.note.ui.common.NoteInputTextField // 새로 만든 텍스트 필드 사용
import com.leafy.features.note.ui.common.NoteSectionHeader
import com.leafy.shared.R
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.TeaType

@Composable
fun BasicInfoSection(
    teaName: String,
    onTeaNameChange: (String) -> Unit,
    teaBrand: String,
    onTeaBrandChange: (String) -> Unit,
    teaType: TeaType,
    onTeaTypeChange: (TeaType) -> Unit,
    teaOrigin: String,
    onTeaOriginChange: (String) -> Unit,
    teaLeafStyle: String,
    onTeaLeafStyleChange: (String) -> Unit,
    teaGrade: String,
    onTeaGradeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 드롭다운용 옵션 리스트
    val leafStyleOptions = listOf("잎차 (Loose Leaf)", "티백 (Tea Bag)", "가루 (Powder)", "긴압차 (Compressed/Cake)", "기타")
    val gradeOptions = listOf("선택 안 함", "OP (Orange Pekoe)", "BOP (Broken)", "FOP (Flowery)", "CTC", "특우 (Special)", "상급 (Premium)", "보통 (Standard)")

    Column(modifier = modifier.fillMaxWidth()) {
        NoteSectionHeader(
            icon = painterResource(id = R.drawable.ic_leaf),
            title = "기본 정보"
        )

        // 1. 차 이름
        NoteInputTextField(
            value = teaName,
            onValueChange = onTeaNameChange,
            label = "차 이름",
            placeholder = "예: 세작, 얼그레이",
            modifier = Modifier.fillMaxWidth()
        )

        // 2. 브랜드 / 다원
        NoteInputTextField(
            value = teaBrand,
            onValueChange = onTeaBrandChange,
            label = "브랜드 / 다원",
            placeholder = "예: 오설록, 트와이닝",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        // 3. Row: 차 종류 | 등급 (둘 다 짧은 항목끼리 묶음)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 차 종류
            NoteDropdownField(
                label = "차 종류",
                options = TeaType.entries,
                selectedOption = teaType,
                onOptionSelected = onTeaTypeChange,
                labelMapper = { it.label },
                modifier = Modifier.weight(1f)
            )

            // 등급 (짧은 텍스트라 종류 옆에 두기 적절)
            NoteDropdownField(
                label = "등급",
                options = gradeOptions,
                selectedOption = teaGrade.ifEmpty { gradeOptions[0] },
                onOptionSelected = onTeaGradeChange,
                labelMapper = { it },
                modifier = Modifier.weight(1f)
            )
        }

        // 4. 산지 (Full Width로 변경하여 입력 편의성 증대)
        NoteInputTextField(
            value = teaOrigin,
            onValueChange = onTeaOriginChange,
            label = "산지",
            placeholder = "예: 보성, 중국 운남성",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        // 5. 형태 (Full Width로 변경하여 텍스트 잘림 방지)
        NoteDropdownField(
            label = "형태",
            options = leafStyleOptions,
            selectedOption = teaLeafStyle.ifEmpty { leafStyleOptions[0] },
            onOptionSelected = onTeaLeafStyleChange,
            labelMapper = { it }, // "잎차 (Loose Leaf)" 처럼 긴 텍스트도 이제 잘 보임
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun BasicInfoSectionPreview() {
    LeafyTheme {
        // 프리뷰에서 입력 및 선택이 가능하도록 상태(State) 정의
        var name by remember { mutableStateOf("") }
        var brand by remember { mutableStateOf("") }
        var type by remember { mutableStateOf(TeaType.GREEN) }
        var origin by remember { mutableStateOf("") }
        var style by remember { mutableStateOf("잎차 (Loose Leaf)") }
        var grade by remember { mutableStateOf("선택 안 함") }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            BasicInfoSection(
                teaName = name,
                onTeaNameChange = { name = it },
                teaBrand = brand,
                onTeaBrandChange = { brand = it },
                teaType = type,
                onTeaTypeChange = { type = it },
                teaOrigin = origin,
                onTeaOriginChange = { origin = it },
                teaLeafStyle = style,
                onTeaLeafStyleChange = { style = it },
                teaGrade = grade,
                onTeaGradeChange = { grade = it }
            )
        }
    }
}