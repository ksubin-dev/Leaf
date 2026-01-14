package com.leafy.features.timer.ui.components
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Settings
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import com.subin.leafy.domain.model.TimerPreset
//
//@Composable
//fun CurrentPresetCard(
//    modifier: Modifier = Modifier,
//    preset: TimerPreset,
//    onClickSettings: () -> Unit
//) {
//    Card(
//        modifier = modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surfaceVariant
//        ),
//        shape = RoundedCornerShape(24.dp)
//    ) {
//        Row(
//            modifier = Modifier.padding(20.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = "Current Preset",
//                    style = MaterialTheme.typography.labelSmall,
//                    color = MaterialTheme.colorScheme.primary,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    text = preset.name,
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = "${preset.temp} · ${preset.leafAmount}",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
//            IconButton(
//                onClick = onClickSettings,
//                modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Settings,
//                    contentDescription = "Settings",
//                    tint = MaterialTheme.colorScheme.primary
//                )
//            }
//        }
//    }
//}