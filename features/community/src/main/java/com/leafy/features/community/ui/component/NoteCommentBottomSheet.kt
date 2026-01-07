package com.leafy.features.community.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leafy.features.community.ui.CommunityCommentUi
import com.leafy.shared.ui.component.LeafyProfileImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCommentBottomSheet(
    onDismissRequest: () -> Unit,
    comments: List<CommunityCommentUi>,
    onSendComment: (String) -> Unit,
    onReplyClick: (String) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val colors = MaterialTheme.colorScheme
    var commentText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = colors.surface,
        modifier = Modifier.fillMaxHeight(0.85f)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "댓글",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )

            // 1. 댓글 리스트 영역
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = comments,
                    key = { it.id }
                ) { comment ->
                    CommentItem(
                        comment = comment,
                        onReplyClick = { onReplyClick(it) }
                    )
                }
            }

            Surface(
                tonalElevation = 4.dp,
                modifier = Modifier.imePadding(),
                color = colors.surface
            ) {
                Column {
                    HorizontalDivider(color = colors.outlineVariant, thickness = 0.5.dp)
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LeafyProfileImage(imageUrl = null, size = 36.dp)

                        TextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = {
                                Text("차에 대한 이야기를 나눠보세요...", style = MaterialTheme.typography.bodyMedium)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = colors.primary
                            ),
                            maxLines = 4
                        )

                        TextButton(
                            onClick = {
                                if (commentText.isNotBlank()) {
                                    onSendComment(commentText)
                                    commentText = ""
                                }
                            },
                            enabled = commentText.isNotBlank()
                        ) {
                            Text(
                                text = "게시",
                                color = if (commentText.isNotBlank()) colors.primary else colors.outline,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}