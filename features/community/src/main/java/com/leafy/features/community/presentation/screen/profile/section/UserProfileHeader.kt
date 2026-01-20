package com.leafy.features.community.presentation.screen.profile.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.shared.ui.model.UserUiModel
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LeafyProfileImage
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun UserProfileHeader(
    user: UserUiModel,
    isMe: Boolean,
    isFollowing: Boolean,
    postCount: Int,
    onFollowClick: () -> Unit,
    onFollowerClick: () -> Unit,
    onFollowingClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            LeafyProfileImage(
                imageUrl = user.profileImageUrl,
                size = 80.dp
            )

            Spacer(modifier = Modifier.width(24.dp))

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileStatItem("기록", postCount.toString(), onClick = null)
                ProfileStatItem("팔로워", user.followerCount, onClick = onFollowerClick)
                ProfileStatItem("팔로잉", user.followingCount, onClick = onFollowingClick)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = user.nickname, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            if (user.title.isNotBlank()) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = user.title,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        val bio = user.bio

        if (!bio.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isMe) {
            val buttonColor = if (isFollowing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
            val contentColor = if (isFollowing) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
            val text = if (isFollowing) "팔로잉" else "팔로우"

            Button(
                onClick = singleClick { onFollowClick() },
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor, contentColor = contentColor),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                Text(text = text, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ProfileStatItem(
    label: String,
    value: String,
    onClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        onClick = singleClick { onClick() }
                    )
                } else {
                    Modifier
                }
            )
            .padding(8.dp)
    ) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = label, fontSize = 13.sp, color = Color.Gray)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, name = "프로필 헤더 모음")
@Composable
private fun UserProfileHeaderPreview() {
    val dummyUser = UserUiModel(
        userId = "test_uid",
        nickname = "차 마시는 루시",
        title = "PRO BREWER",
        bio = "따뜻한 우롱차와 무이암차를 사랑합니다.\n매일 아침 차 한 잔으로 시작하는 기록들. 🌿",
        profileImageUrl = null,
        isFollowing = false,
        followerCount = "1.2k",
        followingCount = "480",
        postCount = "156",
        expertTags = listOf("무이암차", "봉황단총")
    )

    LeafyTheme {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp) // 각 케이스 사이 간격
        ) {
            Column {
                Text(
                    "1. 타인 (팔로우 전)",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                UserProfileHeader(
                    user = dummyUser,
                    isMe = false,
                    isFollowing = false,
                    postCount = 156,
                    onFollowClick = {},
                    onFollowerClick = {},
                    onFollowingClick = {}
                )
            }

            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

            Column {
                Text(
                    "2. 타인 (팔로잉 중)",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                UserProfileHeader(
                    user = dummyUser,
                    isMe = false,
                    isFollowing = true,
                    postCount = 156,
                    onFollowClick = {},
                    onFollowerClick = {},
                    onFollowingClick = {}
                )
            }

            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

            Column {
                Text(
                    "3. 내 프로필 (버튼 없음)",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                UserProfileHeader(
                    user = dummyUser,
                    isMe = true,
                    isFollowing = false,
                    postCount = 156,
                    onFollowClick = {},
                    onFollowerClick = {},
                    onFollowingClick = {}
                )
            }
        }
    }
}