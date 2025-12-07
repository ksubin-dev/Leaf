package com.leafy.features.note.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.theme.LeafyTheme

// ---------------- 1. 단일 텍스트 필드 ----------------

@Composable
fun TeaNameField(
    teaName: String,
    onValueChange: (String) -> Unit
) {
    LeafyFieldLabel(text = "Tea Name")
    OutlinedTextField(
        value = teaName,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        singleLine = true,
    )
}

@Composable
fun BrandManufacturerField(
    brandName: String,
    onValueChange: (String) -> Unit
) {
    LeafyFieldLabel(text = "Brand / Manufacturer")
    OutlinedTextField(
        value = brandName,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        singleLine = true
    )
}

// ---------------- 2. 2열 드롭다운 행 ----------------

@Composable
fun TeaTypeAndStyleRow(
    teaType: String,
    onTeaTypeChange: (String) -> Unit,
    leafStyle: String,
    onLeafStyleChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 분리된 LeafyDropdownField 컴포넌트 사용
        LeafyDropdownField(
            label = "Tea Type",
            options = listOf("Black", "Green", "Oolong", "White", "Herbal"),
            selected = teaType,
            onSelectedChange = onTeaTypeChange,
            modifier = Modifier.weight(1f)
        )

        LeafyDropdownField(
            label = "Leaf Style",
            options = listOf("Loose Leaf", "Tea Bag", "Compressed"),
            selected = leafStyle,
            onSelectedChange = onLeafStyleChange,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun LeafProcessingAndGradeRow(
    leafProcessing: String,
    onLeafProcessingChange: (String) -> Unit,
    teaGrade: String,
    onTeaGradeChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 분리된 LeafyDropdownField 컴포넌트 사용
        LeafyDropdownField(
            label = "Leaf Processing",
            options = listOf("Whole Leaf", "Broken Leaf", "CTC"),
            selected = leafProcessing,
            onSelectedChange = onLeafProcessingChange,
            modifier = Modifier.weight(1f)
        )

        LeafyDropdownField(
            label = "Tea Grade",
            options = listOf("OP", "FOP", "TGFOP", "Dust"),
            selected = teaGrade,
            onSelectedChange = onTeaGradeChange,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TeaInfoFieldsPreview() {
    LeafyTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            TeaNameField(teaName = "홍차 이름", onValueChange = {})
            TeaTypeAndStyleRow(
                teaType = "Black",
                onTeaTypeChange = {},
                leafStyle = "Loose Leaf",
                onLeafStyleChange = {}
            )
            BrandManufacturerField(brandName = "제조사", onValueChange = {})
        }
    }
}