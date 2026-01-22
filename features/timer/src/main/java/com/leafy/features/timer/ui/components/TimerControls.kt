package com.leafy.features.timer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leafy.shared.common.singleClick

@Composable
fun TimerControls(
    modifier: Modifier = Modifier,
    isRunning: Boolean,
    isResetAvailable: Boolean = true,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        val resetAlpha = if (!isRunning && isResetAvailable) 1f else 0.3f
        IconButton(
            onClick = singleClick {
                if (!isRunning && isResetAvailable) {
                    onResetTimer()
                }
            },
            enabled = !isRunning && isResetAvailable,
            modifier = Modifier
                .size(56.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = resetAlpha),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reset",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = resetAlpha)
            )
        }

        Spacer(modifier = Modifier.width(32.dp))

        IconButton(
            onClick = singleClick { onToggleTimer() },
            modifier = Modifier
                .size(88.dp)
                .background(
                    if (isRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    CircleShape
                )
        ) {
            Icon(
                imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Toggle",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.width(32.dp))

        Box(modifier = Modifier.size(56.dp))
    }
}