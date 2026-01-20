package com.leafy.features.mypage.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.component.LeafyProfileImage
import com.subin.leafy.domain.model.User

@Composable
fun ProfileHeader(
    user: User,
    modifier: Modifier = Modifier,
    onFollowerClick: () -> Unit,
    onFollowingClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LeafyProfileImage(
            imageUrl = user.profileImageUrl,
            size = 80.dp,
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            iconTint = MaterialTheme.colorScheme.outline,
            contentDescription = "프로필 이미지"
        )

        Spacer(modifier = Modifier.width(20.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = user.nickname,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = user.bio?.takeIf { it.isNotBlank() } ?: "찻잎과의 여정을 시작합니다 🌿",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {

                Row(
                    modifier = Modifier.clickable { onFollowerClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${user.socialStats.followerCount}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " 팔로워",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 2.dp) // 살짝 간격 줌
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Row(
                    modifier = Modifier.clickable { onFollowingClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${user.socialStats.followingCount}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " 팔로잉",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
        }
    }
}