package com.leafy.features.home.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leafy.features.home.screen.HomeRoute
import com.leafy.features.home.screen.ranking.RankingDetailRoute
import com.leafy.features.home.viewmodel.HomeViewModel
import com.leafy.features.home.viewmodel.HomeViewModelFactory
import com.leafy.features.home.viewmodel.RankingDetailViewModel
import com.leafy.features.home.viewmodel.RankingDetailViewModelFactory
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
                userUseCases = container.userUseCases
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
}