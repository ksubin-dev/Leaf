package com.leafy.features.community.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.leafy.features.community.presentation.screen.detail.CommunityPostDetailRoute
import com.leafy.features.community.presentation.screen.detail.CommunityPostDetailViewModel
import com.leafy.features.community.presentation.screen.feed.CommunityScreen
import com.leafy.features.community.presentation.screen.halloffame.HallOfFameScreen
import com.leafy.features.community.presentation.screen.halloffame.HallOfFameViewModel
import com.leafy.features.community.presentation.screen.popular.PopularPostListScreen
import com.leafy.features.community.presentation.screen.popular.PopularPostListViewModel
import com.leafy.features.community.presentation.screen.profile.UserProfileScreen
import com.leafy.features.community.presentation.screen.profile.UserProfileViewModel
import com.leafy.features.community.presentation.screen.teamaster.TeaMasterListScreen
import com.leafy.features.community.presentation.screen.teamaster.TeaMasterListViewModel
import com.leafy.features.community.presentation.screen.userlist.UserListRoute
import com.leafy.features.community.presentation.screen.userlist.UserListViewModel
import com.leafy.features.community.presentation.screen.write.CommunityWriteRoute
import com.leafy.features.community.presentation.screen.write.CommunityWriteViewModel
import com.leafy.shared.navigation.MainNavigationRoute

fun NavGraphBuilder.communityNavGraph(
    navController: NavController
) {
    composable<MainNavigationRoute.CommunityTab> {
        CommunityScreen(
            onPostClick = { postId ->
                navController.navigate(MainNavigationRoute.CommunityDetail(postId))
            },
            onCommentClick = { postId ->
                navController.navigate(
                    MainNavigationRoute.CommunityDetail(postId = postId, autoFocus = true)
                )
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
        val viewModel: CommunityWriteViewModel = hiltViewModel()

        CommunityWriteRoute(
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onPostSuccess = {
                navController.popBackStack()
            }
        )
    }

    composable<MainNavigationRoute.CommunityDetail> { backStackEntry ->
        val route: MainNavigationRoute.CommunityDetail = backStackEntry.toRoute()
        val viewModel: CommunityPostDetailViewModel = hiltViewModel()

        CommunityPostDetailRoute(
            autoFocus = route.autoFocus,
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
        val viewModel: UserProfileViewModel = hiltViewModel()

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
        val viewModel: TeaMasterListViewModel = hiltViewModel()

        TeaMasterListScreen(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() },
            onMasterClick = { userId ->
                navController.navigate(MainNavigationRoute.UserProfile(userId))
            }
        )
    }

    composable<MainNavigationRoute.PopularPostList> {
        val viewModel: PopularPostListViewModel = hiltViewModel()

        PopularPostListScreen(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() },
            onPostClick = { postId ->
                navController.navigate(MainNavigationRoute.CommunityDetail(postId))
            }
        )
    }

    composable<MainNavigationRoute.HallOfFameList> {
        val viewModel: HallOfFameViewModel = hiltViewModel()

        HallOfFameScreen(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() },
            onPostClick = { postId ->
                navController.navigate(MainNavigationRoute.CommunityDetail(postId))
            }
        )
    }

    composable<MainNavigationRoute.UserList> {
        val viewModel: UserListViewModel = hiltViewModel()

        UserListRoute(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() },
            onUserClick = { userId ->
                navController.navigate(MainNavigationRoute.UserProfile(userId))
            }
        )
    }
}