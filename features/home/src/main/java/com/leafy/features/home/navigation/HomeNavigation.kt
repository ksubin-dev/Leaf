package com.leafy.features.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leafy.features.home.screen.HeroImageDetailScreen
import com.leafy.features.home.screen.HomeScreen
import com.leafy.shared.navigation.MainNavigationRoute

/**
 * Leafy Home NavGraph
 * - MainNavigationRoute.HomeTab 에 해당하는 화면을 연결
 */
fun NavGraphBuilder.homeNavGraph(
    navController: NavController
) {
    composable<MainNavigationRoute.HomeTab> {
        HomeScreen(navController = navController)
    }

    composable("hero_image_detail_screen") {
        HeroImageDetailScreen(
            navController = navController
        )
    }
}