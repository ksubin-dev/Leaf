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
    container: ApplicationContainer
) {
    // 공통 팩토리 생성 (의존성 주입)
    val factory = NoteViewModelFactory(
        noteUseCases = container.noteUseCases,
        userUseCases = container.userUseCases,
        imageUseCases = container.imageUseCases,
        imageCompressor = container.imageCompressor
    )

    // 1. 노트 작성/수정 화면 (NoteTab)
    composable<MainNavigationRoute.NoteTab> { backStackEntry ->
        val route: MainNavigationRoute.NoteTab = backStackEntry.toRoute()
        val viewModel: NoteViewModel = viewModel(factory = factory)

        // 수정 모드인 경우 (noteId가 있을 때) 데이터 로드
        LaunchedEffect(route.noteId) {
            route.noteId?.let { id ->
                viewModel.loadNoteForEdit(id)
            }
        }

        NoteScreen(
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onSaveSuccess = { navController.popBackStack() },
            onNavigateToTimer = { /* TODO: 타이머 화면으로 이동 */ }
        )
    }

    // 2. 노트 상세 화면 (NoteDetail)
    composable<MainNavigationRoute.NoteDetail> { backStackEntry ->
        val route: MainNavigationRoute.NoteDetail = backStackEntry.toRoute()
        val viewModel: DetailViewModel = viewModel(factory = factory)

        NoteDetailScreen(
            viewModel = viewModel,
            noteId = route.noteId,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToEdit = { noteId ->
                // 수정 화면(NoteTab)으로 이동하면서 noteId 전달
                navController.navigate(MainNavigationRoute.NoteTab(noteId = noteId))
            }
        )
    }
}
//import androidx.compose.material3.SnackbarHostState
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import androidx.navigation.NavGraphBuilder
//import androidx.navigation.compose.composable
//import androidx.navigation.toRoute
//import com.leafy.features.note.screen.NoteDetailScreen
//import com.leafy.features.note.screen.NoteScreen
//import com.leafy.features.note.ui.NoteDetailUiEffect
//import com.leafy.features.note.ui.NoteDetailViewModel
//import com.leafy.features.note.viewmodel.NoteViewModel
//import com.leafy.features.note.ui.factory.NoteViewModelFactory
//import com.leafy.shared.di.ApplicationContainer
//import com.leafy.shared.navigation.MainNavigationRoute
//import com.subin.leafy.domain.model.InfusionRecord
//import kotlinx.serialization.json.Json
//
//fun NavGraphBuilder.noteNavGraph(
//    navController: NavController,
//    container: ApplicationContainer,
//    onNavigateBack: () -> Unit,
//    onNavigateToEdit: (String) -> Unit
//) {
//    composable<MainNavigationRoute.NoteTab> { backStackEntry ->
//        val route: MainNavigationRoute.NoteTab = backStackEntry.toRoute()
//
//        val initialRecords = route.initialRecords?.let { jsonString ->
//            runCatching { Json.decodeFromString<List<InfusionRecord>>(jsonString) }.getOrElse { emptyList() }
//        }
//
//        val factory = NoteViewModelFactory(
//            noteUseCases = container.noteUseCases,
//            communityUseCases = container.communityUseCases,
//            initialRecords = initialRecords,
//            noteId = route.noteId,
//            selectedDate = route.date
//        )
//        val viewModel: NoteViewModel = viewModel(factory = factory)
//
//        NoteScreen(
//            viewModel = viewModel,
//            onNavigateBack = onNavigateBack
//        )
//    }
//
//    // 2. 노트 상세 화면
//    composable<MainNavigationRoute.NoteDetail> { backStackEntry ->
//        val route: MainNavigationRoute.NoteDetail = backStackEntry.toRoute()
//        val noteId = route.noteId
//
//        val factory = NoteViewModelFactory(
//            noteUseCases = container.noteUseCases,
//            communityUseCases = container.communityUseCases,
//            noteId = noteId
//        )
//
//        val viewModel: NoteDetailViewModel = viewModel(factory = factory)
//
//        val uiState by viewModel.uiState.collectAsState()
//        val isAuthor by viewModel.isAuthor.collectAsState()
//        val isProcessing by viewModel.isProcessing.collectAsState()
//        val snackbarHostState = remember { SnackbarHostState() }
//
//        LaunchedEffect(Unit) {
//            viewModel.effect.collect { effect ->
//                when (effect) {
//                    is NoteDetailUiEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
//                    is NoteDetailUiEffect.NavigateBack -> onNavigateBack()
//                }
//            }
//        }
//
//        NoteDetailScreen(
//            uiState = uiState,
//            isAuthor = isAuthor,
//            isProcessing = isProcessing,
//            snackbarHostState = snackbarHostState,
//            onNavigateBack = onNavigateBack,
//            onEditClick = { onNavigateToEdit(noteId) },
//            onShareClick = { /* TODO: 공유 로직 구현 */ },
//            onDeleteClick = { viewModel.deleteNote() },
//            onLikeClick = { viewModel.toggleLike(uiState.isLiked) },
//            onBookmarkClick = { viewModel.toggleSave(uiState.isBookmarked) }
//        )
//    }
//}