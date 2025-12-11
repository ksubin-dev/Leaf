package com.leafy.features.mypage.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leafy.features.mypage.screen.MyPageScreen
import com.leafy.shared.navigation.MainNavigationRoute

fun NavGraphBuilder.mypageNavGraph(
    navController: NavController
){
    composable<MainNavigationRoute.MyPageTab>{
        MyPageScreen(
            onSettingsClick = {
                navController.navigate("settings_screen_route")
            }
        )
    }
}