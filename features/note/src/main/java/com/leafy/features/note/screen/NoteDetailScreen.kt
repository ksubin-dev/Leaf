package com.leafy.features.note.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.components.NoteActionButtons
import com.leafy.features.note.ui.components.NoteDetailHeader
import com.leafy.features.note.viewmodel.DetailViewModel
import com.leafy.features.note.viewmodel.DetailUiState
import com.leafy.features.note.ui.sections.detail.BrewingRecipeSection
import com.leafy.features.note.ui.sections.detail.FinalRatingSection
import com.leafy.features.note.ui.sections.detail.PhotoDetailSection
import com.leafy.features.note.ui.sections.detail.SensoryEvaluationSection
import com.leafy.features.note.ui.sections.detail.TastingContextSection
import com.leafy.features.note.ui.sections.detail.TeaInfoSection
import com.leafy.shared.ui.component.LoadingOverlay
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.*

@Composable
fun NoteDetailScreen(
    viewModel: DetailViewModel,
    noteId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    LaunchedEffect(uiState.isDeleteSuccess) {
        if (uiState.isDeleteSuccess) {
            onNavigateBack()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.userMessageShown()
        }
    }

    NoteDetailContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
        onNavigateToEdit = onNavigateToEdit,
        onDeleteNote = viewModel::deleteNote,
        onToggleLike = viewModel::toggleLike,
        onToggleBookmark = viewModel::toggleBookmark,
        onRetry = viewModel::retry,
        onShareClick = { /* TODO: 공유하기 구현 */ }
    )
}

@Composable
fun NoteDetailContent(
    uiState: DetailUiState,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onDeleteNote: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleBookmark: () -> Unit,
    onRetry: () -> Unit,
    onShareClick: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (uiState.note == null && uiState.errorMessage != null && !uiState.isLoading) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "노트를 불러오지 못했습니다.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(onClick = onRetry) {
                        Text("다시 시도")
                    }
                    TextButton(onClick = onNavigateBack) {
                        Text("뒤로 가기")
                    }
                }
            }
            else if (uiState.isLoading && uiState.note == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            else {
                uiState.note?.let { note ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        item {
                            NoteDetailHeader(
                                teaName = note.teaInfo.name,
                                teaType = note.teaInfo.type.label,
                                imageUrl = note.metadata.imageUrls.firstOrNull(),
                                isAuthor = uiState.isAuthor,
                                isLiked = uiState.isLiked,
                                isBookmarked = uiState.isBookmarked,
                                onBackClick = onNavigateBack,
                                onEditClick = { onNavigateToEdit(note.id) },
                                onDeleteClick = onDeleteNote,
                                onLikeClick = onToggleLike,
                                onBookmarkClick = onToggleBookmark
                            )
                        }

                        item {
                            TeaInfoSection(
                                teaInfo = note.teaInfo,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        item {
                            BrewingRecipeSection(
                                recipe = note.recipe,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        item {
                            TastingContextSection(
                                createdTimestamp = note.date,
                                metadata = note.metadata,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        item {
                            SensoryEvaluationSection(
                                evaluation = note.evaluation,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        item {
                            PhotoDetailSection(
                                imageUrls = note.metadata.imageUrls,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                onPhotoClick = { /* TODO: 확대 보기 */ }
                            )
                        }

                        item {
                            FinalRatingSection(
                                rating = note.rating,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        item {
                            NoteActionButtons(
                                isAuthor = uiState.isAuthor,
                                onEditClick = { onNavigateToEdit(note.id) },
                                onShareClick = onShareClick
                            )
                        }
                    }
                }
            }

            if (uiState.isLoading && uiState.note != null) {
                LoadingOverlay(isLoading = true, message = "처리 중입니다...")
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 2000)
@Composable
fun NoteDetailScreenPreview() {
    LeafyTheme {
        val mockNote = BrewingNote(
            id = "preview_id",
            ownerId = "user_id",
            teaInfo = TeaInfo(
                name = "우전 녹차",
                brand = "오설록",
                type = TeaType.GREEN,
                origin = "제주",
                leafStyle = "잎차",
                grade = "특우"
            ),
            recipe = BrewingRecipe(
                waterTemp = 70,
                leafAmount = 3f,
                waterAmount = 150,
                brewTimeSeconds = 120,
                infusionCount = 2,
                teaware = "다관"
            ),
            evaluation = SensoryEvaluation(
                flavorTags = listOf(FlavorTag.GREENISH, FlavorTag.NUTTY, FlavorTag.FRUITY),
                sweetness = 4,
                sourness = 1,
                bitterness = 2,
                astringency = 1,
                umami = 5,
                body = BodyType.MEDIUM,
                finishLevel = 4,
                memo = "제주 햇차의 싱그러움이 느껴지는 맛. 감칠맛이 아주 뛰어나고 떫은맛이 적어 편안하게 마시기 좋다."
            ),
            rating = RatingInfo(
                stars = 5,
                purchaseAgain = true
            ),
            metadata = NoteMetadata(
                weather = WeatherType.SUNNY,
                mood = "가족",
                imageUrls = listOf("dummy_url_1", "dummy_url_2", "dummy_url_3")
            ),
            stats = PostStatistics(10, 5, 0, 0),
            myState = PostSocialState(isLiked = true, isBookmarked = false),
            createdAt = System.currentTimeMillis(),
            isPublic = true,
            date = System.currentTimeMillis()
        )

        NoteDetailContent(
            uiState = DetailUiState(
                isLoading = false,
                note = mockNote,
                isAuthor = true,
                isLiked = true,
                isBookmarked = false
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateBack = {},
            onNavigateToEdit = {},
            onDeleteNote = {},
            onToggleLike = {},
            onToggleBookmark = {},
            onShareClick = {},
            onRetry = {}
        )
    }
}