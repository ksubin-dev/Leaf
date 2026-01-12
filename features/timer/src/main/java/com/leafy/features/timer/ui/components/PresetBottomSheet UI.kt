package com.leafy.features.timer.ui.components
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.leafy.shared.ui.component.DottedRoundedBox
//import com.leafy.shared.ui.theme.LeafyTheme
//import com.subin.leafy.domain.model.TimerPreset
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PresetBottomSheet(
//    presets: List<TimerPreset>,
//    selectedPresetId: String?,
//    onPresetClick: (TimerPreset) -> Unit,
//    onDismiss: () -> Unit
//) {
//    ModalBottomSheet(
//        onDismissRequest = onDismiss,
//        dragHandle = { BottomSheetDefaults.DragHandle() },
//        containerColor = MaterialTheme.colorScheme.surface,
//        tonalElevation = 0.dp
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 20.dp)
//                .padding(bottom = 40.dp)
//        ) {
//            // 헤더 영역
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Select Preset",
//                    style = MaterialTheme.typography.headlineSmall,
//                    fontWeight = FontWeight.Bold
//                )
//                IconButton(onClick = onDismiss) {
//                    Icon(Icons.Default.Close, contentDescription = "닫기")
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // 프리셋 리스트
//            LazyColumn(
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                items(presets) { preset ->
//                    PresetCard(
//                        preset = preset,
//                        isSelected = preset.id == selectedPresetId,
//                        onClick = { onPresetClick(preset) }
//                    )
//                }
//
//                item {
//                    DottedRoundedBox(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(56.dp)
//                            .clickable { /* 새 프리셋 추가 로직 */ },
//                        cornerRadiusDp = 12f,
//                        borderWidthDp = 1f,
//                        borderColor = MaterialTheme.colorScheme.outlineVariant
//                    ) {
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Icon(
//                                imageVector = Icons.Default.Add,
//                                contentDescription = null,
//                                modifier = Modifier.size(20.dp),
//                                tint = MaterialTheme.colorScheme.onSurfaceVariant
//                            )
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Text(
//                                text = "Add New Preset",
//                                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                                style = MaterialTheme.typography.bodyMedium
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun PresetCard(
//    preset: TimerPreset,
//    isSelected: Boolean,
//    onClick: () -> Unit
//) {
//
//    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
//    val borderWidth = if (isSelected) 2.dp else 1.dp
//
//    Surface(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(12.dp))
//            .clickable { onClick() }
//            .border(borderWidth, borderColor, RoundedCornerShape(12.dp)),
//        color = MaterialTheme.colorScheme.surface
//    ) {
//        Row(
//            modifier = Modifier.padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = preset.name,
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold
//                )
//
//                Text(
//                    text = buildString {
//                        append("${preset.temp} · ")
//                        val min = preset.baseTimeSeconds / 60
//                        val sec = preset.baseTimeSeconds % 60
//                        append("%d:%02d · ".format(min, sec))
//                        append("${preset.leafAmount} · ")
//                        append(preset.teaType)
//                    },
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
//
//            if (isSelected) {
//                Box(
//                    modifier = Modifier
//                        .size(10.dp)
//                        .background(MaterialTheme.colorScheme.primary, CircleShape)
//                )
//            }
//        }
//    }
//}
//
//// --- 🔍 프리뷰 추가 ---
//@Preview(showBackground = true)
//@Composable
//fun PresetBottomSheetPreview() {
//    val mockPresets = listOf(
//        TimerPreset(id = "1", name = "Daily Green Tea", temp = "80°C", baseTimeSeconds = 120, leafAmount = "2g", teaType = "Green"),
//        TimerPreset(id = "2", name = "Morning Oolong", temp = "95°C", baseTimeSeconds = 45, leafAmount = "5g", teaType = "Oolong"),
//        TimerPreset(id = "3", name = "Deep Pu-erh", temp = "100°C", baseTimeSeconds = 20, leafAmount = "7g", teaType = "Black")
//    )
//
//    LeafyTheme {
//        Surface(color = Color.White) {
//            Column(modifier = Modifier.padding(20.dp)) {
//                Text("Select Preset (Preview)", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
//                Spacer(modifier = Modifier.height(16.dp))
//                mockPresets.forEach { preset ->
//                    PresetCard(
//                        preset = preset,
//                        isSelected = preset.id == "1",
//                        onClick = {}
//                    )
//                    Spacer(modifier = Modifier.height(12.dp))
//                }
//            }
//        }
//    }
//}