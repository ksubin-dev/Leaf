package com.subin.leafy.ui.screen

import android.widget.Toast
import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.leafy.shared.ui.component.LeafyDialog
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.UploadQueue
import com.subin.leafy.domain.model.UploadStatus
import com.subin.leafy.domain.model.UploadTargetType
import com.subin.leafy.ui.component.BottomBar
import com.subin.leafy.ui.component.LeafyBottomAppBarItem
import com.subin.leafy.ui.component.WriteSelectionBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryPointScreen(
    startDestination: Any,
    latestUploadQueue: UploadQueue? = null,
    onRetryUpload: (String, UploadTargetType) -> Unit = { _, _ -> },
    pendingDeepLink: Any? = null,
    onDeepLinkConsumed: () -> Unit = {}
) {
    LeafyTheme {
        val context = LocalContext.current
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        var savedDeepLink by remember { mutableStateOf<Any?>(null) }
        val uploadStatusSessionStartedAt = remember { System.currentTimeMillis() }
        var blockingUploadQueue by remember { mutableStateOf<UploadQueue?>(null) }
        var dismissedBlockingUploadKey by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(pendingDeepLink) {
            if (pendingDeepLink != null) {
                val isAuthScreen = startDestination == MainNavigationRoute.Auth ||
                        currentDestination?.hierarchy?.any { it.route?.contains("Auth") == true } == true

                if (isAuthScreen) {
                    savedDeepLink = pendingDeepLink
                    onDeepLinkConsumed()
                } else {
                    kotlinx.coroutines.delay(500L)
                    try {
                        navController.navigate(pendingDeepLink)
                    } catch (e: Exception) {
                        Log.e("FCM_NAV", "화면 이동 실패 (에러 발생): ${e.message}", e)
                    } finally {
                        onDeepLinkConsumed()
                        Log.d("FCM_NAV", "DeepLink 처리 완료 (초기화)")
                    }
                }
            }
        }

        LaunchedEffect(
            latestUploadQueue?.id,
            latestUploadQueue?.status,
            latestUploadQueue?.updatedAt
        ) {
            val queue = latestUploadQueue ?: return@LaunchedEffect
            val statusKey = "${queue.id}_${queue.status}_${queue.updatedAt}"

            when (queue.status) {
                UploadStatus.UPLOADING,
                UploadStatus.SYNCED -> {
                    if (queue.updatedAt >= uploadStatusSessionStartedAt) {
                        Toast.makeText(
                            context,
                            queue.message ?: queue.status.defaultUploadMessage(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                UploadStatus.RETRYING -> Unit
                UploadStatus.FAILED,
                UploadStatus.AUTH_REQUIRED -> {
                    if (dismissedBlockingUploadKey != statusKey) {
                        blockingUploadQueue = queue
                    }
                }
                UploadStatus.PENDING -> Unit
            }
        }

        val allItems = remember { LeafyBottomAppBarItem.fetchBottomAppBarItems() }
        var showWriteSheet by remember { mutableStateOf(false) }

        val shouldHideBottomBar = currentDestination?.hierarchy?.any { destination ->
            val route = destination.route ?: return@any false
            listOf(
                "Auth", "Search", "TimerTab", "NoteTab", "NoteDetail",
                "CommunityWrite", "CommunityDetail", "PopularPostList",
                "TeaMasterList", "HallOfFameList", "UserProfile",
                "DailyRecords", "AnalysisReport", "TeaAddEdit",
                "Notification", "Settings", "BookmarkedPosts",
                "LikedPosts", "MyTeaCabinet", "FollowerList", "FollowingList"
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
                    authNavGraph(
                        navController = navController,
                        onAuthSuccess = {
                            val destination = savedDeepLink ?: MainNavigationRoute.HomeTab

                            navController.navigate(destination) {
                                popUpTo<MainNavigationRoute.Auth> { inclusive = true }
                            }
                            savedDeepLink = null
                        }
                    )
                    homeNavGraph(navController = navController)
                    noteNavGraph(navController = navController)
                    communityNavGraph(navController = navController)
                    timerNavGraph(navController = navController)
                    searchNavGraph(navController = navController)
                    mypageNavGraph(navController = navController)
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

            latestUploadQueue
                ?.takeIf { it.status == UploadStatus.RETRYING }
                ?.let { queue ->
                    UploadRetryStatusPill(
                        message = queue.message ?: queue.status.defaultUploadMessage(),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = if (shouldHideBottomBar) 24.dp else 112.dp)
                    )
                }

            blockingUploadQueue?.let { queue ->
                val statusKey = "${queue.id}_${queue.status}_${queue.updatedAt}"
                val isAuthRequired = queue.status == UploadStatus.AUTH_REQUIRED

                LeafyDialog(
                    onDismissRequest = {
                        dismissedBlockingUploadKey = statusKey
                        blockingUploadQueue = null
                    },
                    title = if (isAuthRequired) "로그인이 필요해요" else "업로드에 실패했어요",
                    text = if (isAuthRequired) {
                        "로그인 상태를 확인한 뒤 다시 시도해 주세요."
                    } else {
                        queue.message ?: "네트워크 상태를 확인한 뒤 다시 시도해 주세요."
                    },
                    dismissText = "닫기",
                    confirmText = if (isAuthRequired) "확인" else "다시 시도",
                    containerColor = Color.White,
                    onConfirmClick = {
                        dismissedBlockingUploadKey = statusKey
                        blockingUploadQueue = null
                        if (!isAuthRequired) {
                            onRetryUpload(queue.id, queue.targetType)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun UploadRetryStatusPill(
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.88f),
        contentColor = MaterialTheme.colorScheme.inverseOnSurface,
        shadowElevation = 6.dp
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 12.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun UploadStatus.defaultUploadMessage(): String {
    return when (this) {
        UploadStatus.PENDING -> "업로드 대기 중입니다."
        UploadStatus.UPLOADING -> "업로드 중입니다."
        UploadStatus.RETRYING -> "업로드 재시도 중입니다."
        UploadStatus.SYNCED -> "업로드 완료"
        UploadStatus.FAILED -> "업로드에 실패했어요."
        UploadStatus.AUTH_REQUIRED -> "로그인이 필요해요."
    }
}
