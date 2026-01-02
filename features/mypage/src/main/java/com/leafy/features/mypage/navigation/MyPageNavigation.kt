package com.leafy.features.mypage.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leafy.features.mypage.screen.MyPageScreen
import com.leafy.features.mypage.ui.MyPageViewModel
import com.leafy.features.mypage.ui.factory.MyPageViewModelFactory
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.navigation.MainNavigationRoute

@RequiresApi(Build.VERSION_CODES.O)
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
                // 설정 화면이 있다면 이동 (현재는 정의 안 됨)
            },
            onAddRecordClick = {
                navController.navigate(MainNavigationRoute.NoteTab()) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            onRecordDetailClick = { noteId ->
                navController.navigate(MainNavigationRoute.NoteDetail(noteId = noteId))
            },
            onEditRecordClick = { noteId ->
                navController.navigate(MainNavigationRoute.NoteTab(noteId = noteId))
            }
        )
    }
}