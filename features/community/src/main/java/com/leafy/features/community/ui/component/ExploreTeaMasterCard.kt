package com.leafy.features.community.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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
    val isFollowing = master.isFollowing

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = master.profileImageUrl,
            contentDescription = master.name,
            placeholder = painterResource(id = SharedR.drawable.ic_profile_1),
            error = painterResource(id = SharedR.drawable.ic_profile_1),
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 이름 + 타이틀
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
                text = master.title,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        val buttonText = if (isFollowing) "팔로잉" else "+ 팔로우"
        val containerColor = if (isFollowing) colors.primary.copy(alpha = 0.08f) else colors.background
        val contentColor = colors.primary
        val borderColor = if (isFollowing) colors.primary.copy(alpha = 0.4f) else colors.outlineVariant

        OutlinedButton(
            onClick = {
                onFollowToggle(!isFollowing)
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
        Box(modifier = Modifier.padding(16.dp)) {
            ExploreTeaMasterCard(
                master = ExploreTeaMasterUi(
                    id = "1",
                    name = "그린티 마니아",
                    title = "녹차 & 블렌딩 전문가",
                    profileImageUrl = null,
                    isFollowing = false
                )
            )
        }
    }
}