package com.leafy.features.note.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.R as SharedR

@Composable
fun NoteDetailHeader(
    modifier: Modifier = Modifier,
    teaName: String,
    teaType: String,
    imageUrl: String?,
    isAuthor: Boolean,
    isLiked: Boolean,
    isBookmarked: Boolean,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onLikeClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth().height(320.dp)) {
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Tea Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = painterResource(id = SharedR.drawable.ic_sample_tea_4)
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = SharedR.drawable.ic_leaf),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                        startY = 400f
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            Text(
                text = teaName,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = teaType,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
        }

        // 상단 버튼 영역 (뒤로가기 & [메뉴 or 소셜])
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. 뒤로가기 버튼
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }

            // 2. 우측 버튼 영역 (isAuthor에 따른 분기)
            if (isAuthor) {
                // --- [본인 글] 수정/삭제 드롭다운 ---
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.MoreVert, "More", tint = Color.White)
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("수정하기") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = { showMenu = false; onEditClick() }
                        )
                        DropdownMenuItem(
                            text = { Text("삭제하기", color = Color.Red) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) },
                            onClick = { showMenu = false; onDeleteClick() }
                        )
                    }
                }
            } else {
                // --- [타인 글] 좋아요 & 북마크 ---
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // 좋아요 버튼
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isLiked) SharedR.drawable.ic_like_filled else SharedR.drawable.ic_like
                            ),
                            contentDescription = "Like",
                        )
                    }

                    IconButton(
                        onClick = onBookmarkClick,
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isBookmarked) SharedR.drawable.ic_bookmark_filled else SharedR.drawable.ic_bookmark_outline
                            ),
                            contentDescription = "Bookmark",
                        )
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true, name = "본인 글일 때 (수정/삭제)")
@Composable
fun NoteDetailHeaderAuthorPreview() {
    LeafyTheme {
        NoteDetailHeader(
            teaName = "동정오롱차 (Author)",
            teaType = "Oolong Tea",
            imageUrl = null,
            isAuthor = true,
            isLiked = false,
            isBookmarked = false,
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onLikeClick = {},
            onBookmarkClick = {}
        )
    }
}

@Preview(showBackground = true, name = "타인 글일 때 (좋아요/북마크)")
@Composable
fun NoteDetailHeaderViewerPreview() {
    LeafyTheme {
        NoteDetailHeader(
            teaName = "우전 녹차 (Viewer)",
            teaType = "Green Tea",
            imageUrl = "https://example.com/tea.jpg",
            isAuthor = false,
            isLiked = true,
            isBookmarked = true,
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onLikeClick = {},
            onBookmarkClick = {}
        )
    }
}