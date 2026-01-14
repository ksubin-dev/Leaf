package com.leafy.features.community.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leafy.features.community.screen.CommunityScreen
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.navigation.MainNavigationRoute

fun NavGraphBuilder.communityNavGraph(
    navController: NavController,
    container: ApplicationContainer
) {
    // 1. 커뮤니티 메인 탭
    composable<MainNavigationRoute.CommunityTab> {
        CommunityScreen(
            postUseCases = container.postUseCases,
            userUseCases = container.userUseCases,
            onPostClick = { postId ->
                // TODO: 게시글 상세 화면 구현 후 연결
                // navController.navigate(MainNavigationRoute.CommunityDetail(postId))
                println("게시글 클릭: $postId")
            },
            onMasterClick = { userId ->
                // TODO: 프로필 화면 구현 후 연결
                // navController.navigate(MainNavigationRoute.MasterProfile(userId))
                println("프로필 클릭: $userId")
            }
        )
    }

    // 2. 글쓰기 화면 (아직 UI 구현 전이라면 껍데기만)
    composable<MainNavigationRoute.CommunityWrite> {
        // CommunityWriteScreen(...)
    }

    // 3. 상세 화면 (아직 UI 구현 전이라면 껍데기만)
    composable<MainNavigationRoute.CommunityDetail> { backStackEntry ->
        // val route: MainNavigationRoute.CommunityDetail = backStackEntry.toRoute()
        // CommunityDetailScreen(postId = route.postId)
    }
}