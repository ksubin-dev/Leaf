package com.leafy.features.mypage.navigation
//
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import androidx.navigation.NavGraphBuilder
//import androidx.navigation.compose.composable
//import androidx.navigation.toRoute
//import com.leafy.features.mypage.screen.AnalysisReportScreen
//import com.leafy.features.mypage.screen.DailyRecordsScreen
//import com.leafy.features.mypage.screen.MyPageScreen
//import com.leafy.features.mypage.ui.MyPageViewModel
//import com.leafy.features.mypage.ui.factory.MyPageViewModelFactory
//import com.leafy.shared.di.ApplicationContainer
//import com.leafy.shared.navigation.MainNavigationRoute
//
//
//fun NavGraphBuilder.mypageNavGraph(
//    container: ApplicationContainer,
//    navController: NavController
//) {
//    composable<MainNavigationRoute.MyPageTab> { backStackEntry ->
//        val factory = MyPageViewModelFactory(container)
//        val viewModel: MyPageViewModel = viewModel(factory = factory)
//
//        MyPageScreen(
//            viewModel = viewModel,
//            onSettingsClick = { /* 설정 이동 로직 필요 시 추가 */ },
//            onAddRecordClick = { date ->
//                navController.navigate(MainNavigationRoute.NoteTab(date = date))
//            },
//            onRecordDetailClick = { noteId ->
//                navController.navigate(MainNavigationRoute.NoteDetail(noteId = noteId))
//            },
//            onEditRecordClick = { noteId ->
//                navController.navigate(MainNavigationRoute.NoteTab(noteId = noteId))
//            },
//            onViewAllRecordsClick = { date ->
//                navController.navigate(MainNavigationRoute.DailyRecords(date = date))
//            },
//            onInsightClick = { insight ->
//                navController.navigate(MainNavigationRoute.AnalysisReport)
//            },
//            onFullReportClick = {
//                navController.navigate(MainNavigationRoute.AnalysisReport)
//            }
//        )
//    }
//
//    composable<MainNavigationRoute.DailyRecords> { backStackEntry ->
//        val route: MainNavigationRoute.DailyRecords = backStackEntry.toRoute()
//        val date = route.date
//
//        val parentEntry = remember(backStackEntry) {
//            navController.getBackStackEntry(MainNavigationRoute.MyPageTab)
//        }
//        val viewModel: MyPageViewModel = viewModel(parentEntry, factory = MyPageViewModelFactory(container))
//        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//        DailyRecordsScreen(
//            date = date,
//            records = uiState.monthlyRecords.filter { it.dateString == date },
//            onBackClick = { navController.popBackStack() },
//            onRecordClick = { noteId ->
//                navController.navigate(MainNavigationRoute.NoteDetail(noteId = noteId))
//            },
//            onEditClick = { noteId ->
//                navController.navigate(MainNavigationRoute.NoteTab(noteId = noteId))
//            }
//        )
//    }
//    composable<MainNavigationRoute.AnalysisReport> { backStackEntry ->
//        val parentEntry = remember(backStackEntry) {
//            navController.getBackStackEntry(MainNavigationRoute.MyPageTab)
//        }
//        val viewModel: MyPageViewModel = viewModel(parentEntry, factory = MyPageViewModelFactory(container))
//        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//        // 내일 만들 상세 리포트 스크린 (가칭)
//        AnalysisReportScreen(
//            uiState = uiState,
//            onBackClick = { navController.popBackStack() }
//        )
//    }
//}