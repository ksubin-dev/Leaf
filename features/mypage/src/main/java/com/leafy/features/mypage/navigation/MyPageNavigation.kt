package com.leafy.features.mypage.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.leafy.features.mypage.screen.DailyRecordsScreen
import com.leafy.features.mypage.screen.FollowListScreen
import com.leafy.features.mypage.screen.MyPageScreen
import com.leafy.features.mypage.screen.SavedListScreen
import com.leafy.features.mypage.ui.MyPageViewModel
import com.leafy.features.mypage.ui.factory.MyPageViewModelFactory
import com.leafy.features.mypage.ui.setting.SettingScreen
import com.leafy.features.mypage.ui.setting.SettingViewModel
import com.leafy.features.mypage.ui.factory.SettingViewModelFactory
import com.leafy.features.mypage.ui.tea.MyTeaListScreen
import com.leafy.features.mypage.ui.tea.MyTeaListViewModel
import com.leafy.features.mypage.ui.tea.TeaAddEditScreen
import com.leafy.features.mypage.ui.tea.TeaAddEditViewModel
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.navigation.MainNavigationRoute
import com.leafy.shared.utils.ImageCompressor
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import com.leafy.shared.R as SharedR

fun NavGraphBuilder.mypageNavGraph(
    container: ApplicationContainer,
    navController: NavController
) {
    composable<MainNavigationRoute.MyPageTab> {
        val viewModel: MyPageViewModel = viewModel(
            factory = makeMyPageViewModelFactory(container)
        )

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
            // [연결] 나의 찻장 클릭 시 이동
            onMyTeaCabinetClick = {
                navController.navigate(MainNavigationRoute.MyTeaCabinet)
            }
        )
    }

    composable<MainNavigationRoute.DailyRecords> { backStackEntry ->
        val route: MainNavigationRoute.DailyRecords = backStackEntry.toRoute()
        val targetDate = LocalDate.parse(route.date)

        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(MainNavigationRoute.MyPageTab)
        }

        val viewModel: MyPageViewModel = viewModel(
            viewModelStoreOwner = parentEntry,
            factory = makeMyPageViewModelFactory(container)
        )

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
        val viewModel: MyPageViewModel = viewModel(parentEntry, factory = makeMyPageViewModelFactory(container))
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
        val viewModel: MyPageViewModel = viewModel(parentEntry, factory = makeMyPageViewModelFactory(container))
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
        val viewModel: MyPageViewModel = viewModel(parentEntry, factory = makeMyPageViewModelFactory(container))
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
        val viewModel: MyPageViewModel = viewModel(parentEntry, factory = makeMyPageViewModelFactory(container))
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
        val viewModel: MyTeaListViewModel = viewModel(
            factory = makeMyTeaListViewModelFactory(container)
        )

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

    // [수정] 팩토리 함수 연결
    composable<MainNavigationRoute.TeaAddEdit> {
        val viewModel: TeaAddEditViewModel = viewModel(
            factory = makeTeaAddEditViewModelFactory(container)
        )

        TeaAddEditScreen(
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() }
        )
    }


    composable<MainNavigationRoute.Settings> {
        val factory = SettingViewModelFactory(
            settingUseCases = container.settingUseCases,
            authUseCases = container.authUseCases,
            userUseCases = container.userUseCases,
            timerUseCases = container.timerUseCases
        )
        val viewModel: SettingViewModel = viewModel(factory = factory)

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


@Composable
private fun makeMyPageViewModelFactory(container: ApplicationContainer): MyPageViewModelFactory {
    val context = LocalContext.current
    return MyPageViewModelFactory(
        userUseCases = container.userUseCases,
        noteUseCases = container.noteUseCases,
        postUseCases = container.postUseCases,
        analysisUseCases = container.analysisUseCases,
        imageUseCases = container.imageUseCases,
        teaUseCases = container.teaUseCases,
        imageCompressor = ImageCompressor(context)
    )
}

@Composable
private fun makeMyTeaListViewModelFactory(container: ApplicationContainer): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MyTeaListViewModel(
                teaUseCases = container.teaUseCases
            ) as T
        }
    }
}

@Composable
private fun makeTeaAddEditViewModelFactory(container: ApplicationContainer): ViewModelProvider.Factory {
    val context = LocalContext.current
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val savedStateHandle = extras.createSavedStateHandle()
            return TeaAddEditViewModel(
                savedStateHandle = savedStateHandle,
                teaUseCases = container.teaUseCases,
                imageUseCases = container.imageUseCases,
                imageCompressor = ImageCompressor(context)
            ) as T
        }
    }
}