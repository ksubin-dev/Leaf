package com.leafy.features.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.component.LeafyProfileImage
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.common.singleClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    userProfileUrl: String?,
    hasUnreadNotifications: Boolean,
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
            IconButton(onClick = singleClick { onSearchClick() }) {
                Icon(
                    painter = painterResource(id = SharedR.drawable.ic_search),
                    contentDescription = "Search",
                    tint = colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(onClick = singleClick { onNotificationClick() }) {
                Box {
                    Icon(
                        painter = painterResource(id = SharedR.drawable.ic_notification),
                        contentDescription = "Notifications",
                        tint = colors.primary,
                        modifier = Modifier.size(24.dp)
                    )

                    if (hasUnreadNotifications) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color.Red, CircleShape)
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = 2.dp)
                        )
                    }
                }
            }

            LeafyProfileImage(
                imageUrl = userProfileUrl,
                size = 32.dp,
                onClick = singleClick { onProfileClick() }
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
            onProfileClick = {},
            hasUnreadNotifications = true
        )
    }
}