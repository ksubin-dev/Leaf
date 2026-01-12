package com.leafy.features.note.navigation
//
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
//import com.leafy.features.note.ui.NoteViewModel
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