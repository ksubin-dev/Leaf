package com.leafy.features.community.presentation.components.sheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.model.CommentUiModel
import com.leafy.features.community.presentation.components.item.CommentItem
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LeafyDialog
import com.leafy.shared.ui.component.LeafyProfileImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCommentBottomSheet(
    isLoading: Boolean,
    commentInput: String,
    currentUserProfileUrl: String?,
    comments: List<CommentUiModel>,
    onDismissRequest: () -> Unit,
    onInputChange: (String) -> Unit,
    onSendComment: () -> Unit,
    onDeleteComment: (String) -> Unit,
    onUserProfileClick: (String) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

) {
    val colors = MaterialTheme.colorScheme

    var commentIdToDelete by remember { mutableStateOf<String?>(null) }

    if (commentIdToDelete != null) {
        LeafyDialog(
            onDismissRequest = { commentIdToDelete = null },
            title = "댓글 삭제",
            text = "정말 이 댓글을 삭제하시겠습니까?",
            confirmText = "삭제",
            dismissText = "취소",
            onConfirmClick = {
                onDeleteComment(commentIdToDelete!!)
                commentIdToDelete = null
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = colors.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.fillMaxHeight(0.85f),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "댓글 ${comments.size}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.5f))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(
                    items = comments,
                    key = { it.commentId }
                ) { comment ->
                    CommentItem(
                        comment = comment,
                        onDeleteClick = { commentIdToDelete = comment.commentId },
                        onProfileClick = onUserProfileClick
                    )
                }
            }

            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .imePadding(),
                color = colors.surface
            ) {
                Column {
                    HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.5f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LeafyProfileImage(
                            imageUrl = currentUserProfileUrl,
                            size = 36.dp
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        TextField(
                            value = commentInput,
                            onValueChange = onInputChange,
                            placeholder = {
                                Text("따뜻한 댓글을 남겨주세요...", style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant.copy(alpha = 0.6f))
                            },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = colors.primary
                            ),
                            maxLines = 3,
                            textStyle = MaterialTheme.typography.bodyMedium
                        )

                        TextButton(
                            modifier = Modifier.padding(end = 8.dp),
                            enabled = commentInput.isNotBlank() && !isLoading,
                            onClick = singleClick { onSendComment() }
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = colors.primary
                                )
                            } else {
                                Text(
                                    text = "게시",
                                    color = if (commentInput.isNotBlank()) colors.primary else colors.onSurfaceVariant.copy(alpha = 0.4f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
