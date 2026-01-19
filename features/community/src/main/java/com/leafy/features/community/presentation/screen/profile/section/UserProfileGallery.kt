package com.leafy.features.community.presentation.screen.profile.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.leafy.features.community.presentation.common.model.CommunityPostUiModel
import com.leafy.shared.R
import com.leafy.shared.common.clickableSingle

@Composable
fun UserProfileGallery(
    posts: List<CommunityPostUiModel>,
    onPostClick: (String) -> Unit
) {
    if (posts.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "아직 작성한 기록이 없습니다.",
                color = Color.Gray
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(1.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(posts) { post ->
                GalleryItem(
                    imageUrl = post.imageUrls.firstOrNull(),
                    onClick = { onPostClick(post.postId) }
                )
            }
        }
    }
}

@Composable
private fun GalleryItem(
    imageUrl: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(Color.LightGray)
            .clickableSingle { onClick() }
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_leaf),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.align(Alignment.Center).size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, name = "갤러리 모음")
@Composable
private fun UserProfileGalleryPreview() {
    fun createDummyPost(id: String, hasImage: Boolean): CommunityPostUiModel {
        return CommunityPostUiModel(
            postId = id,
            authorId = "user",
            authorName = "Tester",
            authorProfileUrl = null,
            isFollowingAuthor = false,
            title = "Title",
            content = "Content",
            imageUrls = if (hasImage) listOf("https://via.placeholder.com/150") else emptyList(),
            timeAgo = "1시간 전",
            teaType = null,
            brewingSummary = null,
            rating = null,
            likeCount = "10",
            commentCount = "5",
            viewCount = "100",
            bookmarkCount = "2",
            isLiked = false,
            isBookmarked = false
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "1. 게시글 없음",
            modifier = Modifier.padding(16.dp),
            color = Color.Gray,
            style = MaterialTheme.typography.labelMedium
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color(0xFFF5F5F5))
        ) {
            UserProfileGallery(
                posts = emptyList(),
                onPostClick = {}
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        Text(
            "2. 갤러리 그리드 (이미지 O / X)",
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.Gray,
            style = MaterialTheme.typography.labelMedium
        )

        val dummyPosts = listOf(
            createDummyPost("1", true),
            createDummyPost("2", true),
            createDummyPost("3", false),
            createDummyPost("4", true),
            createDummyPost("5", true),
        )

        Box(modifier = Modifier.weight(1f)) {
            UserProfileGallery(
                posts = dummyPosts,
                onPostClick = {}
            )
        }
    }
}