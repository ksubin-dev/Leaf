package com.leafy.features.note.screen


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.leafy.shared.ui.theme.LeafyTheme
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.draw.clip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuAnchorType.Companion.PrimaryNotEditable
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults





/**
 * New Brewing Note 입력 화면
 * - 상단 AppBar
 * - Photos 섹션 (Dry Leaf / Tea Liquor / Teaware / Additional)
 * - Basic Info 작업
 * - Tasting Context 작업 + 첫번째 아이콘 선택시 오른쪽 테두리 옅은문제 해결 필요
 * - 우림 조건 -> 타이머 버튼 연결?
 * - SensoryEvaluation -> 슬라이더 버튼 해결
 * - FinalRating
 */

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NoteScreen() {

    val colors = MaterialTheme.colorScheme

    // Basic Tea Information 상태 관리 (Hoisted States)
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
                                tint = colors.primary
                            )
                        }
                    },
                    actions = {
                        TextButton(onClick = { /* TODO: Save action (여기서 상태 변수들을 사용) */ }) {
                            Text(
                                text = "Save",
                                color = colors.primary,
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
                    .background(colors.background)
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

                // Tasting Context (Date & Time + Weather)
                TastingContextSection()


                Spacer(modifier = Modifier.height(24.dp))

                // 우림 조건
                BrewingConditionSection()

                Spacer(modifier = Modifier.height(24.dp))

                //SensoryEvaluation
                SensoryEvaluationSection()

                Spacer(modifier = Modifier.height(24.dp))


                FinalRatingSection()

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

    val colors = MaterialTheme.colorScheme

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = SharedR.drawable.ic_note_section_photos),
                contentDescription = "Photos",
                tint = colors.primary,
                modifier = Modifier
                    .height(18.dp)
                    .padding(end = 6.dp)
            )
            Text(
                text = "Photos",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = colors.onSurface
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
                color = colors.onSurface
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

    val colors = MaterialTheme.colorScheme

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
                tint = colors.primary,
                modifier = Modifier
                    .height(18.dp)
                    .padding(end = 6.dp)
            )
            Text(
                text = "Tasting Context",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = colors.onSurface
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
                        tint = colors.primary
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
                    color = colors.tertiary
                )
            }
        )

    }
}

// ---------------- 공용 및 하위 컴포넌트 ----------------

@Composable
private fun LeafyFieldLabel(text: String) {
    val colors = MaterialTheme.colorScheme

    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.Medium
        ),
        color = colors.tertiary
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

    val colors = MaterialTheme.colorScheme
    var expanded by remember { mutableStateOf(false) } // 드롭다운 상태

    Column(modifier = modifier) {
        LeafyFieldLabel(text = label)

        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }, // Box 클릭 시 상태 변경
            modifier = Modifier.fillMaxWidth()
        ) {
            // 💡 Modifier 중복 선언 제거
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(PrimaryNotEditable, true),
                value = selected,
                onValueChange = { /* readOnly */ },
                readOnly = true,
                singleLine = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    cursorColor = colors.primary,
                    focusedTextColor = colors.onSurface,
                    unfocusedTextColor = colors.onSurface,
                    focusedContainerColor = colors.surface,
                    unfocusedContainerColor = colors.surface
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
                            expanded = false // 선택 후 닫기
                        },
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

    val colors = MaterialTheme.colorScheme

    val isSelected = selectedWeather == type

    val borderColor = if (isSelected) colors.primary else colors.surfaceVariant

    Column(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .background(colors.background, RoundedCornerShape(12.dp))
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
            color = colors.onSurfaceVariant
        )
    }
}

@Composable
private fun BrewingConditionSection(
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    var waterTemp by remember { mutableStateOf("85") }      // 물 온도
    var leafAmount by remember { mutableStateOf("3") }      // 찻잎량 (g)
    var brewTime by remember { mutableStateOf("2분 30초") } // 우림 시간
    var brewCount by remember { mutableStateOf("1") }       // 우림 횟수
    var teawareType by remember { mutableStateOf("찻주전자") } // 다기 종류

    Column(modifier = modifier) {

        // 섹션 타이틀 (우림 조건)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = SharedR.drawable.ic_temp),
                contentDescription = "Brewing Conditions",
                tint = colors.primary,
                modifier = Modifier
                    .height(18.dp)
                    .padding(end = 6.dp)
            )
            Text(
                text = "Brewing Conditions",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = colors.inverseSurface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 물 온도 + 찻잎량
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                LeafyFieldLabel(text = "Water Temp (℃)")
                OutlinedTextField(
                    value = waterTemp,
                    onValueChange = { waterTemp = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    singleLine = true
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                LeafyFieldLabel(text = "Leaf Amount (g)")
                OutlinedTextField(
                    value = leafAmount,
                    onValueChange = { leafAmount = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 우림 시간
        LeafyFieldLabel(text = "Brewing Time")
        OutlinedTextField(
            value = brewTime,
            onValueChange = { brewTime = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            singleLine = true,
            trailingIcon = {
                Icon(
                    painter = painterResource(id = SharedR.drawable.ic_timer),
                    contentDescription = "Brew timer",
                    tint = colors.primary
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 우림 횟수 + 다기 종류
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                LeafyFieldLabel(text = "Infusion Count")
                OutlinedTextField(
                    value = brewCount,
                    onValueChange = { brewCount = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    singleLine = true
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                LeafyDropdownField(
                    label = "Teaware",
                    options = listOf("찻주전자", "개완", "머그", "다도 세트"),
                    selected = teawareType,
                    onSelectedChange = { teawareType = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SensoryEvaluationSection(
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    // Flavor tags 선택 상태 (여러 개 선택 가능)
    var selectedTags by remember {
        mutableStateOf(setOf("Sweet", "Smoky"))
    }

    // Taste intensity 슬라이더 값들 (0~5 정도로 가정)
    var sweetIntensity by remember { mutableStateOf(4f) }
    var sourIntensity by remember { mutableStateOf(1f) }
    var bitterIntensity by remember { mutableStateOf(1f) }
    var saltyIntensity by remember { mutableStateOf(3f) }
    var umamiIntensity by remember { mutableStateOf(2f) }

    // Body 드롭다운
    var bodySelected by remember { mutableStateOf("Light") }

    // Finish 슬라이더 (0 = Clean, 5 = Astringent)
    var finishValue by remember { mutableStateOf(1.5f) }

    // Notes
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
                color = colors.inverseSurface
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
                    when {
                        tag == "Smoky" && isSelected -> colors.secondary
                        isSelected -> colors.primary
                        else -> colors.background
                    }
                val borderColor =
                    if (isSelected) backgroundColor else colors.tertiaryContainer
                val textColor =
                    if (isSelected) colors.background else colors.inverseSurface

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

        // ---------- Body ----------
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                LeafyFieldLabel(text = "Body")
                LeafyDropdownField(
                    label = "",
                    options = listOf("Light", "Medium", "Full"),
                    selected = bodySelected,
                    onSelectedChange = { bodySelected = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(modifier = Modifier.weight(1.5f)) {
                LeafyFieldLabel(text = "Finish")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Clean",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant
                    )
                    Slider(
                        value = finishValue,
                        onValueChange = { finishValue = it },
                        valueRange = 0f..5f,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = colors.primary,
                            activeTrackColor = colors.primary
                        )
                    )
                    Text(
                        text = "Astringent",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant
                    )
                }
            }
        }

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
                    color = colors.onSurfaceVariant
                )
            }
        )
    }
}
@Composable
private fun TasteSliderRow(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(56.dp),
            color = colors.inverseSurface
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..5f,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = colors.primary,
                activeTrackColor = colors.primary
            )
        )
        Text(
            text = value.toInt().toString(),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(start = 8.dp),
            color = colors.onSurfaceVariant
        )
    }
}

@Composable
private fun FinalRatingSection(
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    var rating by remember { mutableStateOf(0) }
    var purchaseAgain by remember { mutableStateOf<Boolean?>(null) } // Yes/No 선택 상태

    Column(modifier = modifier) {

        // 섹션 타이틀
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = SharedR.drawable.ic_note_section_final_rating),
                contentDescription = "Final Rating",
                tint = colors.error,
                modifier = Modifier
                    .height(18.dp)
                    .padding(end = 6.dp)
            )
            Text(
                text = "Final Rating",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = colors.inverseSurface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Overall Rating
        LeafyFieldLabel(text = "Overall Rating")
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            (1..5).forEach { starIndex ->
                val isFilled = starIndex <= rating
                IconButton(
                    onClick = { rating = starIndex }
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isFilled) {
                                SharedR.drawable.ic_star_filled
                            } else {
                                SharedR.drawable.ic_star_outline
                            }
                        ),
                        contentDescription = "$starIndex stars",
                        tint = Color.Unspecified,
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Would you purchase this tea again?
        Text(
            text = "Would you purchase this tea again?",
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Yes 버튼
            val yesSelected = purchaseAgain == true
            Button(
                onClick = { purchaseAgain = true },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (yesSelected) colors.primary else colors.background,
                    contentColor = if (yesSelected) colors.background else colors.primary
                ),
                border = if (yesSelected) null else BorderStroke(1.dp, colors.primary)
            ) {
                Text(
                    text = "Yes",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // No 버튼
            val noSelected = purchaseAgain == false
            OutlinedButton(
                onClick = { purchaseAgain = false },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (noSelected) colors.tertiaryContainer else colors.background,
                    contentColor = colors.onSurfaceVariant
                ),
                border = BorderStroke(
                    1.dp,
                    if (noSelected) colors.tertiaryContainer else colors.surfaceVariant
                )
            ) {
                Text(
                    text = "No",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}