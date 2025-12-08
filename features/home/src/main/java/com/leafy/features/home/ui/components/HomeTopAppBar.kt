package com.leafy.features.home.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    TopAppBar(
        title = {
            // Leafy 텍스트 로고
            Text(
                text = "Leafy",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 32.sp
                ),
                color = colors.primary
            )
        },
        actions = {
            // 검색 아이콘
            IconButton(onClick = onSearchClick) {
                Icon(
                    painter = painterResource(id = SharedR.drawable.ic_search),
                    contentDescription = "Search",
                    tint = colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            // 알림 아이콘
            IconButton(onClick = onNotificationClick) {
                Icon(
                    painter = painterResource(id = SharedR.drawable.ic_notification),
                    contentDescription = "Notifications",
                    tint = colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            // 프로필 이미지
            IconButton(onClick = onProfileClick) {
                Image(
                    painter = painterResource(id = SharedR.drawable.ic_profile_3),
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colors.background
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeTopAppBarPreview() {
    LeafyTheme {
        HomeTopAppBar(
            onSearchClick = {},
            onNotificationClick = {},
            onProfileClick = {}
        )
    }
}