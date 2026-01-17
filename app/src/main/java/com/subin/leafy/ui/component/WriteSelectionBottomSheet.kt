package com.subin.leafy.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.shared.R
import com.leafy.shared.ui.theme.LeafyTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteSelectionBottomSheet(
    onDismissRequest: () -> Unit,
    onNoteClick: () -> Unit,
    onPostClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "작성하기",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.padding(bottom = 32.dp)
            )


            SelectionItem(
                iconRes = R.drawable.ic_edit,
                title = "게시글",
                description = "나의 이야기를 공유해보세요",
                onClick = onPostClick
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(2.dp)),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))

            SelectionItem(
                iconRes = R.drawable.ic_leaf,
                title = "브루잉 노트",
                description = "오늘의 티 브루잉을 남겨보세요",
                onClick = onNoteClick
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onDismissRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "CANCEL",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
private fun SelectionItem(
    iconRes: Int,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFDDDDDD)
@Composable
fun WriteSelectionBottomSheetPreview() {
    LeafyTheme {
        WriteSelectionBottomSheet(
            onDismissRequest = {},
            onNoteClick = {},
            onPostClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SelectionItemPreview() {
    LeafyTheme {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .background(Color.White)
        ) {
            SelectionItem(
                iconRes = R.drawable.ic_edit,
                title = "게시글",
                description = "나의 이야기를 공유해보세요",
                onClick = {}
            )
        }
    }
}