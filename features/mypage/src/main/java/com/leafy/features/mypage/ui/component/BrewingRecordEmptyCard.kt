package com.leafy.features.mypage.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.component.DottedRoundedBox
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun BrewingRecordEmptyCard(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DottedRoundedBox(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onAddClick() },
        cornerRadiusDp = 12f,
        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "이 날은 차를 마신 기록이 없네요.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = FontStyle.Italic
                ),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "기록 추가하기",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BrewingRecordEmptyCardPreview() {
    LeafyTheme {
        Box(modifier = Modifier.padding(20.dp)) {
            BrewingRecordEmptyCard(onAddClick = {})
        }
    }
}