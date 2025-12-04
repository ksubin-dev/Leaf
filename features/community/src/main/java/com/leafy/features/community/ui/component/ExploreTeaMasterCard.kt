package com.leafy.features.community.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun ExploreTeaMasterCard(
    master: ExploreTeaMasterUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onFollowToggle: (Boolean) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    // 로컬 팔로우 상태 (서버 연동 전까지는 UI 전용)
    var isFollowing by remember { mutableStateOf(master.isFollowing) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 프로필 이미지
        Image(
            painter = painterResource(id = master.profileImageRes),
            contentDescription = master.name,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 이름 + 타이틀 + 한 줄 소개
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = master.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = colors.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = master.title,                 // 예: "녹차 & 블렌딩 전문가"
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
                maxLines = 1
            )
//            Spacer(modifier = Modifier.height(2.dp))
//            Text(
//                text = master.description,          // 한 줄 소개
//                style = MaterialTheme.typography.bodySmall,
//                color = colors.onSurfaceVariant,
//                maxLines = 1
//            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 팔로우 / 팔로잉 버튼
        val buttonText = if (isFollowing) "팔로잉" else "+ 팔로우"
        val containerColor =
            if (isFollowing) colors.primary.copy(alpha = 0.08f) else colors.background
        val contentColor =
            if (isFollowing) colors.primary else colors.primary
        val borderColor =
            if (isFollowing) colors.primary.copy(alpha = 0.4f) else colors.outlineVariant

        OutlinedButton(
            onClick = {
                isFollowing = !isFollowing
                onFollowToggle(isFollowing)
            },
            shape = RoundedCornerShape(999.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = containerColor,
                contentColor = contentColor
            ),
            border = BorderStroke(1.dp, borderColor)
        ) {
            Text(
                text = buttonText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExploreTeaMasterCardPreview() {
    LeafyTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            ExploreTeaMasterCard(
                master = ExploreTeaMasterUi(
                    name = "그린티 마니아",
                    title = "녹차 & 블렌딩 전문가",
                    profileImageRes = SharedR.drawable.ic_profile_1,
                    isFollowing = false
                )
            )
        }
    }
}