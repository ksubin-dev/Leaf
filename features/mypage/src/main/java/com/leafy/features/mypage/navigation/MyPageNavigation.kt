package com.leafy.features.mypage.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leafy.features.mypage.screen.MyPageScreen
import com.leafy.features.mypage.ui.MyPageViewModel
import com.leafy.features.mypage.ui.factory.MyPageViewModelFactory
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.navigation.MainNavigationRoute

fun NavGraphBuilder.mypageNavGraph(
    container: ApplicationContainer,
    navController: NavController
) {
    composable<MainNavigationRoute.MyPageTab> {
        val factory = MyPageViewModelFactory(container)
        val viewModel: MyPageViewModel = viewModel(factory = factory)

        MyPageScreen(
            viewModel = viewModel,
            onSettingsClick = {
                // navController.navigate(MyPageRoute.Settings)
            },
            onAddRecordClick = {
                // 기록 추가(노트 작성) 탭으로 이동
                navController.navigate(MainNavigationRoute.NoteTab())
            },
            onEditRecordClick = { noteId ->
                // 선택한 기록 상세 페이지로 이동 (NoteId 전달)
                // navController.navigate(MainNavigationRoute.NoteDetail(noteId))
            }
        )
    }
}