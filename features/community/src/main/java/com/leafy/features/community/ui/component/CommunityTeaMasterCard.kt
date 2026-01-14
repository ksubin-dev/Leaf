package com.leafy.features.community.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.features.community.ui.model.UserUiModel
import com.leafy.shared.ui.component.LeafyProfileImage
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun CommunityTeaMasterCard(
    master: UserUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onFollowToggle: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val isFollowing = master.isFollowing

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LeafyProfileImage(
            imageUrl = master.profileImageUrl,
            size = 56.dp
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = master.nickname,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                color = colors.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = master.title,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 3. 팔로우 버튼
        val buttonText = if (isFollowing) "팔로잉" else "+ 팔로우"

        val containerColor = if (isFollowing) colors.surfaceVariant else Color.White
        val contentColor = if (isFollowing) colors.onSurfaceVariant else colors.primary
        val border = if (isFollowing) null else BorderStroke(1.dp, colors.primary)

        OutlinedButton(
            onClick = onFollowToggle,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = containerColor,
                contentColor = contentColor
            ),
            border = border,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Text(
                text = buttonText,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommunityTeaMasterCardPreview() {
    LeafyTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            CommunityTeaMasterCard(
                master = UserUiModel(
                    userId = "1",
                    nickname = "그린티 마니아",
                    title = "녹차 & 블렌딩 전문가",
                    profileImageUrl = null,
                    isFollowing = false,
                    followerCount = "500",
                    expertTags = listOf("녹차", "블렌딩")
                )
            )
        }
    }
}