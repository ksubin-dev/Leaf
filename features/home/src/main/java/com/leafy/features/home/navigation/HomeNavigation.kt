package com.leafy.features.home.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leafy.features.home.presentation.notification.NotificationScreen
import com.leafy.features.home.presentation.notification.NotificationViewModel
import com.leafy.features.home.presentation.notification.makeNotificationViewModelFactory // 팩토리 함수 Import
import com.leafy.features.home.presentation.home.HomeRoute
import com.leafy.features.home.presentation.ranking.RankingDetailRoute
import com.leafy.features.home.presentation.home.HomeViewModel
import com.leafy.features.home.presentation.home.HomeViewModelFactory
import com.leafy.features.home.presentation.ranking.RankingDetailViewModel
import com.leafy.features.home.presentation.ranking.RankingDetailViewModelFactory
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.navigation.MainNavigationRoute

fun NavGraphBuilder.homeNavGraph(
    navController: NavController,
    container: ApplicationContainer
) {
    composable<MainNavigationRoute.HomeTab> {
        val viewModel: HomeViewModel = viewModel(
            factory = HomeViewModelFactory(
                homeUseCases = container.homeUseCases,
                postUseCases = container.postUseCases,
                userUseCases = container.userUseCases,
                notificationUseCases = container.notificationUseCases
            )
        )

        HomeRoute(
            viewModel = viewModel,
            navController = navController,
            onRankingFilterClick = viewModel::onRankingFilterSelected,
            onMoreRankingClick = { currentFilter ->
                navController.navigate(
                    MainNavigationRoute.RankingDetail(initialFilterLabel = currentFilter.label)
                )
            }
        )
    }


    composable<MainNavigationRoute.RankingDetail> {
        val viewModel: RankingDetailViewModel = viewModel(
            factory = RankingDetailViewModelFactory(
                postUseCases = container.postUseCases
            )
        )

        RankingDetailRoute(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() },
            onItemClick = { postId ->
                navController.navigate(MainNavigationRoute.CommunityDetail(postId))
            }
        )
    }

    composable<MainNavigationRoute.Notification> {
        val viewModel: NotificationViewModel = viewModel(
            factory = makeNotificationViewModelFactory(container)
        )

        NotificationScreen(
            viewModel = viewModel,
            navController = navController,
            onBackClick = { navController.popBackStack() }
        )
    }
}