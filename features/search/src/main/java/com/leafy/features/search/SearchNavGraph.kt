package com.leafy.features.search

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leafy.shared.navigation.MainNavigationRoute

fun NavGraphBuilder.searchNavGraph(
    navController: NavController
) {
    composable<MainNavigationRoute.Search> {
        val viewModel: SearchViewModel = hiltViewModel()

        SearchScreen(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() },
            onPostClick = { postId ->
                navController.navigate(MainNavigationRoute.CommunityDetail(postId))
            },
            onUserClick = { userId ->
                navController.navigate(MainNavigationRoute.UserProfile(userId))
            }
        )
    }
}