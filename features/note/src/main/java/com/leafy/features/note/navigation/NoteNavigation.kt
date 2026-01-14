package com.leafy.features.note.navigation


import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.leafy.features.note.factory.NoteViewModelFactory
import com.leafy.features.note.screen.NoteDetailScreen
import com.leafy.features.note.screen.NoteScreen
import com.leafy.features.note.viewmodel.DetailViewModel
import com.leafy.features.note.viewmodel.NoteViewModel
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.navigation.MainNavigationRoute

fun NavGraphBuilder.noteNavGraph(
    navController: NavController,
    container: ApplicationContainer,
) {
    val factory = NoteViewModelFactory(
        noteUseCases = container.noteUseCases,
        userUseCases = container.userUseCases,
        imageUseCases = container.imageUseCases,
        postUseCases = container.postUseCases,
        imageCompressor = container.imageCompressor
    )

    composable<MainNavigationRoute.NoteTab> { backStackEntry ->
        val route: MainNavigationRoute.NoteTab = backStackEntry.toRoute()
        val viewModel: NoteViewModel = viewModel(factory = factory)

        LaunchedEffect(route.noteId) {
            route.noteId?.let { id ->
                viewModel.loadNoteForEdit(id)
            }
        }

        NoteScreen(
            viewModel = viewModel,
            onNavigateBack = {
                navController.popBackStack() },
            onSaveSuccess = {
                navController.popBackStack() },
            onNavigateToTimer = { /* TODO: 타이머 화면으로 이동 */ }
        )
    }

    composable<MainNavigationRoute.NoteDetail> { backStackEntry ->
        val route: MainNavigationRoute.NoteDetail = backStackEntry.toRoute()
        val viewModel: DetailViewModel = viewModel(factory = factory)

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
