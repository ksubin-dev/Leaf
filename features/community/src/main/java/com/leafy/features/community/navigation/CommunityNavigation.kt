package com.leafy.features.community.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leafy.features.community.screen.CommunityScreen
import com.leafy.features.community.ui.detail.CommunityPostDetailRoute
import com.leafy.features.community.ui.detail.CommunityPostDetailViewModel
import com.leafy.features.community.ui.detail.CommunityPostDetailViewModelFactory
import com.leafy.features.community.ui.write.CommunityWriteRoute
import com.leafy.features.community.ui.write.CommunityWriteViewModel
import com.leafy.features.community.ui.write.CommunityWriteViewModelFactory
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.navigation.MainNavigationRoute

fun NavGraphBuilder.communityNavGraph(
    navController: NavController,
    container: ApplicationContainer
) {
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

    composable<MainNavigationRoute.CommunityDetail> {
        val factory = CommunityPostDetailViewModelFactory(
            postUseCases = container.postUseCases,
            userUseCases = container.userUseCases
        )

        // ViewModel 생성
        val viewModel: CommunityPostDetailViewModel = viewModel(factory = factory)

        // Route 연결
        CommunityPostDetailRoute(
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToNoteDetail = { originNoteId ->
                navController.navigate(MainNavigationRoute.NoteDetail(originNoteId))
            }
        )
    }
}