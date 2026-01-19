package com.leafy.features.note.ui.components

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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }

            if (isAuthor) {
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
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isLiked) SharedR.drawable.ic_like_filled else SharedR.drawable.ic_like
                            ),
                            contentDescription = "Like",
                            tint = if (isLiked) MaterialTheme.colorScheme.error else Color.White
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
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary else Color.White
                        )
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true, name = "1. 본인 글 (수정/삭제 메뉴)")
@Composable
fun NoteDetailHeaderAuthorPreview() {
    LeafyTheme {
        NoteDetailHeader(
            teaName = "내가 마신 동정오롱",
            teaType = "Oolong Tea (청차)",
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

@Preview(showBackground = true, name = "2. 타인 글 (좋아요/북마크 동작)")
@Composable
fun NoteDetailHeaderViewerPreview() {
    LeafyTheme {
        var isLiked by remember { mutableStateOf(false) }
        var isBookmarked by remember { mutableStateOf(false) }

        NoteDetailHeader(
            teaName = "티 소믈리에의 추천 녹차",
            teaType = "Green Tea (녹차)",
            imageUrl = null,
            isAuthor = false,
            isLiked = isLiked,
            isBookmarked = isBookmarked,
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onLikeClick = { isLiked = !isLiked },
            onBookmarkClick = { isBookmarked = !isBookmarked }
        )
    }
}