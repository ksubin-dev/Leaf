package com.leafy.features.note.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.common.NotePhotoSlot
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyGreen
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.ui.theme.LeafyWhite
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.draw.clip

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * New Brewing Note 입력 화면
 * - 상단 AppBar
 * - Photos 섹션 (Dry Leaf / Tea Liquor / Teaware / Additional)
 * - Basic Info 작업
 * - Tasting Context 작업 + 첫번째 아이콘 선택시 오른쪽 테두리 옅은문제 해결 필요
 * - 나머지 섹션(Sensory...)는 이후 단계에서 추가 예정
 */

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NoteScreen() {
    // 🌟 1. Basic Tea Information 상태 관리 (Hoisted States)
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

                Spacer(modifier = Modifier.height(24.dp))

                // 3) Tasting Context (Date & Time + Weather)
                TastingContextSection()

                // 이후 Sensory Evaluation, Final Rating 추가 예정
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

/* ----------------- Basic Tea Information 섹션 ----------------- */
@OptIn(ExperimentalMaterial3Api::class)
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
/* ----------------- Tasting Context 섹션 (날씨 포함) ----------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TastingContextSection(
    modifier: Modifier = Modifier
) {
    var dateTime by remember { mutableStateOf("") }
    var selectedWeather by remember { mutableStateOf(WeatherType.CLEAR) }

    // ⬇️ DatePicker 상태
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    Column(modifier = modifier) {

        // 섹션 타이틀
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = SharedR.drawable.ic_note_section_context),
                contentDescription = "Tasting Context",
                tint = LeafyGreen,
                modifier = Modifier
                    .height(18.dp)
                    .padding(end = 6.dp)
            )
            Text(
                text = "Tasting Context",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFF303437)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Date & Time
        LeafyFieldLabel(text = "Date & Time")
        OutlinedTextField(
            value = dateTime,
            onValueChange = { /* readOnly */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .clickable { showDatePicker = true },   // ⬅️ 필드 눌러도 열리게
            readOnly = true,
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {  // ⬅️ 아이콘 눌러도 열림
                    Icon(
                        painter = painterResource(id = SharedR.drawable.ic_calendar),
                        contentDescription = "Pick date",
                        tint = LeafyGreen
                    )
                }
            }
        )

        // ⬇️ 작은 캘린더 다이얼로그
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                            val millis = datePickerState.selectedDateMillis
                            if (millis != null) {
                                val date = Date(millis)
                                val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                                dateTime = formatter.format(date)
                            }
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weather
        LeafyFieldLabel(text = "Weather")
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WeatherOptionButton(
                type = WeatherType.CLEAR,
                label = "Clear",
                iconRes = SharedR.drawable.ic_weather_clear,
                selectedWeather = selectedWeather,
                onSelected = { selectedWeather = it },
                modifier = Modifier.weight(1f)
            )
            WeatherOptionButton(
                type = WeatherType.CLOUDY,
                label = "Cloudy",
                iconRes = SharedR.drawable.ic_weather_cloudy,
                selectedWeather = selectedWeather,
                onSelected = { selectedWeather = it },
                modifier = Modifier.weight(1f)
            )
            WeatherOptionButton(
                type = WeatherType.RAINY,
                label = "Rainy",
                iconRes = SharedR.drawable.ic_weather_rainy,
                selectedWeather = selectedWeather,
                onSelected = { selectedWeather = it },
                modifier = Modifier.weight(1f)
            )
            WeatherOptionButton(
                type = WeatherType.SNOWY,
                label = "Snowy",
                iconRes = SharedR.drawable.ic_weather_snowy,
                selectedWeather = selectedWeather,
                onSelected = { selectedWeather = it },
                modifier = Modifier.weight(1f)
            )
            WeatherOptionButton(
                type = WeatherType.INDOOR,
                label = "Indoor",
                iconRes = SharedR.drawable.ic_weather_indoor,
                selectedWeather = selectedWeather,
                onSelected = { selectedWeather = it },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LeafyFieldLabel(text = "With")

        var withPeople by remember { mutableStateOf("") }

        OutlinedTextField(
            value = withPeople,
            onValueChange = { withPeople = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            singleLine = true,
            placeholder = {
                Text(
                    text = "e.g. Minjae, Subin",
                    color = Color(0xFFB8BCC2)
                )
            }
        )

    }
}

// ---------------- 공용 및 하위 컴포넌트 ----------------

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

// ---------------- LeafyDropdownField ----------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeafyDropdownField(
    modifier: Modifier = Modifier,
    label: String,
    options: List<String>,
    selected: String,
    onSelectedChange: (String) -> Unit
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
            //드롭다운 강사님 환율 예제코드의 드롭다운 활용해서 수정!!!!!!!!!!!!!!!
            OutlinedTextField(
                value = selected,
                onValueChange = { /* readOnly이므로 비워둠 */ },
                // 🟢 기능 작동을 위해 .menuAnchor()를 유지합니다.
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

// ---------------- 기타 필드 컴포넌트 ----------------

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
/* ---------------- Weather 버튼 ---------------- */

private enum class WeatherType {
    CLEAR, CLOUDY, RAINY, SNOWY, INDOOR
}

@Composable
private fun WeatherOptionButton(
    modifier: Modifier = Modifier,
    type: WeatherType,
    label: String,
    iconRes: Int,
    selectedWeather: WeatherType,
    onSelected: (WeatherType) -> Unit
) {
    val isSelected = selectedWeather == type

    val borderColor = if (isSelected) LeafyGreen else Color(0xFFE1E4EA)

    Column(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .background(LeafyWhite, RoundedCornerShape(12.dp))
            .clickable { onSelected(type) }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = Color.Unspecified,
            modifier = Modifier.height(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF7A7F86)
        )
    }
}