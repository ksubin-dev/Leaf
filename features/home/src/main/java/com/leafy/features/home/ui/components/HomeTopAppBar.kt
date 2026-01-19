package com.leafy.features.home.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.component.LeafyProfileImage // 커스텀 컴포넌트 import
import com.leafy.shared.ui.theme.LeafyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    userProfileUrl: String?,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    TopAppBar(
        title = {
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
            IconButton(onClick = onSearchClick) {
                Icon(
                    painter = painterResource(id = SharedR.drawable.ic_search),
                    contentDescription = "Search",
                    tint = colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(onClick = onNotificationClick) {
                Icon(
                    painter = painterResource(id = SharedR.drawable.ic_notification),
                    contentDescription = "Notifications",
                    tint = colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            LeafyProfileImage(
                imageUrl = userProfileUrl,
                size = 32.dp,
                onClick = onProfileClick
            )

            Spacer(modifier = Modifier.width(16.dp))
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
            userProfileUrl = null,
            onSearchClick = {},
            onNotificationClick = {},
            onProfileClick = {}
        )
    }
}