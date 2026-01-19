package com.leafy.features.timer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LeafyDropdownField
import com.leafy.shared.ui.component.LeafyTextField
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.BrewingRecipe
import com.subin.leafy.domain.model.TeaType
import com.subin.leafy.domain.model.TeawareType
import com.subin.leafy.domain.model.TimerPreset
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PresetEditDialog(
    initialPreset: TimerPreset? = null,
    onDismissRequest: () -> Unit,
    onSaveClick: (TimerPreset) -> Unit,
    onDeleteClick: ((String) -> Unit)? = null
) {

    var name by remember { mutableStateOf(initialPreset?.name ?: "") }
    var selectedTeaType by remember {
        mutableStateOf(
            if (initialPreset?.teaType == TeaType.UNKNOWN) TeaType.GREEN
            else (initialPreset?.teaType ?: TeaType.GREEN)
        )
    }
    var tempStr by remember { mutableStateOf(initialPreset?.recipe?.waterTemp?.toString() ?: "95") }
    var timeStr by remember { mutableStateOf(initialPreset?.recipe?.brewTimeSeconds?.toString() ?: "180") }
    var waterStr by remember { mutableStateOf(initialPreset?.recipe?.waterAmount?.toString() ?: "150") }
    var leafStr by remember { mutableStateOf(initialPreset?.recipe?.leafAmount?.toString() ?: "") }

    var selectedTeaware by remember {
        mutableStateOf(
            initialPreset?.recipe?.teaware ?: TeawareType.MUG
        )
    }

    val isEditMode = initialPreset != null

    val displayTeaTypes = remember {
        TeaType.entries.filter { it != TeaType.UNKNOWN }
    }

    val displayTeawares = remember {
        TeawareType.entries.filter { it != TeawareType.ETC }
    }

    var isTeawareExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 700.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditMode) "레시피 수정" else "새 레시피 만들기",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    if (isEditMode && onDeleteClick != null) {
                        TextButton(
                            onClick = singleClick {
                                onDeleteClick(initialPreset.id)
                            }
                        ) {
                            Text("Delete", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                LeafyTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "레시피 이름",
                    placeholder = "예: 오후의 얼그레이",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))


                LeafyDropdownField(
                    selectedOption = selectedTeaType,
                    options = displayTeaTypes,
                    onOptionSelected = { selectedTeaType = it },
                    labelMapper = { it.label },
                    label = "차 종류",
                    placeholder = "차 종류를 선택해주세요",
                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LeafyTextField(
                        value = tempStr,
                        onValueChange = { if(it.all { c -> c.isDigit() }) tempStr = it },
                        label = "온도 (°C)",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    LeafyTextField(
                        value = timeStr,
                        onValueChange = { if(it.all { c -> c.isDigit() }) timeStr = it },
                        label = "시간 (초)",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LeafyTextField(
                        value = waterStr,
                        onValueChange = { if(it.all { c -> c.isDigit() }) waterStr = it },
                        label = "물 양 (ml)",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    LeafyTextField(
                        value = leafStr,
                        onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null || it == ".") leafStr = it },
                        label = "잎 양 (g)",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LeafyDropdownField(
                    selectedOption = selectedTeaware,
                    options = displayTeawares,
                    onOptionSelected = { selectedTeaware = it },
                    labelMapper = { it.label },
                    label = "사용 다구",
                    placeholder = "다구를 선택해주세요",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = singleClick {
                        val newPreset = TimerPreset(
                            id = initialPreset?.id ?: UUID.randomUUID().toString(),
                            name = name.ifBlank { "나만의 레시피" },
                            teaType = selectedTeaType,
                            isDefault = false,
                            recipe = BrewingRecipe(
                                waterTemp = tempStr.toIntOrNull() ?: 90,
                                brewTimeSeconds = timeStr.toIntOrNull() ?: 180,
                                leafAmount = leafStr.toFloatOrNull() ?: 3f,
                                waterAmount = waterStr.toIntOrNull() ?: 150,
                                infusionCount = 1,
                                teaware = selectedTeaware
                            )
                        )
                        onSaveClick(newPreset)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isEditMode) "수정 완료" else "레시피 저장", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PresetEditDialogPreview() {
    LeafyTheme {
        PresetEditDialog(
            initialPreset = null,
            onDismissRequest = {},
            onSaveClick = {},
            onDeleteClick = {}
        )
    }
}