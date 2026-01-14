package com.leafy.features.note.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.R
import com.leafy.shared.ui.component.LoadingOverlay
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.BodyType
import com.subin.leafy.domain.model.FlavorTag
import com.subin.leafy.domain.model.TeaType
import com.subin.leafy.domain.model.WeatherType
import android.net.Uri
import androidx.compose.foundation.layout.size
import com.leafy.features.note.ui.sections.create.BasicInfoSection
import com.leafy.features.note.ui.sections.create.BrewingRecipeSection
import com.leafy.features.note.ui.sections.create.FinalRatingSection
import com.leafy.features.note.ui.sections.create.PhotosSection
import com.leafy.features.note.ui.sections.create.SensoryEvalSection
import com.leafy.features.note.ui.sections.create.TastingContextSection
import com.leafy.features.note.viewmodel.NoteUiState
import com.leafy.features.note.viewmodel.NoteViewModel

// ------------------------------------------------------------------------
// 1. Stateful Screen (ViewModel 연결용)
// ------------------------------------------------------------------------
@Composable
fun NoteScreen(
    viewModel: NoteViewModel,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    onNavigateToTimer: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Side Effects
    LaunchedEffect(uiState.isSaveSuccess) {
        if (uiState.isSaveSuccess) onSaveSuccess()
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.userMessageShown()
        }
    }

    // UI 그리기
    NoteContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
        onSave = viewModel::saveNote,
        onNavigateToTimer = onNavigateToTimer,

        // 이미지
        onAddImages = viewModel::addImages,
        onRemoveImage = viewModel::removeImage,

        // 기본 정보
        onTeaNameChange = viewModel::updateTeaName,
        onTeaBrandChange = viewModel::updateTeaBrand,
        onTeaTypeChange = viewModel::updateTeaType,
        onTeaOriginChange = viewModel::updateTeaOrigin,
        onTeaLeafStyleChange = viewModel::updateTeaLeafStyle,
        onTeaGradeChange = viewModel::updateTeaGrade,

        // 환경
        onDateTimeChange = viewModel::updateDateTime,
        onWeatherSelected = viewModel::updateWeather,
        onWithPeopleChange = viewModel::updateWithPeople,

        // 레시피
        onWaterTempChange = viewModel::updateWaterTemp,
        onLeafAmountChange = viewModel::updateLeafAmount,
        onWaterAmountChange = viewModel::updateWaterAmount,
        onBrewTimeChange = viewModel::updateBrewTime,
        onInfusionCountChange = viewModel::updateInfusionCount,
        onTeawareChange = viewModel::updateTeaware,

        // 감각 평가
        onFlavorTagToggle = viewModel::updateFlavorTag,
        onSweetnessChange = viewModel::updateSweetness,
        onSournessChange = viewModel::updateSourness,
        onBitternessChange = viewModel::updateBitterness,
        onAstringencyChange = viewModel::updateAstringency,
        onUmamiChange = viewModel::updateUmami,
        onBodyChange = viewModel::updateBodyType,
        onFinishChange = viewModel::updateFinish,
        onMemoChange = viewModel::updateMemo,

        // 최종 평가
        onRatingChange = viewModel::updateStarRating,
        onPurchaseAgainChange = viewModel::updatePurchaseAgain
    )
}

// ------------------------------------------------------------------------
// 2. Stateless Content
// ------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteContent(
    uiState: NoteUiState,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    onNavigateToTimer: () -> Unit,

    // Callbacks
    onAddImages: (List<Uri>) -> Unit,
    onRemoveImage: (Uri) -> Unit,
    onTeaNameChange: (String) -> Unit,
    onTeaBrandChange: (String) -> Unit,
    onTeaTypeChange: (TeaType) -> Unit,
    onTeaOriginChange: (String) -> Unit,
    onTeaLeafStyleChange: (String) -> Unit,
    onTeaGradeChange: (String) -> Unit,
    onDateTimeChange: (String) -> Unit,
    onWeatherSelected: (WeatherType) -> Unit,
    onWithPeopleChange: (String) -> Unit,
    onWaterTempChange: (String) -> Unit,
    onLeafAmountChange: (String) -> Unit,
    onWaterAmountChange: (String) -> Unit,
    onBrewTimeChange: (String) -> Unit,
    onInfusionCountChange: (String) -> Unit,
    onTeawareChange: (String) -> Unit,
    onFlavorTagToggle: (FlavorTag) -> Unit,
    onSweetnessChange: (Float) -> Unit,
    onSournessChange: (Float) -> Unit,
    onBitternessChange: (Float) -> Unit,
    onAstringencyChange: (Float) -> Unit,
    onUmamiChange: (Float) -> Unit,
    onBodyChange: (BodyType) -> Unit,
    onFinishChange: (Float) -> Unit,
    onMemoChange: (String) -> Unit,
    onRatingChange: (Int) -> Unit,
    onPurchaseAgainChange: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "새로운 차 기록",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "뒤로가기"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = onSave,
                        enabled = uiState.isFormValid && !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(end = 8.dp).size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "저장",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.isFormValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 24.dp)
            ) {
                // [1] 사진
                item {
                    PhotosSection(
                        selectedImages = uiState.selectedImages,
                        onAddImages = onAddImages,
                        onRemoveImage = onRemoveImage
                    )
                }
                // [2] 기본 정보
                item {
                    BasicInfoSection(
                        teaName = uiState.teaName,
                        onTeaNameChange = onTeaNameChange,
                        teaBrand = uiState.teaBrand,
                        onTeaBrandChange = onTeaBrandChange,
                        teaType = uiState.teaType,
                        onTeaTypeChange = onTeaTypeChange,
                        teaOrigin = uiState.teaOrigin,
                        onTeaOriginChange = onTeaOriginChange,
                        teaLeafStyle = uiState.teaLeafStyle,
                        onTeaLeafStyleChange = onTeaLeafStyleChange,
                        teaGrade = uiState.teaGrade,
                        onTeaGradeChange = onTeaGradeChange
                    )
                }
                // [3] 환경
                item {
                    TastingContextSection(
                        dateTime = uiState.selectedDateString,
                        onDateTimeChange = onDateTimeChange,
                        selectedWeather = uiState.selectedWeather,
                        onWeatherSelected = onWeatherSelected,
                        withPeople = uiState.withPeople,
                        onWithPeopleChange = onWithPeopleChange
                    )
                }
                // [4] 레시피
                item {
                    BrewingRecipeSection(
                        waterTemp = uiState.waterTemp,
                        onWaterTempChange = onWaterTempChange,
                        leafAmount = uiState.leafAmount,
                        onLeafAmountChange = onLeafAmountChange,
                        waterAmount = uiState.waterAmount,
                        onWaterAmountChange = onWaterAmountChange,
                        brewTime = uiState.brewTime,
                        onBrewTimeChange = onBrewTimeChange,
                        infusionCount = uiState.infusionCount,
                        onInfusionCountChange = onInfusionCountChange,
                        teaware = uiState.teaware,
                        onTeawareChange = onTeawareChange,
                        onTimerClick = onNavigateToTimer
                    )
                }
                // [5] 감각 평가
                item {
                    SensoryEvalSection(
                        flavorTags = uiState.flavorTags,
                        onFlavorTagToggle = onFlavorTagToggle,
                        sweetness = uiState.sweetness,
                        onSweetnessChange = onSweetnessChange,
                        sourness = uiState.sourness,
                        onSournessChange = onSournessChange,
                        bitterness = uiState.bitterness,
                        onBitternessChange = onBitternessChange,
                        astringency = uiState.astringency,
                        onAstringencyChange = onAstringencyChange,
                        umami = uiState.umami,
                        onUmamiChange = onUmamiChange,
                        body = uiState.body,
                        onBodyChange = onBodyChange,
                        finish = uiState.finish,
                        onFinishChange = onFinishChange,
                        memo = uiState.memo,
                        onMemoChange = onMemoChange
                    )
                }
                // [6] 최종 평가
                item {
                    FinalRatingSection(
                        rating = uiState.starRating,
                        purchaseAgain = uiState.purchaseAgain,
                        onRatingChange = onRatingChange,
                        onPurchaseAgainChange = onPurchaseAgainChange
                    )
                }
            }

            if (uiState.isLoading) {
                LoadingOverlay(isLoading = true, message = "기록을 저장하고 있습니다...")
            }
        }
    }
}

// ------------------------------------------------------------------------
// 3. Preview (ViewModel 없이 UI만 확인!)
// ------------------------------------------------------------------------
@Preview(showBackground = true, heightDp = 1500)
@Composable
fun NoteScreenPreview() {
    LeafyTheme {
        val dummyState = NoteUiState(
            teaName = "우전 녹차",
            teaBrand = "오설록",
            teaType = TeaType.GREEN,
            teaOrigin = "제주",
            waterTemp = "70",
            leafAmount = "5",
            waterAmount = "150",
            brewTime = "60",
            selectedDateString = "2026-01-05",
            selectedWeather = WeatherType.SUNNY,
            flavorTags = listOf(FlavorTag.GREENISH, FlavorTag.NUTTY),
            sweetness = 3f,
            umami = 4f,
            starRating = 5,
            purchaseAgain = true
        )

        NoteContent(
            uiState = dummyState,
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateBack = {},
            onSave = {},
            onNavigateToTimer = {},
            // 모든 콜백에 빈 람다 {} 전달
            onAddImages = {}, onRemoveImage = {}, onTeaNameChange = {},
            onTeaBrandChange = {}, onTeaTypeChange = {}, onTeaOriginChange = {},
            onTeaLeafStyleChange = {}, onTeaGradeChange = {}, onDateTimeChange = {},
            onWeatherSelected = {}, onWithPeopleChange = {}, onWaterTempChange = {},
            onLeafAmountChange = {}, onWaterAmountChange = {}, onBrewTimeChange = {},
            onInfusionCountChange = {}, onTeawareChange = {}, onFlavorTagToggle = {},
            onSweetnessChange = {}, onSournessChange = {}, onBitternessChange = {},
            onAstringencyChange = {}, onUmamiChange = {}, onBodyChange = {},
            onFinishChange = {}, onMemoChange = {}, onRatingChange = {},
            onPurchaseAgainChange = {}
        )
    }
}
