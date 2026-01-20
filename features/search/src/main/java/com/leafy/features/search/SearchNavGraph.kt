package com.leafy.features.search

import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leafy.shared.navigation.MainNavigationRoute
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases

fun NavGraphBuilder.searchNavGraph(
    navController: NavController,
    postUseCases: PostUseCases,
    userUseCases: UserUseCases
) {
    composable<MainNavigationRoute.Search> {
        val viewModel: SearchViewModel = viewModel(
            factory = SearchViewModelFactory.provide(postUseCases, userUseCases)
        )

        SearchScreen(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() },
            onPostClick = { postId ->
                navController.navigate(MainNavigationRoute.CommunityDetail(postId))
            },
            onUserClick = { userId ->
                Log.d("SearchNav", "Moving to profile: $userId")
                navController.navigate(MainNavigationRoute.UserProfile(userId))
            }
        )
    }
}