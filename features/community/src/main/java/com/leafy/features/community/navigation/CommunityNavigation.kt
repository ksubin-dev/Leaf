package com.leafy.features.community.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leafy.features.community.screen.CommunityScreen
import com.leafy.shared.navigation.MainNavigationRoute

fun NavGraphBuilder.communityNavGraph(
    navController: NavController
){
    composable<MainNavigationRoute.CommunityTab>{
        CommunityScreen(
            navController = navController
        )
    }
}