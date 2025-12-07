package com.leafy.features.note.ui.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.components.* // 분리된 필드 컴포넌트들을 Import
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun BasicTeaInformationSection(
    modifier: Modifier = Modifier,
    teaName: String,
    onTeaNameChange: (String) -> Unit,
    brandName: String,
    onBrandNameChange: (String) -> Unit,
    teaType: String,
    onTeaTypeChange: (String) -> Unit,
    leafStyle: String,
    onLeafStyleChange: (String) -> Unit,
    leafProcessing: String,
    onLeafProcessingChange: (String) -> Unit,
    teaGrade: String,
    onTeaGradeChange: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(modifier = modifier) {
        // 섹션 타이틀
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = SharedR.drawable.ic_leaf),
                contentDescription = "Basic Tea Information",
                tint = colors.primary,
                modifier = Modifier
                    .height(18.dp)
                    .padding(end = 6.dp)
            )
            Text(
                text = "Basic Tea Information",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = colors.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tea Name 필드
        TeaNameField(
            teaName = teaName,
            onValueChange = onTeaNameChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tea Type + Leaf Style (2열)
        TeaTypeAndStyleRow(
            teaType = teaType,
            onTeaTypeChange = onTeaTypeChange,
            leafStyle = leafStyle,
            onLeafStyleChange = onLeafStyleChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Brand / Manufacturer 필드
        BrandManufacturerField(
            brandName = brandName,
            onValueChange = onBrandNameChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Leaf Processing + Tea Grade (2열)
        LeafProcessingAndGradeRow(
            leafProcessing = leafProcessing,
            onLeafProcessingChange = onLeafProcessingChange,
            teaGrade = teaGrade,
            onTeaGradeChange = onTeaGradeChange
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BasicTeaInformationSectionPreview() {
    LeafyTheme {
        // Preview용 가짜 상태 전달
        BasicTeaInformationSection(
            teaName = "Dragon Well",
            onTeaNameChange = {},
            brandName = "Leafy Tea Co.",
            onBrandNameChange = {},
            teaType = "Green",
            onTeaTypeChange = {},
            leafStyle = "Loose Leaf",
            onLeafStyleChange = {},
            leafProcessing = "Whole Leaf",
            onLeafProcessingChange = {},
            teaGrade = "Premium",
            onTeaGradeChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}