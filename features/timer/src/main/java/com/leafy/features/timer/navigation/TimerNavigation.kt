package com.leafy.features.timer.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leafy.features.timer.screen.TimerScreen
import com.leafy.shared.navigation.MainNavigationRoute

fun NavGraphBuilder.timerNavGraph(
    navController: NavController
){
    composable<MainNavigationRoute.TimerTab>{
        TimerScreen(
            navController = navController
        )
    }
}