package com.leafy.features.timer.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel // [필수 Import]
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leafy.features.timer.screen.PresetListScreen
import com.leafy.features.timer.screen.TimerScreen
import com.leafy.features.timer.ui.TimerViewModel
import com.leafy.shared.navigation.MainNavigationRoute

fun NavGraphBuilder.timerNavGraph(
    navController: NavController
) {
    composable<MainNavigationRoute.TimerTab> { backStackEntry ->

        val viewModel: TimerViewModel = hiltViewModel()

        TimerScreen(
            viewModel = viewModel,
            onBackClick = {
                navController.popBackStack()
            },
            onNavigateToNote = { navArgsJson ->
                val previousBackStackEntry = navController.previousBackStackEntry
                if (previousBackStackEntry?.destination?.route?.contains("NoteTab") == true) {
                    previousBackStackEntry.savedStateHandle["brewing_result"] = navArgsJson
                    navController.popBackStack()
                } else {
                    navController.navigate(
                        MainNavigationRoute.NoteTab(initialRecords = navArgsJson)
                    ) {
                        popUpTo(MainNavigationRoute.TimerTab) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            onNavigateToPresetList = {
                navController.navigate(MainNavigationRoute.TimerPresetList)
            }
        )
    }

    composable<MainNavigationRoute.TimerPresetList> { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry<MainNavigationRoute.TimerTab>()
        }
        val viewModel: TimerViewModel = hiltViewModel(parentEntry)

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        PresetListScreen(
            presets = uiState.presets,
            onBackClick = { navController.popBackStack() },
            onLockedClick = { viewModel.showDefaultPresetWarning() },
            onPresetSelect = { preset ->
                viewModel.selectPreset(preset)
                navController.popBackStack()
            },
            onAddPreset = viewModel::savePreset,
            onUpdatePreset = viewModel::savePreset,
            onDeletePreset = viewModel::deletePreset,
            effectFlow = viewModel.sideEffect,

            onSettingsClick = {
                navController.navigate(MainNavigationRoute.Settings)
            }
        )
    }
}