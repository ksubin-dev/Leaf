package com.leafy.features.note.navigation


import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
        val brewingResult by savedStateHandle.getStateFlow<String?>("brewing_result", null).collectAsState()

        LaunchedEffect(brewingResult) {
            brewingResult?.let { json ->
                // ViewModel에 데이터 전달 (기존 입력값 덮어쓰기 방지 로직이 ViewModel에 있어야 함)
                viewModel.initFromTimerData(json)

                // 데이터를 소비했으면 제거해줍니다 (중복 실행 방지)
                savedStateHandle.remove<String>("brewing_result")
            }
        }

        NoteScreen(
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onSaveSuccess = {
                navController.popBackStack()
            },
            onNavigateToTimer = {
                navController.navigate(MainNavigationRoute.TimerTab) {
                    popUpTo(MainNavigationRoute.HomeTab) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
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