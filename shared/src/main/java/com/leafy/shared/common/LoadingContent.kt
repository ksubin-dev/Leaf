package com.leafy.shared.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.subin.leafy.domain.common.DataResourceResult

@Composable
fun <T> LoadingContent(
    result: DataResourceResult<T>,
    onRetry: () -> Unit = {},
    content: @Composable (T) -> Unit
) {
    when (result) {
        is DataResourceResult.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        is DataResourceResult.Success -> {
            content(result.data)
        }

        is DataResourceResult.Failure -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "데이터를 불러오지 못했습니다.",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = result.exception.message ?: "알 수 없는 에러가 발생했습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onRetry) {
                    Text("다시 시도")
                }
            }
        }

        is DataResourceResult.DummyConstructor -> {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}