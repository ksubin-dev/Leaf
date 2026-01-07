package com.leafy.features.note.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.NoteUiState
import com.leafy.features.note.ui.common.DetailSectionCard
import com.leafy.features.note.ui.detail.BrewingConditionsContent
import com.leafy.features.note.ui.detail.NoteActionButtons
import com.leafy.features.note.ui.detail.NoteDetailHeader
import com.leafy.features.note.ui.detail.PhotoDetailSectionContent
import com.leafy.features.note.ui.detail.RatingInfoDetailSection
import com.leafy.features.note.ui.detail.SensoryEvaluationContent
import com.leafy.features.note.ui.detail.TastingContextContent
import com.leafy.features.note.ui.detail.TeaInfoDetailContent
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.BodyType
import com.subin.leafy.domain.model.BrewingCondition
import com.subin.leafy.domain.model.NoteContext
import com.subin.leafy.domain.model.RatingInfo
import com.subin.leafy.domain.model.SensoryEvaluation
import com.subin.leafy.domain.model.TeaInfo
import com.subin.leafy.domain.model.WeatherType

@Composable
fun NoteDetailScreen(
    uiState: NoteUiState,
    isAuthor: Boolean,
    isProcessing: Boolean,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onLikeClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NoteDetailHeader(
                    teaName = uiState.teaName,
                    teaType = uiState.teaType,
                    imageUrl = uiState.liquorUri ?: uiState.dryLeafUri ?: uiState.teawareUri ?: uiState.additionalUri,
                    isAuthor = isAuthor,
                    isLiked = uiState.isLiked,
                    isBookmarked = uiState.isBookmarked,
                    onBackClick = onNavigateBack,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick,
                    onLikeClick = onLikeClick,
                    onBookmarkClick = onBookmarkClick
                )

                DetailSectionCard(title = "Tea Information") {
                    TeaInfoDetailContent(
                        TeaInfo(uiState.teaName, uiState.brandName, uiState.teaType, uiState.leafStyle, uiState.leafProcessing, uiState.teaGrade)
                    )
                }

                DetailSectionCard(title = "Brewing Conditions") {
                    BrewingConditionsContent(
                        condition = BrewingCondition(uiState.waterTemp, uiState.leafAmount, uiState.brewTime, uiState.brewCount, uiState.teaware)
                    )
                }

                DetailSectionCard(title = "Tasting Context") {
                    TastingContextContent(
                        NoteContext(uiState.dateTime, uiState.weather, uiState.withPeople)
                    )
                }

                DetailSectionCard(title = "Sensory Evaluation") {
                    SensoryEvaluationContent(
                        SensoryEvaluation(uiState.selectedTags, uiState.sweetness, uiState.sourness, uiState.bitterness, uiState.saltiness, uiState.umami, uiState.bodyType, uiState.finishLevel, uiState.memo)
                    )
                }

                val hasPhoto = listOf(uiState.dryLeafUri, uiState.liquorUri, uiState.teawareUri, uiState.additionalUri).any { !it.isNullOrBlank() }
                if (hasPhoto) {
                    DetailSectionCard(title = "Photos") {
                        PhotoDetailSectionContent(uiState)
                    }
                }

                RatingInfoDetailSection(
                    ratingInfo = RatingInfo(stars = uiState.rating, purchaseAgain = uiState.purchaseAgain)
                )

                NoteActionButtons(
                    onEditClick = if (isAuthor) onEditClick else null,
                    onShareClick = onShareClick
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FinalNoteDetailScreenPreview() {
    LeafyTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val mockState = NoteUiState(
            teaName = "Earl Grey Supreme",
            teaType = "Black Tea",
            brandName = "Twinings",
            leafStyle = "Loose Leaf",
            leafProcessing = "Whole Leaf",
            teaGrade = "FTGFOP",
            waterTemp = "95",
            leafAmount = "2.5",
            brewTime = "4:30",
            brewCount = "3",
            teaware = "Ceramic Gaiwan",
            dateTime = "Nov 13, 2024 • 3:30 PM",
            weather = WeatherType.CLEAR,
            withPeople = "Solo",
            selectedTags = setOf("Floral", "Sweet", "Fruity", "Woody"),
            sweetness = 7f,
            sourness = 2f,
            bitterness = 1f,
            saltiness = 0f,
            umami = 6F,
            bodyType = BodyType.FULL,
            finishLevel = 0.8f,
            memo = "Excellent balance of bergamot and base tea.",
            rating = 5,
            purchaseAgain = true
        )

        NoteDetailScreen(
            uiState = NoteUiState(teaName = "Earl Grey Supreme", isLiked = true),
            isAuthor = false,
            isProcessing = false,
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateBack = {},
            onEditClick = {},
            onShareClick = {},
            onDeleteClick = {},
            onLikeClick = {},
            onBookmarkClick = {}
        )
    }
}