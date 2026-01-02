package com.leafy.features.note.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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

@RequiresApi(Build.VERSION_CODES.O)
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

        // 통합 팩토리 사용
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

    // 2. 노트 상세 화면 (NoteDetail)
    composable<MainNavigationRoute.NoteDetail> { backStackEntry ->
        val route: MainNavigationRoute.NoteDetail = backStackEntry.toRoute()
        val noteId = route.noteId

        val snackbarHostState = remember { SnackbarHostState() }
        // 🔹 UI 전용 로직을 위한 코루틴 스코프 (공유하기 등 단순 스낵바용)
        val scope = rememberCoroutineScope()

        // 통합 팩토리 사용 (NoteDetailViewModel 생성 지원)
        val factory = NoteViewModelFactory(
            noteUseCases = container.noteUseCases,
            initialRecords = null,
            noteId = noteId
        )

        val viewModel: NoteDetailViewModel = viewModel(factory = factory)
        val uiState by viewModel.uiState.collectAsState()
        val isProcessing by viewModel.isProcessing.collectAsState()

        LaunchedEffect(viewModel.effect) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is NoteDetailUiEffect.ShowSnackbar -> {
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
            isProcessing = isProcessing,
            snackbarHostState = snackbarHostState,
            onNavigateBack = onNavigateBack,
            onEditClick = { onNavigateToEdit(noteId) },
            onShareClick = {
                scope.launch {
                    snackbarHostState.showSnackbar("공유 기능은 아직 준비 중입니다.")
                }
            },
            onDeleteClick = {
                viewModel.deleteNote()
            }
        )
    }
}