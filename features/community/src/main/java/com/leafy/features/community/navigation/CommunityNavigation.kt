package com.leafy.features.community.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leafy.features.community.screen.CommunityScreen
import com.leafy.features.community.ui.CommunityViewModel
import com.leafy.features.community.ui.CommunityViewModelFactory
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.navigation.MainNavigationRoute

fun NavGraphBuilder.communityNavGraph(
    navController: NavController,
    container: ApplicationContainer
){
    composable<MainNavigationRoute.CommunityTab>{

        val viewModel: CommunityViewModel = viewModel(
            factory = CommunityViewModelFactory(container.communityUseCases)
        )

        CommunityScreen(
            viewModel = viewModel,
            onNoteClick = { noteTitle ->
                // 상세 화면으로 이동 로직 (예: navController.navigate(...))
            },
            onMasterClick = { masterName ->
                // 마스터 프로필 화면으로 이동 로직
            }
        )
    }
}