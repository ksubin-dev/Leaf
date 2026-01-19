package com.leafy.features.timer.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leafy.features.timer.screen.PresetListScreen
import com.leafy.features.timer.screen.TimerScreen
import com.leafy.features.timer.ui.TimerViewModel
import com.leafy.features.timer.ui.factory.TimerViewModelFactory
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.navigation.MainNavigationRoute

fun NavGraphBuilder.timerNavGraph(
    navController: NavController,
    container: ApplicationContainer
) {
    composable<MainNavigationRoute.TimerTab> { backStackEntry ->

        val viewModel: TimerViewModel = viewModel(
            factory = TimerViewModelFactory(container.timerUseCases)
        )

        TimerScreen(
            viewModel = viewModel,
            onBackClick = {
                navController.popBackStack()
            },
            onNavigateToNote = { navArgsJson ->
                navController.previousBackStackEntry?.savedStateHandle?.set("brewing_result", navArgsJson)
                navController.popBackStack()
            },
            onNavigateToPresetList = {
                navController.navigate(MainNavigationRoute.TimerPresetList)
            }
        )
    }

    composable<MainNavigationRoute.TimerPresetList> { backStackEntry ->
        val timerTabEntry = remember(backStackEntry) {
            navController.getBackStackEntry<MainNavigationRoute.TimerTab>()
        }
        val viewModel: TimerViewModel = viewModel(timerTabEntry)

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        PresetListScreen(
            presets = uiState.presets,
            onBackClick = { navController.popBackStack() },
            onLockedClick = {
                viewModel.showDefaultPresetWarning()
            },
            onPresetSelect = { preset ->
                viewModel.selectPreset(preset)
                navController.popBackStack()
            },
            onAddPreset = viewModel::savePreset,
            onUpdatePreset = viewModel::savePreset,
            onDeletePreset = viewModel::deletePreset,
            userMessage = uiState.userMessage,
            onMessageShown = viewModel::messageShown
        )
    }
}