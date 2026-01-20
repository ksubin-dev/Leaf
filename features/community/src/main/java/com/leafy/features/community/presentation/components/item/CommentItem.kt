package com.leafy.features.community.presentation.components.item

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.shared.ui.model.CommentUiModel
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LeafyProfileImage
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun CommentItem(
    modifier: Modifier = Modifier,
    comment: CommentUiModel,
    onDeleteClick: () -> Unit = {},
    onProfileClick: (String) -> Unit = {}
    // onReplyClick: () -> Unit = {} // 추후 답글 기능 추가 시 주석 해제
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        LeafyProfileImage(
            imageUrl = comment.authorProfileUrl,
            size = 36.dp,
            onClick = { onProfileClick(comment.authorId) }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.authorName,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = colors.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = comment.timeAgo,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurface,
                lineHeight = 20.sp
            )

            // 답글 달기 버튼 (필요하면 주석 해제해서 사용)
            /*
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "답글 달기",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = colors.secondary,
                modifier = Modifier.clickable { onReplyClick() }
            )
            */
        }

        if (comment.isMine) {
            IconButton(
                onClick = singleClick { onDeleteClick() },
                modifier = Modifier
                    .size(24.dp)
                    .offset(y = (-2).dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "댓글 삭제",
                    tint = colors.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommentItemPreview() {
    LeafyTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            // 남이 쓴 댓글
            CommentItem(
                comment = CommentUiModel(
                    commentId = "1",
                    authorId = "user2",
                    authorName = "홍차왕자",
                    authorProfileUrl = null,
                    content = "우와 사진 색감이 너무 예쁘네요! 저도 마셔보고 싶어요.",
                    timeAgo = "10분 전",
                    isMine = false
                )
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 내가 쓴 댓글 (삭제 버튼 보임)
            CommentItem(
                comment = CommentUiModel(
                    commentId = "2",
                    authorId = "me",
                    authorName = "나야나",
                    authorProfileUrl = null,
                    content = "감사합니다! 정말 향이 좋아요.",
                    timeAgo = "방금 전",
                    isMine = true
                )
            )
        }
    }
}