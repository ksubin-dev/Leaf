package com.leafy.features.note.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.leafy.features.note.screen.NoteScreen
import com.leafy.features.note.ui.NoteViewModel
import com.leafy.features.note.ui.factory.NoteViewModelFactory
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.navigation.MainNavigationRoute
import com.subin.leafy.domain.model.InfusionRecord
import kotlinx.serialization.json.Json

fun NavGraphBuilder.noteNavGraph(
    container: ApplicationContainer,
    onNavigateBack: () -> Unit
) {
    composable<MainNavigationRoute.NoteTab> { backStackEntry ->
        val route: MainNavigationRoute.NoteTab = backStackEntry.toRoute()

        val initialRecords = route.initialRecords?.let { jsonString ->
            runCatching {
                Json.decodeFromString<List<InfusionRecord>>(jsonString)
            }.getOrElse {
                emptyList()
            }
        }

        val factory = NoteViewModelFactory(
            noteUseCases = container.noteUseCases,
            initialRecords = initialRecords
        )

        val viewModel: NoteViewModel = viewModel(factory = factory)

        NoteScreen(
            viewModel = viewModel,
            onNavigateBack = onNavigateBack
        )
    }
}