package com.leafy.features.note.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.leafy.features.note.screen.NoteDetailScreen
import com.leafy.features.note.screen.NoteScreen
import com.leafy.features.note.ui.NoteDetailUiEffect
import com.leafy.features.note.ui.NoteDetailViewModel
import com.leafy.features.note.ui.NoteViewModel
import com.leafy.features.note.ui.factory.NoteViewModelFactory
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.navigation.MainNavigationRoute
import com.subin.leafy.domain.model.InfusionRecord
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

fun NavGraphBuilder.noteNavGraph(
    navController: NavController,
    container: ApplicationContainer,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit = {}
) {
    composable<MainNavigationRoute.NoteTab> { backStackEntry ->
        val route: MainNavigationRoute.NoteTab = backStackEntry.toRoute()
        val initialRecords = route.initialRecords?.let { jsonString ->
            runCatching { Json.decodeFromString<List<InfusionRecord>>(jsonString) }.getOrElse { emptyList() }
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

    composable<MainNavigationRoute.NoteDetail> { backStackEntry ->
        val route: MainNavigationRoute.NoteDetail = backStackEntry.toRoute()
        val noteId = route.noteId

        val snackbarHostState = remember { SnackbarHostState() }

        val factory = NoteViewModelFactory(
            noteUseCases = container.noteUseCases,
            initialRecords = null,
            noteId = noteId
        )

        val viewModel: NoteDetailViewModel = viewModel(factory = factory)
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(viewModel.effect) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is NoteDetailUiEffect.ShowToast -> {
                        snackbarHostState.showSnackbar(
                            message = effect.message,
                            withDismissAction = true
                        )
                    }
                    is NoteDetailUiEffect.NavigateBack -> {
                        onNavigateBack()
                    }
                }
            }
        }

        NoteDetailScreen(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onNavigateBack = onNavigateBack,
            onEditClick = { onNavigateToEdit(noteId) },
            onShareClick = {
                viewModel.viewModelScope.launch {
                    snackbarHostState.showSnackbar("Sharing is not yet implemented.")
                }
            },
            onDeleteClick = {
                viewModel.deleteNote()
            }
        )
    }
}