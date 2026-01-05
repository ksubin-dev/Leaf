package com.leafy.features.note.screen

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.features.note.ui.NoteUiEffect
import com.leafy.features.note.ui.NoteUiState
import com.leafy.features.note.ui.NoteViewModel
import com.leafy.features.note.ui.sections.*
import com.leafy.shared.common.LoadingOverlay
import com.subin.leafy.domain.model.BodyType
import com.subin.leafy.domain.model.WeatherType
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.ui.utils.showToast

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoteScreen(
    viewModel: NoteViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isProcessing by viewModel.isProcessing.collectAsStateWithLifecycle()

    val dryLeafLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { viewModel.updateContext(dryUri = it.toString()) } }

    val liquorLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { viewModel.updateContext(liqUri = it.toString()) } }

    val teawareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { viewModel.updateContext(teaUri = it.toString()) } }

    val additionalLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { viewModel.updateContext(addUri = it.toString()) } }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is NoteUiEffect.ShowSnackbar -> {
                    showToast(context = context, message = effect.message)
                }
                is NoteUiEffect.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        NoteContent(
            uiState = uiState,
            isProcessing = isProcessing,
            onNavigateBack = onNavigateBack,
            onSave = { viewModel.saveNote() },
            onClickDryLeaf = { dryLeafLauncher.launch("image/*") },
            onClickLiquor = { liquorLauncher.launch("image/*") },
            onClickTeaware = { teawareLauncher.launch("image/*") },
            onClickAdditional = { additionalLauncher.launch("image/*") },
            onUpdateTeaInfo = { name, brand, type, style, processing, grade ->
                viewModel.updateTeaInfo(name, brand, type, style, processing, grade)
            },
            onUpdateContext = { dateTime, weather, withPeople, dry, liq, tea, add ->
                viewModel.updateContext(dateTime, weather, withPeople, dry, liq, tea, add)
            },
            onUpdateCondition = { temp, amount, time, count, teaware ->
                viewModel.updateCondition(temp, amount, time, count, teaware)
            },
            onUpdateSensory = { tags, sweet, sour, bitter, salt, umami, body, finish, memo ->
                viewModel.updateSensory(tags, sweet, sour, bitter, salt, umami, body, finish, memo)
            },
            onUpdateRating = { rating, purchase ->
                viewModel.updateRating(rating, purchase)
            }
        )

        LoadingOverlay(
            isLoading = isProcessing,
            message = "브루잉 노트를 저장 중입니다...\n사진이 많으면 시간이 걸릴 수 있어요."
        )
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteContent(
    uiState: NoteUiState,
    isProcessing: Boolean,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    onClickDryLeaf: () -> Unit,
    onClickLiquor: () -> Unit,
    onClickTeaware: () -> Unit,
    onClickAdditional: () -> Unit,
    onUpdateTeaInfo: (String?, String?, String?, String?, String?, String?) -> Unit,
    onUpdateContext: (String?, WeatherType?, String?, String?, String?, String?, String?) -> Unit,
    onUpdateCondition: (String?, String?, String?, String?, String?) -> Unit,
    onUpdateSensory: (Set<String>?, Float?, Float?, Float?, Float?, Float?, BodyType?, Float?, String?) -> Unit,
    onUpdateRating: (Int?, Boolean?) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isSaved) "Edit Brewing Note" else "New Brewing Note",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = SharedR.drawable.ic_back),
                            contentDescription = "Back",
                            modifier = Modifier.height(20.dp)
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = onSave,
                        enabled = uiState.canSave && !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = "Save",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            PhotosSection(
                dryLeafUri = uiState.dryLeafUri,
                liquorUri = uiState.liquorUri,
                teawareUri = uiState.teawareUri,
                additionalUri = uiState.additionalUri,
                onClickDryLeaf = onClickDryLeaf,
                onClickTeaLiquor = onClickLiquor,
                onClickTeaware = onClickTeaware,
                onClickAdditional = onClickAdditional
            )

            Spacer(modifier = Modifier.height(24.dp))

            BasicTeaInformationSection(
                teaName = uiState.teaName,
                onTeaNameChange = { onUpdateTeaInfo(it, null, null, null, null, null) },
                brandName = uiState.brandName,
                onBrandNameChange = { onUpdateTeaInfo(null, it, null, null, null, null) },
                teaType = uiState.teaType,
                onTeaTypeChange = { onUpdateTeaInfo(null, null, it, null, null, null) },
                leafStyle = uiState.leafStyle,
                onLeafStyleChange = { onUpdateTeaInfo(null, null, null, it, null, null) },
                leafProcessing = uiState.leafProcessing,
                onLeafProcessingChange = { onUpdateTeaInfo(null, null, null, null, it, null) },
                teaGrade = uiState.teaGrade,
                onTeaGradeChange = { onUpdateTeaInfo(null, null, null, null, null, it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            TastingContextSection(
                dateTime = uiState.dateTime,
                selectedWeather = uiState.weather,
                withPeople = uiState.withPeople,
                onDateTimeChange = { onUpdateContext(it, null, null, null, null, null, null) },
                onWeatherSelected = { onUpdateContext(null, it, null, null, null, null, null) },
                onWithPeopleChange = { onUpdateContext(null, null, it, null, null, null, null) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            BrewingConditionSection(
                waterTemp = uiState.waterTemp,
                leafAmount = uiState.leafAmount,
                brewTime = uiState.brewTime,
                brewCount = uiState.brewCount,
                teawareType = uiState.teaware,
                onWaterTempChange = { onUpdateCondition(it, null, null, null, null) },
                onLeafAmountChange = { onUpdateCondition(null, it, null, null, null) },
                onBrewTimeChange = { onUpdateCondition(null, null, it, null, null) },
                onBrewCountChange = { onUpdateCondition(null, null, null, it, null) },
                onTeawareTypeChange = { onUpdateCondition(null, null, null, null, it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SensoryEvaluationSection(
                selectedTags = uiState.selectedTags,
                sweetIntensity = uiState.sweetness,
                sourIntensity = uiState.sourness,
                bitterIntensity = uiState.bitterness,
                saltyIntensity = uiState.saltiness,
                umamiIntensity = uiState.umami,
                bodyType = uiState.bodyType,
                finishValue = uiState.finishLevel,
                notes = uiState.memo,
                onTagsChange = { onUpdateSensory(it, null, null, null, null, null, null, null, null) },
                onSweetnessChange = { onUpdateSensory(null, it, null, null, null, null, null, null, null) },
                onSournessChange = { onUpdateSensory(null, null, it, null, null, null, null, null, null) },
                onBitternessChange = { onUpdateSensory(null, null, null, it, null, null, null, null, null) },
                onSaltyChange = { onUpdateSensory(null, null, null, null, it, null, null, null, null) },
                onUmamiChange = { onUpdateSensory(null, null, null, null, null, it, null, null, null) },
                onBodyTypeChange = { onUpdateSensory(null, null, null, null, null, null, it, null, null) },
                onFinishValueChange = { onUpdateSensory(null, null, null, null, null, null, null, it, null) },
                onNotesChange = { onUpdateSensory(null, null, null, null, null, null, null, null, it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            FinalRatingSection(
                rating = uiState.rating,
                purchaseAgain = uiState.purchaseAgain,
                onRatingChange = { onUpdateRating(it, null) },
                onPurchaseAgainChange = { onUpdateRating(null, it) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
private fun NoteScreenPreview() {
    LeafyTheme {
        val mockUiState = NoteUiState(
            teaName = "Dragon Well",
            brandName = "West Lake Tea",
            dateTime = "12/17/2025",
            weather = WeatherType.CLEAR,
            bodyType = BodyType.MEDIUM,
            waterTemp = "85",
            leafAmount = "3.0",
            brewTime = "2:00",
            memo = "Great green tea experience."
        )

        NoteContent(
            uiState = mockUiState,
            isProcessing = false,
            onNavigateBack = {},
            onSave = {},
            onClickDryLeaf = {},
            onClickLiquor = {},
            onClickTeaware = {},
            onClickAdditional = {},
            onUpdateTeaInfo = { _, _, _, _, _, _ -> },
            onUpdateContext = { _, _, _, _, _, _, _ -> },
            onUpdateCondition = { _, _, _, _, _ -> },
            onUpdateSensory = { _, _, _, _, _, _, _, _, _ -> },
            onUpdateRating = { _, _ -> }
        )
    }
}
