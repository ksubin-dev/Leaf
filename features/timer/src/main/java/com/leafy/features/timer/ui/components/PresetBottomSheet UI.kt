package com.leafy.features.timer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.subin.leafy.domain.model.TimerPreset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetBottomSheet(
    presets: List<TimerPreset>,
    onPresetClick: (TimerPreset) -> Unit,
    onDismiss: () -> Unit
) {
    // 1. 바텀시트의 상태를 관리합니다.
    val sheetState = rememberModalBottomSheetState()

    // 2. 바텀시트 본체
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() } // 상단 핸들 표시
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp) // 하단 여백
        ) {
            Text(
                text = "우림 프리셋 선택",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // 3. 프리셋 리스트
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(presets) { preset ->
                    PresetItem(
                        preset = preset,
                        onClick = { onPresetClick(preset) }
                    )
                }
            }
        }
    }
}

@Composable
fun PresetItem(
    preset: TimerPreset,
    onClick: () -> Unit
) {
    // 리스트의 각 항목 디자인
    ListItem(
        headlineContent = {
            Text(preset.name, fontWeight = FontWeight.SemiBold)
        },
        supportingContent = {
            Text("${preset.teaType} | ${preset.temp} | ${preset.baseTimeSeconds}초")
        },
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
    )
}