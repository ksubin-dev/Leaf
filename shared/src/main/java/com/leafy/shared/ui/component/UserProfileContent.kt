package com.leafy.shared.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme

/**
 * 마이페이지 상단에 표시되는 사용자 프로필 정보 및 통계 영역 (설정 버튼 제외)
 */
@Composable
fun UserProfileContent(
    username: String,
    bio: String,
    sessionsCount: Int,
    notesCount: Int,
    followerCount: Int,
    followingCount: Int,
    rating: Double,
    badgesCount: Int,
    profileImageRes: Int,
    onEditProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // 1. 프로필 이미지, 이름 Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1-1. 프로필 이미지
            Image(
                painter = painterResource(id = profileImageRes),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceVariant)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 1-2. 이름 및 소개
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = username,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.onSurface
                )
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = colors.onSurfaceVariant)) {
                            append(bio) // Tea Enthusiast
                        }
                        append(" · ")
                        withStyle(style = SpanStyle(color = colors.onSurfaceVariant)) {
                            append("${notesCount} teas tasted")
                        }
                        append("\n")
                        withStyle(style = SpanStyle(color = colors.secondary)) {
                            append("팔로워 ${followerCount} · 팔로잉 ${followingCount}")
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. 통계 Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ProfileStatItem(label = "Sessions", value = sessionsCount.toString(), modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(10.dp))
            ProfileStatItem(label = "Teas", value = notesCount.toString(), modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(10.dp))
            ProfileStatItem(label = "Avg Rating", value = String.format("%.1f", rating), modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(10.dp))
            ProfileStatItem(label = "Badges", value = badgesCount.toString(), modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. 프로필 수정 버튼
        OutlinedButton(
            onClick = onEditProfileClick,
            modifier = Modifier.fillMaxWidth().height(40.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = colors.surface,
                contentColor = colors.primary,
            )
        ) {
            Text("Edit Profile", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
        }
    }
}

/**
 * 통계 항목 (Sessions, Teas, Rating, Badges) 카드 스타일을 표시하는 서브 컴포넌트
 */
@Composable
private fun ProfileStatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(68.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UserProfileContentPreview() {
    LeafyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            UserProfileContent(
                username = "TeaLover_Jane",
                bio = "Tea Enthusiast",
                sessionsCount = 127,
                notesCount = 43,
                followerCount = 120,
                followingCount = 45,
                rating = 4.2,
                badgesCount = 12,
                profileImageRes = SharedR.drawable.ic_profile_1,
                onEditProfileClick = {}
            )
        }
    }
}