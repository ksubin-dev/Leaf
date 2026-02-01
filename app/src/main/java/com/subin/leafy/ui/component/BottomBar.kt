package com.subin.leafy.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.leafy.shared.navigation.LeafyNavigation
import com.leafy.shared.navigation.MainNavigationRoute

@Composable
fun BottomBar(
    bottomNavItems: List<LeafyBottomAppBarItem>,
    currentDestination: NavDestination?,
    onBottomTabClick: (LeafyNavigation) -> Unit,
    onTimerButtonClick: () -> Unit
) {
    val timerItem = bottomNavItems.find { it.destination == MainNavigationRoute.TimerTab }
    val timerIconRes = timerItem?.iconRes ?: 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        NavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {
            bottomNavItems.forEach { item ->
                val isTimerTab = item.destination == MainNavigationRoute.TimerTab
                val isSelected = currentDestination?.hasRoute(item.destination::class) == true

                if (isTimerTab) {
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Box(modifier = Modifier.size(24.dp)) },
                        label = { Text(item.tabName) },
                        enabled = false,
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Transparent,
                            disabledIconColor = Color.Transparent,
                            disabledTextColor = MaterialTheme.colorScheme.tertiary
                        )
                    )
                } else {
                    NavigationBarItem(
                        selected = isSelected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.tertiary,
                            unselectedTextColor = MaterialTheme.colorScheme.tertiary
                        ),
                        label = { Text(item.tabName) },
                        icon = {
                            Icon(
                                painter = painterResource(id = item.iconRes),
                                contentDescription = item.tabName
                            )
                        },
                        onClick = { onBottomTabClick(item.destination) }
                    )
                }
            }
        }

        LeafyTimerButton(
            iconRes = timerIconRes,
            onClick = onTimerButtonClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp)
        )
    }
}