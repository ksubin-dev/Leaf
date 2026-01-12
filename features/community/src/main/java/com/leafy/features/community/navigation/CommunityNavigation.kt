package com.leafy.features.community.navigation
//
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import androidx.navigation.NavGraphBuilder
//import androidx.navigation.compose.composable
//import com.leafy.features.community.screen.CommunityScreen
//import com.leafy.features.community.ui.CommunityViewModel
//import com.leafy.features.community.ui.CommunityViewModelFactory
//import com.leafy.shared.di.ApplicationContainer
//import com.leafy.shared.navigation.MainNavigationRoute
//
//fun NavGraphBuilder.communityNavGraph(
//    navController: NavController,
//    container: ApplicationContainer
//) {
//    composable<MainNavigationRoute.CommunityTab> {
//        val viewModel: CommunityViewModel = viewModel(
//            factory = CommunityViewModelFactory(container.communityUseCases)
//        )
//
//        CommunityScreen(
//            viewModel = viewModel,
//            onNoteClick = { noteId ->
//                navController.navigate(MainNavigationRoute.NoteDetail(noteId = noteId))
//            },
//            onMasterClick = { masterId ->
//                // TODO: 마스터 프로필 화면 루트가 정의되면 연결하세요.
//                // 예: navController.navigate(MainNavigationRoute.MasterProfile(masterId))
//                println("마스터 클릭됨: $masterId")
//            }
//        )
//    }
//}