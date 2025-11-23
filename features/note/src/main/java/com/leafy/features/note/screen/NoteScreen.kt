package com.leafy.features.note.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.NotePhotoSlot
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyGreen
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.ui.theme.LeafyWhite

/**
 * New Brewing Note 입력 화면
 * - 상단 AppBar
 * - Photos 섹션 (Dry Leaf / Tea Liquor / Teaware / Additional)
 * - Basic Info 작업
 * - 나머지 섹션(Tasting Context, Sensory...)는 이후 단계에서 추가 예정
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NoteScreen() {
    var teaName by remember { mutableStateOf("Enter Tea Name") }
    var brandName by remember { mutableStateOf("Enter brand name") }
    var teaType by remember { mutableStateOf("Black") }
    var leafStyle by remember { mutableStateOf("Loose Leaf") }
    var leafProcessing by remember { mutableStateOf("Whole Leaf") }
    var teaGrade by remember { mutableStateOf("OP") }

    LeafyTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "New Brewing Note",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { /* TODO: navController.navigateUp() */ }) {
                            Icon(
                                painter = painterResource(id = SharedR.drawable.ic_back),
                                contentDescription = "Back",
                                modifier = Modifier
                                    .height(20.dp),
                                tint = LeafyGreen
                            )
                        }
                    },
                    actions = {
                        TextButton(onClick = { /* TODO: Save action (여기서 상태 변수들을 사용) */ }) {
                            Text(
                                text = "Save",
                                color = LeafyGreen,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LeafyWhite)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                // ---------------- 1. Photos 섹션 ----------------
                PhotosSection(
                    onClickDryLeaf = { /* TODO: 사진 선택/촬영 */ },
                    onClickTeaLiquor = { /* TODO */ },
                    onClickTeaware = { /* TODO */ },
                    onClickAdditional = { /* TODO */ }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ---------------- 2. Basic Tea Information 섹션 ----------------
                BasicTeaInformationSection(
                    teaName = teaName,
                    onTeaNameChange = { teaName = it },
                    brandName = brandName,
                    onBrandNameChange = { brandName = it },
                    teaType = teaType,
                    onTeaTypeChange = { teaType = it },
                    leafStyle = leafStyle,
                    onLeafStyleChange = { leafStyle = it },
                    leafProcessing = leafProcessing,
                    onLeafProcessingChange = { leafProcessing = it },
                    teaGrade = teaGrade,
                    onTeaGradeChange = { teaGrade = it }
                )

                // 이 아래에 나중에 Tasting Context, Sensory Evaluation, Final Rating 순서대로 추가
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ====================================================================
// ========================= 하위 컴포넌트 정의 =========================
// ====================================================================


// 1. Photos 섹션
@Composable
private fun PhotosSection(
    onClickDryLeaf: () -> Unit,
    onClickTeaLiquor: () -> Unit,
    onClickTeaware: () -> Unit,
    onClickAdditional: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = SharedR.drawable.ic_note_section_photos),
                contentDescription = "Photos",
                tint = LeafyGreen,
                modifier = Modifier
                    .height(18.dp)
                    .padding(end = 6.dp)
            )
            Text(
                text = "Photos",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFF303437)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NotePhotoSlot(
                    title = "Dry Leaf",
                    iconRes = SharedR.drawable.ic_leaf,
                    onClick = onClickDryLeaf,
                    modifier = Modifier.weight(1f)
                )
                NotePhotoSlot(
                    title = "Tea Liquor",
                    iconRes = SharedR.drawable.ic_note_photo_tea_liquor,
                    onClick = onClickTeaLiquor,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NotePhotoSlot(
                    title = "Teaware",
                    iconRes = SharedR.drawable.ic_note_photo_teaware,
                    onClick = onClickTeaware,
                    modifier = Modifier.weight(1f)
                )
                NotePhotoSlot(
                    title = "Additional",
                    iconRes = SharedR.drawable.ic_note_photo_add,
                    onClick = onClickAdditional,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// 2. Basic Tea Information 섹션 (Hoisting 적용)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicTeaInformationSection(
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
    onTeaGradeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 섹션 타이틀
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = SharedR.drawable.ic_leaf),
                contentDescription = "Basic Tea Information",
                tint = LeafyGreen,
                modifier = Modifier
                    .height(18.dp)
                    .padding(end = 6.dp)
            )
            Text(
                text = "Basic Tea Information",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFF303437)
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

// ---------------- 분리된 하위 컴포넌트 ----------------

@Composable
private fun LeafyFieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.Medium
        ),
        color = Color(0xFF7A7F86)
    )
}

@Composable
private fun TeaNameField(
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
        singleLine = true
    )
}

@Composable
private fun BrandManufacturerField(
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

@Composable
private fun TeaTypeAndStyleRow(
    teaType: String,
    onTeaTypeChange: (String) -> Unit,
    leafStyle: String,
    onLeafStyleChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
private fun LeafProcessingAndGradeRow(
    leafProcessing: String,
    onLeafProcessingChange: (String) -> Unit,
    teaGrade: String,
    onTeaGradeChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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


// ---------------- LeafyDropdownField  ----------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeafyDropdownField(
    label: String,
    options: List<String>,
    selected: String,
    onSelectedChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        LeafyFieldLabel(text = label)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            OutlinedTextField(
                value = selected,
                onValueChange = { /* readOnly이므로 비워둠 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                singleLine = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedBorderColor = LeafyGreen
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelectedChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

