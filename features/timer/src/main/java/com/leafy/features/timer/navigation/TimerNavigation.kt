package com.leafy.features.timer.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leafy.features.timer.screen.TimerScreen
import com.leafy.features.timer.ui.TimerViewModel
import com.leafy.features.timer.ui.factory.TimerViewModelFactory
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.navigation.MainNavigationRoute

fun NavGraphBuilder.timerNavGraph(
    navController: NavController,
    container: ApplicationContainer
) {
    composable<MainNavigationRoute.TimerTab> {
        val factory = TimerViewModelFactory(container.timerUseCases)
        val viewModel: TimerViewModel = viewModel(factory = factory)

        TimerScreen(
            viewModel = viewModel,
            onNavigateToNote = { json ->
                navController.navigate(MainNavigationRoute.NoteTab(initialRecords = json)) {
                    launchSingleTop = true
                }
            },
            onNavigateToSettings = {
                // 프리셋 설정 페이지 이동 로직 (추후 구현)
            }
        )
    }
}