package com.leafy.features.community.presentation.screen.feed

import androidx.compose.foundation.BorderStroke
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
import com.leafy.features.community.presentation.common.model.UserUiModel
import com.leafy.shared.common.clickableSingle
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LeafyProfileImage
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun CommunityTeaMasterCard(
    modifier: Modifier = Modifier,
    master: UserUiModel,
    currentUserId: String? = null,
    onClick: () -> Unit = {},
    onFollowToggle: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val isFollowing = master.isFollowing

    val isMe = currentUserId != null && master.userId == currentUserId

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickableSingle(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LeafyProfileImage(
            imageUrl = master.profileImageUrl,
            size = 56.dp,
            onClick = { onClick() }
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
            if (master.expertTags.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    master.expertTags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = colors.primaryContainer.copy(alpha = 0.5f),
                            border = BorderStroke(0.5.dp, colors.outlineVariant)
                        ) {
                            Text(
                                text = tag,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.onSecondaryContainer
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = master.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (!isMe) {
            val buttonText = if (isFollowing) "팔로잉" else "+ 팔로우"

            val containerColor = if (isFollowing) colors.surfaceVariant else Color.White
            val contentColor = if (isFollowing) colors.onSurfaceVariant else colors.primary
            val border = if (isFollowing) null else BorderStroke(1.dp, colors.primary)

            OutlinedButton(
                onClick = singleClick { onFollowToggle() },
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
}

@Preview(showBackground = true, name = "티 마스터 카드 비교")
@Composable
private fun CommunityTeaMasterCardPreview() {
    LeafyTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Case 1: 전문가 태그 있음", style = MaterialTheme.typography.labelMedium)
            CommunityTeaMasterCard(
                master = UserUiModel(
                    userId = "1",
                    nickname = "그린티 마니아",
                    title = "녹차 & 블렌딩 전문가",
                    profileImageUrl = null,
                    isFollowing = false,
                    followerCount = "500",
                    expertTags = listOf("녹차", "말차", "블렌딩")
                )
            )

            HorizontalDivider()

            Text("Case 2: 전문가 태그 없음 (기존 타이틀)", style = MaterialTheme.typography.labelMedium)
            CommunityTeaMasterCard(
                master = UserUiModel(
                    userId = "my_id",
                    nickname = "나 자신",
                    title = "영국식 홍차 전문 큐레이터",
                    profileImageUrl = null,
                    isFollowing = true,
                    followerCount = "1.2k",
                    expertTags = emptyList()
                ),
                currentUserId = "my_id"
            )
        }
    }
}