package com.leafy.features.note.screen

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.features.note.ui.sections.create.BasicInfoSection
import com.leafy.features.note.ui.sections.create.BrewingRecipeSection
import com.leafy.features.note.ui.sections.create.FinalRatingSection
import com.leafy.features.note.ui.sections.create.PhotosSection
import com.leafy.features.note.ui.sections.create.SensoryEvalSection
import com.leafy.features.note.ui.sections.create.TastingContextSection
import com.leafy.features.note.viewmodel.NoteSideEffect
import com.leafy.features.note.viewmodel.NoteUiState
import com.leafy.features.note.viewmodel.NoteViewModel
import com.leafy.shared.R
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LoadingOverlay
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.BodyType
import com.subin.leafy.domain.model.FlavorTag
import com.subin.leafy.domain.model.TeaType
import com.subin.leafy.domain.model.TeawareType
import com.subin.leafy.domain.model.WeatherType

@Composable
fun NoteScreen(
    viewModel: NoteViewModel,
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is NoteSideEffect.NavigateBack -> {
                    onNavigateBack()
                }
                is NoteSideEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        effect.message.asString(context),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    NoteContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onSave = viewModel::saveNote,

        onAddImages = viewModel::addImages,
        onRemoveImage = viewModel::removeImage,

        onTeaNameChange = viewModel::updateTeaName,
        onTeaBrandChange = viewModel::updateTeaBrand,
        onTeaTypeChange = viewModel::updateTeaType,
        onTeaOriginChange = viewModel::updateTeaOrigin,
        onTeaLeafStyleChange = viewModel::updateTeaLeafStyle,
        onTeaGradeChange = viewModel::updateTeaGrade,

        onDateTimeChange = viewModel::updateDateTime,
        onWeatherSelected = viewModel::updateWeather,
        onWithPeopleChange = viewModel::updateWithPeople,

        onWaterTempChange = viewModel::updateWaterTemp,
        onLeafAmountChange = viewModel::updateLeafAmount,
        onWaterAmountChange = viewModel::updateWaterAmount,
        onBrewTimeChange = viewModel::updateBrewTime,
        onInfusionCountChange = viewModel::updateInfusionCount,
        onTeawareChange = viewModel::updateTeaware,

        onFlavorTagToggle = viewModel::updateFlavorTag,
        onSweetnessChange = viewModel::updateSweetness,
        onSournessChange = viewModel::updateSourness,
        onBitternessChange = viewModel::updateBitterness,
        onAstringencyChange = viewModel::updateAstringency,
        onUmamiChange = viewModel::updateUmami,
        onBodyChange = viewModel::updateBodyType,
        onFinishChange = viewModel::updateFinish,
        onMemoChange = viewModel::updateMemo,

        onRatingChange = viewModel::updateStarRating,
        onPurchaseAgainChange = viewModel::updatePurchaseAgain
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteContent(
    uiState: NoteUiState,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
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
    onTeawareChange: (TeawareType) -> Unit,
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
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "새로운 차 기록",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = singleClick { onNavigateBack() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_back),
                                contentDescription = "뒤로가기",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    actions = {
                        Button(
                            onClick = singleClick { onSave() },
                            enabled = uiState.isFormValid && !uiState.isLoading,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = MaterialTheme.colorScheme.surface,
                                disabledContentColor = MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .height(36.dp)
                        ) {
                            Text(
                                text = "Save",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                )
            },
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    contentPadding = PaddingValues(vertical = 24.dp)
                ) {
                    item {
                        PhotosSection(
                            selectedImages = uiState.selectedImages,
                            onAddImages = onAddImages,
                            onRemoveImage = onRemoveImage
                        )
                    }
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
                    item {
                        TastingContextSection(
                            dateTime = uiState.selectedDateString,
                            onDateTimeChange = onDateTimeChange,
                            selectedWeather = uiState.selectedWeather,
                            onWeatherSelected = onWeatherSelected,
                            mood = uiState.withPeople,
                            onMoodChange = onWithPeopleChange
                        )
                    }
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
                        )
                    }
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
                    item {
                        FinalRatingSection(
                            rating = uiState.starRating,
                            purchaseAgain = uiState.purchaseAgain,
                            onRatingChange = onRatingChange,
                            onPurchaseAgainChange = onPurchaseAgainChange
                        )
                    }
                }
            }

        }
        LoadingOverlay(
            isLoading = uiState.isLoading,
            message = "백그라운드 저장소로 보내는 중..."
        )
    }
}

@Preview(showBackground = true, heightDp = 1500)
@Composable
fun NoteScreenPreview() {
    LeafyTheme {
        val dummyState = NoteUiState(
            teaName = "우전 녹차",
            teaBrand = "오설록",
            teaType = TeaType.GREEN,
            teaOrigin = "제주",
            teaware = TeawareType.GAIWAN,
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
            onNavigateBack = {},
            onSave = {},
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
