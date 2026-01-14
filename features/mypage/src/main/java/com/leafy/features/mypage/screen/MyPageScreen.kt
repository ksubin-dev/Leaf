package com.leafy.features.mypage.screen
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LifecycleEventObserver
//import androidx.lifecycle.compose.LocalLifecycleOwner
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.leafy.features.mypage.ui.MyPageUiEffect
//import com.leafy.features.mypage.ui.MyPageUiState
//import com.leafy.features.mypage.ui.MyPageViewModel
//import com.leafy.features.mypage.ui.component.MyPageTopAppBar
//import com.leafy.features.mypage.ui.component.ProfileHeader
//import com.leafy.features.mypage.ui.session.MyPageCalendarSection
//import com.leafy.features.mypage.ui.session.MyPageInsightSection
//import com.leafy.shared.ui.theme.LeafyTheme
//import com.subin.leafy.domain.model.BrewingInsight
//import com.subin.leafy.domain.model.BrewingRecord
//import com.subin.leafy.domain.model.InsightAction
//import com.subin.leafy.domain.model.InsightCategory
//import com.subin.leafy.domain.model.InsightContent
//import com.subin.leafy.domain.model.User
//import com.subin.leafy.domain.model.UserStats
//import java.time.LocalDate
//
//@Composable
//fun MyPageScreen(
//    viewModel: MyPageViewModel,
//    onSettingsClick: () -> Unit,
//    onAddRecordClick: (String) -> Unit,
//    onEditRecordClick: (String) -> Unit,
//    onRecordDetailClick: (String) -> Unit,
//    onViewAllRecordsClick: (String) -> Unit,
//    onInsightClick: (BrewingInsight) -> Unit,
//    onFullReportClick: () -> Unit
//) {
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//    val snackbarHostState = remember { SnackbarHostState() }
//    val lifecycleOwner = LocalLifecycleOwner.current
//
//    DisposableEffect(lifecycleOwner) {
//        val observer = LifecycleEventObserver { _, event ->
//            if (event == Lifecycle.Event.ON_RESUME) {
//                viewModel.refresh()
//            }
//        }
//        lifecycleOwner.lifecycle.addObserver(observer)
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(observer)
//        }
//    }
//
//    LaunchedEffect(Unit) {
//        viewModel.effect.collect { effect ->
//            when (effect) {
//                is MyPageUiEffect.NavigateToDetail -> onRecordDetailClick(effect.noteId)
//                is MyPageUiEffect.NavigateToDailyRecords -> onViewAllRecordsClick(effect.date)
//                is MyPageUiEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
//            }
//        }
//    }
//
//    LaunchedEffect(uiState.errorMessage) {
//        uiState.errorMessage?.let {
//            snackbarHostState.showSnackbar(it)
//        }
//    }
//
//    Scaffold(
//        modifier = Modifier.fillMaxSize(),
//        topBar = {
//            MyPageTopAppBar(
//                onSettingsClick = onSettingsClick
//            )
//        },
//        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
//    ) { innerPadding ->
//        MyPageContent(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding),
//            uiState = uiState,
//            onDateClick = viewModel::onDateSelected,
//            onPrevMonth = { viewModel.changeMonth(-1) },
//            onNextMonth = { viewModel.changeMonth(1) },
//            onAddRecordClick = { onAddRecordClick(uiState.selectedDateString) },
//            onEditRecordClick = onEditRecordClick,
//            onRecordDetailClick = viewModel::onRecordDetailClick,
//            onViewAllClick = viewModel::onViewAllClick,
//            onInsightDetailClick = onInsightClick,
//            onFullReportClick = onFullReportClick
//        )
//    }
//}
//
//@Composable
//private fun MyPageContent(
//    modifier: Modifier = Modifier,
//    uiState: MyPageUiState,
//    onDateClick: (Int) -> Unit,
//    onPrevMonth: () -> Unit,
//    onNextMonth: () -> Unit,
//    onAddRecordClick: () -> Unit,
//    onEditRecordClick: (String) -> Unit,
//    onRecordDetailClick: (String) -> Unit,
//    onViewAllClick: (String) -> Unit,
//    onInsightDetailClick: (BrewingInsight) -> Unit,
//    onFullReportClick: () -> Unit
//) {
//    val scrollState = rememberScrollState()
//
//    Column(
//        modifier = modifier
//            .background(MaterialTheme.colorScheme.background)
//            .verticalScroll(scrollState)
//    ) {
//        if (uiState.isLoading) {
//            LinearProgressIndicator(
//                modifier = Modifier.fillMaxWidth(),
//                color = MaterialTheme.colorScheme.primary
//            )
//        }
//
//        uiState.user?.let { user ->
//            ProfileHeader(user = user)
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        MyPageCalendarSection(
//            uiState = uiState,
//            onDateClick = onDateClick,
//            onPrevMonth = onPrevMonth,
//            onNextMonth = onNextMonth,
//            onAddClick = onAddRecordClick,
//            onEditClick = onEditRecordClick,
//            onDetailClick = onRecordDetailClick,
//            onViewAllClick = onViewAllClick
//        )
//
//        MyPageInsightSection(
//            uiState = uiState,
//            onInsightClick = onInsightDetailClick,
//            onViewFullReportClick = onFullReportClick
//        )
//        //북마크 구현
//
//        //팔로잉 팔로우 섹션 보는 페이지
//
//        //설정 페이지
//
//        Spacer(modifier = Modifier.height(32.dp))
//    }
//}
//
//@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
//@Composable
//private fun MyPageScreenPreview() {
//    val previewDate = LocalDate.of(2025, 12, 21)
//    val dateString = "2025-12-21"
//
//    val mockUser = User(
//        id = "user_1",
//        username = "Felix",
//        profileImageUrl = null,
//        bio = "평온한 오후, 차 한 잔의 여유를 즐깁니다. 🍵",
//        followerCount = 124,
//        followingCount = 89
//    )
//
//    val mockStats = UserStats(
//        totalBrewingCount = 124,
//        currentStreak = 8,
//        monthlyBrewingCount = 18,
//        preferredTimeSlot = "오후 2시 ~ 4시",
//        averageBrewingTime = "3분 20초",
//        weeklyBrewingCount = 5,
//        averageRating = 4.8,
//        myTeaChestCount = 12,
//        wishlistCount = 5
//    )
//
//    val mockRecords = listOf(
//        BrewingRecord(
//            id = "note_1",
//            teaName = "알리산 우롱",
//            metaInfo = "95°C / 5g / 3min",
//            rating = 5,
//            dateString = dateString
//        ),
//        BrewingRecord(
//            id = "note_2",
//            teaName = "알리산 우롱",
//            metaInfo = "95°C / 5g / 3min",
//            rating = 5,
//            dateString = dateString
//        )
//    )
//    val mockInsights = listOf(
//        BrewingInsight(
//            id = "insight_1",
//            type = InsightCategory.TIME_PATTERN,
//            emoji = "🌙",
//            title = "저녁의 루틴",
//            description = "주로 저녁 시간대에 차를 즐기시네요.",
//            action = InsightAction.RecordToday,
//            content = InsightContent.Summary("오후 3시", "가장 평온한 시간"),
//        ),
//        BrewingInsight(
//            id = "insight_2",
//            type = InsightCategory.BREWING_MASTER,
//            emoji = "⏱️",
//            title = "장인의 손길",
//            description = "평균 브루잉 시간은 2분 50초입니다.",
//            action = InsightAction.OpenFullAnalytics,
//            content = InsightContent.ChartData(listOf("우롱차"), listOf(60f), "%"),
//        )
//    )
//
//    LeafyTheme {
//        MyPageContent(
//            uiState = MyPageUiState(
//                user = mockUser,
//                userStats = mockStats,
//                selectedDate = previewDate,
//                recordedDays = listOf(21),
//                monthlyRecords = mockRecords,
//                selectedRecord = mockRecords[0],
//                brewingInsights = mockInsights,
//                isLoading = false
//            ),
//            onDateClick = {},
//            onPrevMonth = {},
//            onNextMonth = {},
//            onAddRecordClick = {},
//            onEditRecordClick = {},
//            onRecordDetailClick = {},
//            onViewAllClick = {},
//            onInsightDetailClick = {},
//            onFullReportClick = {}
//        )
//    }
//}