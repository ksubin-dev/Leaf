package com.leafy.features.mypage.presentation.analysis

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.features.mypage.presentation.analysis.component.AnalysisHabitSection
import com.leafy.features.mypage.presentation.analysis.component.AnalysisStatCard
import com.leafy.features.mypage.presentation.analysis.component.CaffeineTrendChart
import com.leafy.features.mypage.presentation.analysis.component.TeaDistributionChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisReportScreen(
    viewModel: AnalysisViewModel,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onMessageShown()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("나의 티 리포트") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.analysisData == null || uiState.analysisData?.totalBrewingCount == 0) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "아직 분석할 데이터가 충분하지 않아요 \n차를 마시고 기록을 남겨보세요!",
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
                                text = "주간 활동 및 카페인",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "이번 주 ${data.weeklyCount}회",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "  |  일일 평균 ${data.dailyCaffeineAvg}mg",
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
                                text = "즐겨 마시는 차 종류",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (data.favoriteTeaType != null && data.favoriteTeaType != "-") {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "가장 많이 마신 차는 '${data.favoriteTeaType}'입니다.",
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
                        onClick = onShareClick,
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
                            text = "리포트 공유하기",
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