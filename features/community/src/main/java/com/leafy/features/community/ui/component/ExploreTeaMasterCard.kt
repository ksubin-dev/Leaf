package com.leafy.features.community.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.component.LeafyProfileImage
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
        LeafyProfileImage(
            imageUrl = master.profileImageUrl,
            size = 52.dp
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = master.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.onSurface
            )
            Text(
                text = master.title,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        val buttonText = if (isFollowing) "팔로잉" else "+ 팔로우"
        val containerColor = if (isFollowing) colors.primary.copy(alpha = 0.08f) else colors.primary
        val contentColor = if (isFollowing) colors.primary else colors.onPrimary
        val border = if (isFollowing) BorderStroke(1.dp, colors.primary.copy(alpha = 0.4f)) else null

        Button(
            onClick = { onFollowToggle(!isFollowing) },
            shape = RoundedCornerShape(999.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor
            ),
            border = border,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = buttonText,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
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