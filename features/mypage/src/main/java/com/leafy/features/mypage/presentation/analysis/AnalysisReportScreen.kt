package com.leafy.features.mypage.presentation.analysis

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.features.mypage.presentation.analysis.component.AnalysisHabitSection
import com.leafy.features.mypage.presentation.analysis.component.AnalysisStatCard
import com.leafy.features.mypage.presentation.analysis.component.CaffeineTrendChart
import com.leafy.features.mypage.presentation.analysis.component.TeaDistributionChart
import com.leafy.shared.R
import com.leafy.shared.common.singleClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisReportScreen(
    onBackClick: () -> Unit,
    onShareClick: () -> Unit = {},
    viewModel: AnalysisViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is AnalysisSideEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        effect.message.asString(context),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_analysis_report)) },
                navigationIcon = {
                    IconButton(onClick = singleClick { onBackClick() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
    ) { innerPadding ->

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.analysisData == null || uiState.analysisData?.totalBrewingCount == 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.msg_analysis_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            uiState.analysisData?.let { data ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    AnalysisStatCard(
                        totalCount = data.totalBrewingCount,
                        streakDays = data.currentStreakDays,
                        monthlyCount = data.monthlyBrewingCount
                    )

                    if (data.totalBrewingCount > 0) {
                        AnalysisHabitSection(
                            preferredTemp = data.preferredTemperature,
                            avgTime = data.averageBrewingTime,
                            preferredTimeSlot = data.preferredTimeSlot,
                            avgRating = data.averageRating
                        )
                    }

                    if (data.weeklyCaffeineTrend.any { it > 0 }) {
                        Column {
                            Text(
                                text = stringResource(R.string.title_weekly_activity),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(R.string.format_weekly_count, data.weeklyCount),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "  |  " + stringResource(R.string.format_daily_caffeine, data.dailyCaffeineAvg),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            CaffeineTrendChart(
                                weeklyTrend = data.weeklyCaffeineTrend
                            )
                        }
                    }

                    if (data.teaTypeDistribution.isNotEmpty()) {
                        Column {
                            Text(
                                text = stringResource(R.string.title_tea_preference),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (data.favoriteTeaType != null && data.favoriteTeaType != "-") {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.format_favorite_tea, data.favoriteTeaType!!),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            TeaDistributionChart(
                                distribution = data.teaTypeDistribution
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = singleClick { onShareClick() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.action_share_report),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}