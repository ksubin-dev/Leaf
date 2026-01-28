package com.subin.leafy.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.leafy.features.auth.navigation.authNavGraph
import com.leafy.features.community.navigation.communityNavGraph
import com.leafy.features.home.navigation.homeNavGraph
import com.leafy.features.mypage.navigation.mypageNavGraph
import com.leafy.features.note.navigation.noteNavGraph
import com.leafy.features.search.searchNavGraph
import com.leafy.features.timer.navigation.timerNavGraph
import com.leafy.shared.navigation.MainNavigationRoute
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.ui.component.BottomBar
import com.subin.leafy.ui.component.LeafyBottomAppBarItem
import com.subin.leafy.ui.component.WriteSelectionBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryPointScreen(
    startDestination: Any
) {
    LeafyTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val allItems = remember { LeafyBottomAppBarItem.fetchBottomAppBarItems() }

        var showWriteSheet by remember { mutableStateOf(false) }

        val shouldHideBottomBar = currentDestination?.hierarchy?.any { destination ->
            val route = destination.route ?: return@any false
            listOf(
                "Auth",
                "Search",
                "TimerTab",
                "NoteTab",
                "NoteDetail",
                "CommunityWrite",
                "CommunityDetail",
                "PopularPostList",
                "TeaMasterList",
                "HallOfFameList",
                "UserProfile",
                "DailyRecords",
                "AnalysisReport",
                "TeaAddEdit",
                "Notification",
                "Settings",
                "BookmarkedPosts",
                "LikedPosts",
                "MyTeaCabinet",
                "FollowerList",
                "FollowingList"
            ).any { route.contains(it) }
        } == true

        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    if (!shouldHideBottomBar) {
                        BottomBar(
                            bottomNavItems = allItems,
                            currentDestination = currentDestination,
                            onTimerButtonClick = {
                                navController.navigate(MainNavigationRoute.TimerTab) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            onBottomTabClick = { destination ->
                                if (destination is MainNavigationRoute.NoteTab) {
                                    showWriteSheet = true
                                } else {
                                    navController.navigate(destination) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.padding(
                        bottom = if (shouldHideBottomBar) 0.dp else paddingValues.calculateBottomPadding()
                    )
                ) {
                    // 각 NavGraph 호출부에서 container 파라미터 제거 fetures 모듈 단 해결하고나면 에러 사라질 예정

                    authNavGraph(
                        navController = navController,
                        onAuthSuccess = {
                            navController.navigate(MainNavigationRoute.HomeTab) {
                                popUpTo<MainNavigationRoute.Auth> { inclusive = true }
                            }
                        }
                    )
                    homeNavGraph(
                        navController = navController
                    )
                    noteNavGraph(
                        navController = navController,
                    )
                    communityNavGraph(
                        navController = navController,
                    )
                    timerNavGraph(
                        navController = navController,
                    )
//
//                    // [중요] Search는 UseCase를 개별적으로 넘기던 것도 다 삭제합니다.
//                    // Hilt가 SearchViewModel 내부에서 알아서 주입받기 때문입니다.
//                    searchNavGraph(
//                        navController = navController,
//                        // postUseCases = container.postUseCases, [삭제]
//                        // userUseCases = container.userUseCases  [삭제]
//                    )
//
//                    mypageNavGraph(
//                        navController = navController,
//                        // container = container [삭제]
//                    )
                }
            }

            if (showWriteSheet) {
                WriteSelectionBottomSheet(
                    onDismissRequest = { showWriteSheet = false },
                    onNoteClick = {
                        showWriteSheet = false
                        navController.navigate(MainNavigationRoute.NoteTab(noteId = null)) {
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
        }
    }
}