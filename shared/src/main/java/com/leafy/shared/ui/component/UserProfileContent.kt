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


@Composable
fun UserProfileContent(
    modifier: Modifier = Modifier,
    username: String,
    bio: String,
    notesCount: Int,
    postsCount: Int,
    followerCount: Int,
    followingCount: Int,
    rating: Double,
    badgesCount: Int,
    profileImageRes: Int,
    onEditProfileClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

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


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ProfileStatItem(label = "Brewing Notes", value = notesCount.toString(), modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(10.dp))
            ProfileStatItem(label = "Posts", value = postsCount.toString(), modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(10.dp))
            ProfileStatItem(label = "Avg Rating", value = String.format("%.1f", rating), modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(10.dp))
            ProfileStatItem(label = "Badges", value = badgesCount.toString(), modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))


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


@Composable
private fun ProfileStatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(74.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2
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
                notesCount = 127,
                postsCount = 43,
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