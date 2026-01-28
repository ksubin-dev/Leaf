package com.leafy.features.home.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leafy.features.home.presentation.home.HomeRoute
import com.leafy.features.home.presentation.home.HomeViewModel
import com.leafy.features.home.presentation.notification.NotificationScreen
import com.leafy.features.home.presentation.notification.NotificationViewModel
import com.leafy.features.home.presentation.ranking.RankingDetailRoute
import com.leafy.features.home.presentation.ranking.RankingDetailViewModel
import com.leafy.shared.navigation.MainNavigationRoute

fun NavGraphBuilder.homeNavGraph(
    navController: NavController
) {
    composable<MainNavigationRoute.HomeTab> {
        val viewModel: HomeViewModel = hiltViewModel()

        HomeRoute(
            navController = navController,
            onMoreRankingClick = { currentFilter ->
                navController.navigate(
                    MainNavigationRoute.RankingDetail(initialFilterLabel = currentFilter.label)
                )
            },
            viewModel = viewModel
        )
    }

    composable<MainNavigationRoute.RankingDetail> {
        val viewModel: RankingDetailViewModel = hiltViewModel()

        RankingDetailRoute(
            onBackClick = { navController.popBackStack() },
            onItemClick = { postId ->
                navController.navigate(MainNavigationRoute.CommunityDetail(postId))
            },
            viewModel = viewModel
        )
    }

    composable<MainNavigationRoute.Notification> {
        val viewModel: NotificationViewModel = hiltViewModel()

        NotificationScreen(
            navController = navController,
            onBackClick = { navController.popBackStack() },
            viewModel = viewModel
        )
    }
}