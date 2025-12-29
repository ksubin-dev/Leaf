package com.subin.leafy.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.leafy.features.auth.navigation.AuthRouteGraph
import com.leafy.features.auth.navigation.authNavGraph
import com.leafy.features.community.navigation.communityNavGraph
import com.leafy.features.home.navigation.homeNavGraph
import com.leafy.features.mypage.navigation.mypageNavGraph
import com.leafy.features.note.navigation.noteNavGraph
import com.leafy.features.timer.navigation.timerNavGraph
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.navigation.LeafyNavigation
import com.leafy.shared.navigation.MainNavigationRoute
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.di.ApplicationContainerImpl
import com.subin.leafy.ui.component.LeafyBottomAppBarItem
import com.subin.leafy.ui.component.LeafyTimerButton


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryPointScreen(container: ApplicationContainer) {
    LeafyTheme {
        val colors = MaterialTheme.colorScheme
        val navController = rememberNavController()
        val allItems = remember { LeafyBottomAppBarItem.fetchBottomAppBarItems() }
        val timerItem = allItems.first { it.destination == MainNavigationRoute.TimerTab }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val isAuthScreen = currentDestination?.hierarchy?.any {
            it.route?.contains("AuthRoute") == true || it.route?.contains("AuthRouteGraph") == true
        } == true

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (!isAuthScreen) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        NavigationBar(
                            modifier = Modifier.align(Alignment.BottomCenter),
                            containerColor = colors.background
                        ) {
                            allItems.forEach { item ->
                                val isTimer = item.destination == MainNavigationRoute.TimerTab
                                val selected = isDestinationSelected(currentDestination, item.destination)

                                if (isTimer) {
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = { /* no-op */ },
                                        icon = { Box(modifier = Modifier.size(24.dp)) },
                                        label = {},
                                        enabled = false,
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.Transparent,
                                            indicatorColor = Color.Transparent
                                        )
                                    )
                                } else {
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(item.destination) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                painter = painterResource(id = item.iconRes),
                                                contentDescription = item.tabName
                                            )
                                        },
                                        label = { Text(text = item.tabName) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = colors.primary,
                                            selectedTextColor = colors.primary,
                                            unselectedIconColor = colors.tertiaryContainer,
                                            unselectedTextColor = colors.tertiaryContainer,
                                            indicatorColor = colors.background
                                        )
                                    )
                                }
                            }
                        }

                        val timerSelected = isDestinationSelected(currentDestination, MainNavigationRoute.TimerTab)
                        LeafyTimerButton(
                            iconRes = timerItem.iconRes,
                            selected = timerSelected,
                            onClick = {
                                navController.navigate(MainNavigationRoute.TimerTab) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-24).dp)
                        )
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = AuthRouteGraph,
                modifier = Modifier.padding(
                    bottom = if (isAuthScreen) 0.dp else paddingValues.calculateBottomPadding()
                )
            ) {
                authNavGraph(
                    navController = navController,
                    container = container,
                    onAuthSuccess = {
                        navController.navigate(MainNavigationRoute.HomeTab) {
                            popUpTo(AuthRouteGraph) { inclusive = true }
                        }
                    }
                )

                homeNavGraph(navController)
                noteNavGraph(container = container, onNavigateBack = { navController.popBackStack() })
                communityNavGraph(navController = navController, container = container)
                timerNavGraph(navController = navController, container = container)
                mypageNavGraph(container = container, navController = navController)
            }
        }
    }
}

/**
 * 현재 Destination 이 지정한 LeafyNavigation 목적지와 같은지 체크
 */
private fun isDestinationSelected(
    currentDestination: NavDestination?,
    target: LeafyNavigation
): Boolean {
    val targetRoute = target::class.qualifiedName
    return currentDestination
        ?.hierarchy
        ?.any { it.route == targetRoute } == true
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EntryPointScreenPreview() {
    LeafyTheme {
        val dummyContainer = ApplicationContainerImpl()
        EntryPointScreen(container = dummyContainer)
    }
}