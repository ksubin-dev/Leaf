package com.leafy.features.mypage.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserStats
import com.leafy.shared.R as SharedR

@Composable
fun ProfileHeader(
    user: User,
    stats: UserStats,
    profileImageRes: Int? = null,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 프로필 이미지 영역
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    if (profileImageRes != null) {
                        Image(
                            painter = painterResource(id = profileImageRes),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "${user.username}", // User 모델 사용
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "이번 주: ${stats.weeklyBrewingCount}회 브루잉 · 평균 ${stats.averageRating}★", // Stats 모델 사용
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 가로 스크롤 영역: Stats 모델의 데이터들로 칩 구성
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SnapshotChip(label = "선호 티", value = stats.preferredTea)
            SnapshotChip(label = "평균 우림", value = stats.averageBrewingTime)
            SnapshotChip(label = "이번 달", value = "${stats.monthlyBrewingCount}회 브루잉")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProfileHeaderPreview() {
    val mockUser = User(id = "1", username = "Felix", profileImageUrl = null)
    val mockStats = UserStats(
        weeklyBrewingCount = 3,
        averageRating = 4.5,
        preferredTea = "Oolong",
        averageBrewingTime = "3:00",
        monthlyBrewingCount = 12
    )

    LeafyTheme {
        ProfileHeader(
            user = mockUser,
            stats = mockStats,
            onSettingsClick = {}
        )
    }
}