package com.leafy.features.community.presentation.screen.write.section

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun TagInputSection(
    tags: List<String>,
    currentInput: String,
    onValueChange: (String) -> Unit,
    onRemoveTag: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        if (tags.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                items(tags) { tag ->
                    InputChip(
                        selected = true,
                        onClick = { onRemoveTag(tag) },
                        label = { Text(tag) },
                        trailingIcon = { Icon(Icons.Rounded.Close, null, modifier = Modifier.size(14.dp)) },
                        colors = InputChipDefaults.inputChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer, selectedLabelColor = MaterialTheme.colorScheme.onSurface),
                        border = null
                    )
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("#", color = MaterialTheme.colorScheme.primary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(4.dp))
            BasicTextField(
                value = currentInput,
                onValueChange = onValueChange,
                textStyle = TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { inner ->
                    if (currentInput.isEmpty()) Text("태그 입력 (스페이스바로 추가)", color = Color.Gray.copy(alpha = 0.5f), fontSize = 14.sp)
                    inner()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TagInputSectionPreview() {
    LeafyTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("1. 태그가 없고 입력 중일 때", style = MaterialTheme.typography.titleSmall)
            TagInputSection(
                tags = emptyList(),
                currentInput = "핸드드립",
                onValueChange = {},
                onRemoveTag = {}
            )

            Text("2. 태그가 있고 입력창이 비었을 때", style = MaterialTheme.typography.titleSmall)
            TagInputSection(
                tags = listOf("홈카페", "아이스", "여름"),
                currentInput = "",
                onValueChange = {},
                onRemoveTag = {}
            )
        }
    }
}