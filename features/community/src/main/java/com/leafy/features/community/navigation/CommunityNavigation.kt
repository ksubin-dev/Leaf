package com.leafy.features.community.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leafy.features.community.screen.CommunityScreen
import com.leafy.features.community.ui.write.CommunityWriteRoute
import com.leafy.features.community.ui.write.CommunityWriteViewModel
import com.leafy.features.community.ui.write.CommunityWriteViewModelFactory
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
                navController.navigate(MainNavigationRoute.CommunityDetail(postId))
            },
            onMasterClick = { userId ->
                // TODO: 프로필 연결
                // navController.navigate(MainNavigationRoute.MasterProfile(userId))
            },
            onMorePopularClick = { /* 더보기 로직 */ },
            onMoreBookmarkClick = { /* 더보기 로직 */ },
            onMoreMasterClick = { /* 더보기 로직 */ }
        )
    }

    // 2. 글쓰기 화면
    composable<MainNavigationRoute.CommunityWrite> {
        val viewModel: CommunityWriteViewModel = viewModel(
            factory = CommunityWriteViewModelFactory(
                postUseCases = container.postUseCases,
                noteUseCases = container.noteUseCases,
                userUseCases = container.userUseCases,
                imageUseCases = container.imageUseCases,
                imageCompressor = container.imageCompressor
            )
        )

        CommunityWriteRoute(
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onPostSuccess = {
                navController.popBackStack()
            }
        )
    }

    // 3. 상세 화면
    composable<MainNavigationRoute.CommunityDetail> { backStackEntry ->
        // ... (나중에 구현)
    }
}