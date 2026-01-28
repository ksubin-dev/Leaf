package com.leafy.features.mypage.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.leafy.features.mypage.presentation.analysis.AnalysisReportScreen
import com.leafy.features.mypage.presentation.analysis.AnalysisViewModel
import com.leafy.features.mypage.presentation.bookmark.SavedListScreen
import com.leafy.features.mypage.presentation.main.MyPageScreen
import com.leafy.features.mypage.presentation.main.MyPageViewModel
import com.leafy.features.mypage.presentation.record.DailyRecordsScreen
import com.leafy.features.mypage.presentation.setting.SettingScreen
import com.leafy.features.mypage.presentation.setting.SettingViewModel
import com.leafy.features.mypage.presentation.social.FollowListScreen
import com.leafy.features.mypage.presentation.tea.edit.TeaAddEditScreen
import com.leafy.features.mypage.presentation.tea.edit.TeaAddEditViewModel
import com.leafy.features.mypage.presentation.tea.list.MyTeaListScreen
import com.leafy.features.mypage.presentation.tea.list.MyTeaListViewModel
import com.leafy.shared.navigation.MainNavigationRoute
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import com.leafy.shared.R as SharedR

fun NavGraphBuilder.mypageNavGraph(
    navController: NavController
) {
    composable<MainNavigationRoute.MyPageTab> {
        val viewModel: MyPageViewModel = hiltViewModel()

        MyPageScreen(
            viewModel = viewModel,
            onSettingsClick = {
                navController.navigate(MainNavigationRoute.Settings)
            },
            onAddRecordClick = { date ->
                navController.navigate(MainNavigationRoute.NoteTab(date = date.toString()))
            },
            onRecordDetailClick = { noteId ->
                navController.navigate(MainNavigationRoute.NoteDetail(noteId = noteId))
            },
            onEditRecordClick = { noteId ->
                navController.navigate(MainNavigationRoute.NoteTab(noteId = noteId))
            },
            onViewAllRecordsClick = { date ->
                navController.navigate(MainNavigationRoute.DailyRecords(date = date.toString()))
            },
            onPostDetailClick = { postId ->
                navController.navigate(MainNavigationRoute.CommunityDetail(postId))
            },
            onViewAllBookmarksClick = {
                navController.navigate(MainNavigationRoute.BookmarkedPosts)
            },
            onViewAllLikedPostsClick = {
                navController.navigate(MainNavigationRoute.LikedPosts)
            },
            onFollowerClick = {
                navController.navigate(MainNavigationRoute.FollowerList)
            },
            onFollowingClick = {
                navController.navigate(MainNavigationRoute.FollowingList)
            },
            onMyTeaCabinetClick = {
                navController.navigate(MainNavigationRoute.MyTeaCabinet)
            },
            onAnalysisClick = {
                navController.navigate(MainNavigationRoute.AnalysisReport)
            }
        )
    }

    composable<MainNavigationRoute.AnalysisReport> {
        val viewModel: AnalysisViewModel = hiltViewModel()

        AnalysisReportScreen(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() }
        )
    }

    composable<MainNavigationRoute.DailyRecords> { backStackEntry ->
        val route: MainNavigationRoute.DailyRecords = backStackEntry.toRoute()
        val targetDate = LocalDate.parse(route.date)

        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(MainNavigationRoute.MyPageTab)
        }
        val viewModel: MyPageViewModel = hiltViewModel(parentEntry)
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        val dailyRecords = uiState.calendarNotes.filter { note ->
            val noteDate = Instant.ofEpochMilli(note.date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            noteDate == targetDate
        }

        DailyRecordsScreen(
            date = targetDate,
            records = dailyRecords,
            onBackClick = { navController.popBackStack() },
            onRecordClick = { noteId ->
                navController.navigate(MainNavigationRoute.NoteDetail(noteId = noteId))
            },
            onEditClick = { noteId ->
                navController.navigate(MainNavigationRoute.NoteTab(noteId = noteId))
            }
        )
    }

    composable<MainNavigationRoute.BookmarkedPosts> { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(MainNavigationRoute.MyPageTab)
        }
        val viewModel: MyPageViewModel = hiltViewModel(parentEntry)
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        SavedListScreen(
            title = "저장한 노트",
            posts = uiState.bookmarkedPosts,
            iconResId = SharedR.drawable.ic_bookmark_filled,
            onBackClick = { navController.popBackStack() },
            onPostClick = { postId ->
                navController.navigate(MainNavigationRoute.CommunityDetail(postId))
            }
        )
    }

    composable<MainNavigationRoute.LikedPosts> { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(MainNavigationRoute.MyPageTab)
        }
        val viewModel: MyPageViewModel = hiltViewModel(parentEntry)
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        SavedListScreen(
            title = "좋아요한 글",
            posts = uiState.likedPosts,
            iconResId = SharedR.drawable.ic_like_filled,
            onBackClick = { navController.popBackStack() },
            onPostClick = { postId ->
                navController.navigate(MainNavigationRoute.CommunityDetail(postId))
            }
        )
    }

    composable<MainNavigationRoute.FollowerList> { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(MainNavigationRoute.MyPageTab)
        }
        val viewModel: MyPageViewModel = hiltViewModel(parentEntry)
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            viewModel.loadFollowLists()
        }

        FollowListScreen(
            title = "팔로워",
            users = uiState.followerList,
            currentUserId = uiState.myProfile?.id,
            onBackClick = { navController.popBackStack() },
            onUserClick = { userId ->
                navController.navigate(MainNavigationRoute.UserProfile(userId))
            },
            onFollowToggle = { userId ->
                viewModel.toggleFollow(userId)
            }
        )
    }

    composable<MainNavigationRoute.FollowingList> { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(MainNavigationRoute.MyPageTab)
        }
        val viewModel: MyPageViewModel = hiltViewModel(parentEntry)
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            viewModel.loadFollowLists()
        }

        FollowListScreen(
            title = "팔로잉",
            users = uiState.followingList,
            currentUserId = uiState.myProfile?.id,
            onBackClick = { navController.popBackStack() },
            onUserClick = { userId ->
                navController.navigate(MainNavigationRoute.UserProfile(userId))
            },
            onFollowToggle = { userId ->
                viewModel.toggleFollow(userId)
            }
        )
    }

    composable<MainNavigationRoute.MyTeaCabinet> {
        val viewModel: MyTeaListViewModel = hiltViewModel()

        MyTeaListScreen(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() },
            onAddTeaClick = {
                navController.navigate(MainNavigationRoute.TeaAddEdit(teaId = null))
            },
            onTeaClick = { teaId ->
                navController.navigate(MainNavigationRoute.TeaAddEdit(teaId = teaId))
            }
        )
    }

    composable<MainNavigationRoute.TeaAddEdit> {
        val viewModel: TeaAddEditViewModel = hiltViewModel()

        TeaAddEditScreen(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() }
        )
    }

    composable<MainNavigationRoute.Settings> {
        val viewModel: SettingViewModel = hiltViewModel()

        SettingScreen(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() },
            onLogoutSuccess = {
                navController.navigate(MainNavigationRoute.Auth) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }
}
