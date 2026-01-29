package com.leafy.features.note.navigation


import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.leafy.features.note.screen.NoteDetailScreen
import com.leafy.features.note.screen.NoteScreen
import com.leafy.features.note.viewmodel.DetailViewModel
import com.leafy.features.note.viewmodel.NoteViewModel
import com.leafy.shared.navigation.MainNavigationRoute

fun NavGraphBuilder.noteNavGraph(
    navController: NavController
) {

    composable<MainNavigationRoute.NoteTab> { backStackEntry ->
        val route: MainNavigationRoute.NoteTab = backStackEntry.toRoute()

        val viewModel: NoteViewModel = hiltViewModel()

        LaunchedEffect(route.initialRecords) {
            route.initialRecords?.let { json ->
                viewModel.initFromTimerData(json)
            }
        }

        LaunchedEffect(route.noteId) {
            route.noteId?.let { id ->
                viewModel.loadNoteForEdit(id)
            }
        }

        val savedStateHandle = backStackEntry.savedStateHandle
        val brewingResult by savedStateHandle.getStateFlow<String?>("brewing_result", null)
            .collectAsStateWithLifecycle()

        LaunchedEffect(brewingResult) {
            brewingResult?.let { json ->
                viewModel.initFromTimerData(json)
                savedStateHandle.remove<String>("brewing_result")
            }
        }

        NoteScreen(
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
        )
    }

    composable<MainNavigationRoute.NoteDetail> { backStackEntry ->
        val route: MainNavigationRoute.NoteDetail = backStackEntry.toRoute()

        val viewModel: DetailViewModel = hiltViewModel()

        NoteDetailScreen(
            viewModel = viewModel,
            noteId = route.noteId,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToEdit = { noteId ->
                navController.navigate(MainNavigationRoute.NoteTab(noteId = noteId))
            }
        )
    }
}