package com.leafy.features.community.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.leafy.features.community.presentation.screen.feed.CommunityScreen
import com.leafy.features.community.presentation.screen.detail.CommunityPostDetailRoute
import com.leafy.features.community.presentation.screen.detail.CommunityPostDetailViewModel
import com.leafy.features.community.presentation.screen.detail.CommunityPostDetailViewModelFactory
import com.leafy.features.community.presentation.screen.halloffame.HallOfFameScreen
import com.leafy.features.community.presentation.screen.halloffame.HallOfFameViewModel
import com.leafy.features.community.presentation.screen.halloffame.HallOfFameViewModelFactory
import com.leafy.features.community.presentation.screen.popular.PopularPostListScreen
import com.leafy.features.community.presentation.screen.popular.PopularPostListViewModel
import com.leafy.features.community.presentation.screen.popular.PopularPostListViewModelFactory
import com.leafy.features.community.presentation.screen.profile.UserProfileScreen
import com.leafy.features.community.presentation.screen.profile.UserProfileViewModel
import com.leafy.features.community.presentation.screen.profile.UserProfileViewModelFactory
import com.leafy.features.community.presentation.screen.teamaster.TeaMasterListScreen
import com.leafy.features.community.presentation.screen.teamaster.TeaMasterListViewModel
import com.leafy.features.community.presentation.screen.teamaster.TeaMasterListViewModelFactory
import com.leafy.features.community.presentation.screen.userlist.UserListScreen
import com.leafy.features.community.presentation.screen.write.CommunityWriteRoute
import com.leafy.features.community.presentation.screen.write.CommunityWriteViewModel
import com.leafy.features.community.presentation.screen.write.CommunityWriteViewModelFactory
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.navigation.MainNavigationRoute

fun NavGraphBuilder.communityNavGraph(
    navController: NavController,
    container: ApplicationContainer
) {
    composable<MainNavigationRoute.CommunityTab> {
        CommunityScreen(
            postUseCases = container.postUseCases,
            userUseCases = container.userUseCases,
            onPostClick = { postId ->
                navController.navigate(MainNavigationRoute.CommunityDetail(postId))
            },
            onMasterClick = { userId ->
                navController.navigate(MainNavigationRoute.UserProfile(userId))
            },
            onMoreMasterClick = {
                navController.navigate(MainNavigationRoute.TeaMasterList)
            },
            onMorePopularClick = {
                navController.navigate(MainNavigationRoute.PopularPostList)
            },
            onMoreBookmarkClick = {
                navController.navigate(MainNavigationRoute.HallOfFameList)
            }
        )
    }

    composable<MainNavigationRoute.CommunityWrite> {
        val viewModel: CommunityWriteViewModel = viewModel(
            factory = CommunityWriteViewModelFactory(
                postUseCases = container.postUseCases,
                noteUseCases = container.noteUseCases,
                userUseCases = container.userUseCases,
                imageUseCases = container.imageUseCases,
                imageCompressor = container.imageCompressor
            )
        )

        CommunityWriteRoute(
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onPostSuccess = {
                navController.popBackStack()
            }
        )
    }

    composable<MainNavigationRoute.CommunityDetail> {
        val factory = CommunityPostDetailViewModelFactory(
            postUseCases = container.postUseCases,
            userUseCases = container.userUseCases
        )
        val viewModel: CommunityPostDetailViewModel = viewModel(factory = factory)

        CommunityPostDetailRoute(
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToNoteDetail = { originNoteId ->
                navController.navigate(MainNavigationRoute.NoteDetail(originNoteId))
            },
            onNavigateToUserProfile = { userId ->
                navController.navigate(MainNavigationRoute.UserProfile(userId))
            }
        )
    }

    composable<MainNavigationRoute.UserProfile> {
        val viewModel: UserProfileViewModel = viewModel(
            factory = UserProfileViewModelFactory(
                userUseCases = container.userUseCases,
                postUseCases = container.postUseCases
            )
        )

        UserProfileScreen(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() },
            onPostClick = { postId ->
                navController.navigate(MainNavigationRoute.CommunityDetail(postId))
            },
            onNavigateToUserList = { userId, nickname, type ->
                navController.navigate(
                    MainNavigationRoute.UserList(
                        userId = userId,
                        nickname = nickname,
                        type = type
                    )
                )
            }
        )
    }

    composable<MainNavigationRoute.TeaMasterList> {
        val viewModel: TeaMasterListViewModel = viewModel(
            factory = TeaMasterListViewModelFactory.provide(
                postUseCases = container.postUseCases,
                userUseCases = container.userUseCases
            )
        )

        TeaMasterListScreen(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() },
            onMasterClick = { userId ->
                navController.navigate(MainNavigationRoute.UserProfile(userId))
            }
        )
    }

    composable<MainNavigationRoute.PopularPostList> {
        val viewModel: PopularPostListViewModel = viewModel(
            factory = PopularPostListViewModelFactory.provide(
                postUseCases = container.postUseCases
            )
        )

        PopularPostListScreen(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() },
            onPostClick = { postId ->
                navController.navigate(MainNavigationRoute.CommunityDetail(postId))
            }
        )
    }

    composable<MainNavigationRoute.HallOfFameList> {
        val viewModel: HallOfFameViewModel = viewModel(
            factory = HallOfFameViewModelFactory.provide(
                postUseCases = container.postUseCases
            )
        )
        HallOfFameScreen(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() },
            onPostClick = { postId ->
                navController.navigate(MainNavigationRoute.CommunityDetail(postId))
            }
        )
    }

    composable<MainNavigationRoute.UserList> { backStackEntry ->
        val route = backStackEntry.toRoute<MainNavigationRoute.UserList>()

        UserListScreen(
            navController = navController,
            container = container,
            userId = route.userId,
            type = route.type
        )
    }
}