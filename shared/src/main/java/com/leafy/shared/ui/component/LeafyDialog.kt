package com.leafy.shared.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun LeafyDialog(
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    confirmText: String = "확인",
    dismissText: String = "취소",
    containerColor: Color? = null,
    onConfirmClick: () -> Unit
) {
    val dialogContainerColor = containerColor ?: MaterialTheme.colorScheme.surfaceContainerHigh

    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = dialogContainerColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.align(Alignment.End)) {
                TextButton(onClick = singleClick { onDismissRequest() }) {
                    Text(text = dismissText, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = singleClick { onConfirmClick() }) {
                    Text(text = confirmText, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UploadFailedDialogPreview() {
    LeafyTheme {
        LeafyDialog(
            onDismissRequest = {},
            title = "업로드에 실패했어요",
            text = "네트워크 상태를 확인한 뒤 다시 시도해 주세요.",
            dismissText = "닫기",
            confirmText = "다시 시도",
            containerColor = Color.White,
            onConfirmClick = {}
        )
    }
}
