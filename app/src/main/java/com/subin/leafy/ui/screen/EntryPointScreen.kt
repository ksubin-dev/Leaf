package com.subin.leafy.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.leafy.features.community.navigation.communityNavGraph
import com.leafy.features.home.navigation.homeNavGraph
import com.leafy.features.mypage.navigation.mypageNavGraph
import com.leafy.features.note.navigation.noteNavGraph
import com.leafy.features.timer.navigation.timerNavGraph
import com.leafy.shared.navigation.LeafyNavigation
import com.leafy.shared.navigation.MainNavigationRoute
import com.leafy.shared.ui.theme.LeafyBottomBarBackground
import com.leafy.shared.ui.theme.LeafyGray
import com.leafy.shared.ui.theme.LeafyGreen
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.ui.component.LeafyBottomAppBarItem
import com.subin.leafy.ui.component.LeafyTimerButton


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EntryPointScreen() {
    LeafyTheme {

        val navController = rememberNavController()

        // 전체 탭 리스트 (Home / Community / Timer / Note / My)
        val allItems = remember {
            LeafyBottomAppBarItem.fetchBottomAppBarItems()
        }

        val timerItem = allItems.first { it.destination == MainNavigationRoute.TimerTab }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        // 현재 탭 이름 (TopAppBar 타이틀)
        val currentTabName = allItems
            .firstOrNull { isDestinationSelected(currentDestination, it.destination) }
            ?.tabName ?: "Leafy"

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(text = currentTabName) }
                )
            },
            bottomBar = {
                // 바텀바 + 가운데 타이머 버튼을 함께 배치하는 영역
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 5개 칸을 모두 NavigationBar에 넣고, 가운데(Timer)는 투명 더미로 사용
                    NavigationBar(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        containerColor = LeafyBottomBarBackground
                    ) {
                        allItems.forEach { item ->
                            val isTimer = item.destination == MainNavigationRoute.TimerTab
                            val selected =
                                isDestinationSelected(currentDestination, item.destination)

                            if (isTimer) {
                                // 가운데 더미 칸: 눈에 보이지 않고 클릭도 안 됨, 간격 확보용
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { /* no-op */ },
                                    icon = {
                                        Box(
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = {},
                                    enabled = false,
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Transparent,
                                        selectedTextColor = Color.Transparent,
                                        unselectedIconColor = Color.Transparent,
                                        unselectedTextColor = Color.Transparent,
                                        indicatorColor = Color.Transparent
                                    )
                                )
                            } else {
                                // 실제로 보이는 4개 탭 (Home / Community / Note / My)
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
                                        selectedIconColor = LeafyGreen,
                                        selectedTextColor = LeafyGreen,
                                        unselectedIconColor = LeafyGray,
                                        unselectedTextColor = LeafyGray,
                                        indicatorColor = LeafyBottomBarBackground
                                    )
                                )
                            }
                        }
                    }

                    // NavigationBar 중앙 위에 겹치는 64x64 타이머 버튼
                    val timerSelected =
                        isDestinationSelected(currentDestination, MainNavigationRoute.TimerTab)

                    LeafyTimerButton(
                        iconRes = timerItem.iconRes,
                        selected = timerSelected,
                        onClick = {
                            navController.navigate(MainNavigationRoute.TimerTab) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-24).dp)   // 바텀바 윗선보다 위로 살짝 띄우기
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = MainNavigationRoute.HomeTab,
                modifier = Modifier.padding(paddingValues)
            ) {
                homeNavGraph(navController)
                noteNavGraph(navController)
                communityNavGraph(navController)
                timerNavGraph(navController)
                mypageNavGraph(navController)
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