package com.leafy.features.note.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
    onNavigateBack: () -> Unit,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .verticalScroll(rememberScrollState())
        ) {
            NoteDetailHeader(
                teaName = uiState.teaName,
                teaType = uiState.teaType,
                imageUrl = uiState.dryLeafUri,
                onBackClick = onNavigateBack,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )

            DetailSectionCard(title = "Tea Information") {
                TeaInfoDetailContent(
                    TeaInfo(uiState.teaName, uiState.brandName, uiState.teaType, uiState.leafStyle, uiState.leafProcessing, uiState.teaGrade)
                )
            }

            DetailSectionCard(title = "Brewing Conditions") {
                BrewingConditionsContent(
                    condition = BrewingCondition(
                        waterTemp = uiState.waterTemp,
                        leafAmount = uiState.leafAmount,
                        brewTime = uiState.brewTime,
                        brewCount = uiState.brewCount,
                        teaware = uiState.teaware
                    )
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

            // 3. 사진 섹션
            DetailSectionCard(title = "Photos") {
                PhotoDetailSectionContent(uiState)
            }

            // 4. 별점 및 평점 숫자
            RatingInfoDetailSection(
                ratingInfo = RatingInfo(
                    stars = uiState.rating,
                    purchaseAgain = uiState.purchaseAgain
                )
            )
            Spacer(modifier = Modifier.height(24.dp))

            NoteActionButtons(
                onEditClick = onEditClick,
                onShareClick = onShareClick
            )

            Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding() + 32.dp))
        }
    }
}


@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun FinalNoteDetailScreenPreview() {
    LeafyTheme {
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
            memo = "Excellent balance of bergamot and base tea. The citrus notes are bright but not overpowering. Smooth mouthfeel with a pleasant astringency. Perfect afternoon tea.",
            rating = 5,
            purchaseAgain = true
        )

        NoteDetailScreen(
            uiState = mockState,
            onNavigateBack = {},
            onEditClick = {},
            onShareClick = {},
            onDeleteClick = {}
        )
    }
}