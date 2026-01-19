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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.leafy.features.auth.navigation.authNavGraph
import com.leafy.features.community.navigation.communityNavGraph
import com.leafy.features.home.navigation.homeNavGraph
import com.leafy.features.note.navigation.noteNavGraph
import com.leafy.features.timer.navigation.timerNavGraph
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.navigation.LeafyNavigation
import com.leafy.shared.navigation.MainNavigationRoute
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.ui.component.LeafyBottomAppBarItem
import com.subin.leafy.ui.component.LeafyTimerButton
import com.subin.leafy.ui.component.WriteSelectionBottomSheet


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryPointScreen(container: ApplicationContainer, startDestination: Any) {
    LeafyTheme {
        val colors = MaterialTheme.colorScheme
        val navController = rememberNavController()
        val allItems = remember { LeafyBottomAppBarItem.fetchBottomAppBarItems() }
        val timerItem = allItems.first { it.destination == MainNavigationRoute.TimerTab }

        var showWriteSheet by remember { mutableStateOf(false) }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val shouldHideBottomBar = currentDestination?.hierarchy?.any { destination ->
            val route = destination.route ?: return@any false
            listOf(
                "Auth",             // 로그인/회원가입
                "TimerTab",         // 타이머 화면 (몰입)
                "NoteTab",          // 노트 작성/수정 화면
                "NoteDetail",       // 노트 상세
                "CommunityWrite",   // 커뮤니티 글쓰기
                "CommunityDetail",  // 게시글 상세
                "PopularPostList",  // 인기글 더보기 리스트
                "TeaMasterList",    // 다인 추천 더보기 리스트
                "HallOfFameList",   // 명예의 전당 더보기 리스트
                "DailyRecords",     // 캘린더 상세 기록
                "AnalysisReport",   // 분석 리포트
                "UserProfile"       // 타 유저 프로필
            ).any { route.contains(it) }
        } == true

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (!shouldHideBottomBar) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        NavigationBar(
                            modifier = Modifier.align(Alignment.BottomCenter),
                            containerColor = colors.background,
                            tonalElevation = 0.dp
                        ) {
                            allItems.forEach { item ->
                                val isTimer = item.destination == MainNavigationRoute.TimerTab
                                val selected = isDestinationSelected(currentDestination, item.destination)

                                val isNoteTab = item.destination is MainNavigationRoute.NoteTab

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
                                            if (isNoteTab) {
                                                showWriteSheet = true
                                            } else {
                                                navController.navigate(item.destination) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
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
            if (showWriteSheet) {
                WriteSelectionBottomSheet(
                    onDismissRequest = { showWriteSheet = false },
                    onNoteClick = {
                        showWriteSheet = false
                        navController.navigate(MainNavigationRoute.NoteTab(noteId = null)) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onPostClick = {
                        showWriteSheet = false
                        navController.navigate(MainNavigationRoute.CommunityWrite)
                    }
                )
            }
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(
                    bottom = if (shouldHideBottomBar) 0.dp else paddingValues.calculateBottomPadding()
                )
            ) {
                authNavGraph(
                    navController = navController,
                    container = container,
                    onAuthSuccess = {
                        navController.navigate(MainNavigationRoute.HomeTab) {
                            popUpTo<MainNavigationRoute.Auth> { inclusive = true }
                        }
                    }
                )
                homeNavGraph(
                    navController = navController,
                    container = container
                )
                noteNavGraph(
                    navController = navController,
                    container = container
                )
                communityNavGraph(navController = navController, container = container)
                timerNavGraph(navController = navController, container = container)
                //mypageNavGraph(container = container, navController = navController)
            }
        }
    }
}

private fun isDestinationSelected(
    currentDestination: NavDestination?,
    target: LeafyNavigation
): Boolean {
    val targetRoute = target::class.qualifiedName
    return currentDestination
        ?.hierarchy
        ?.any { it.route == targetRoute } == true
}