package com.leafy.features.mypage.presentation.main

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.features.mypage.presentation.main.MyPageUiState
import com.leafy.features.mypage.presentation.main.MyPageViewModel
import com.leafy.features.mypage.presentation.main.component.AnalysisTeaserCard
import com.leafy.features.mypage.presentation.main.component.MyPageTopAppBar
import com.leafy.features.mypage.presentation.main.component.ProfileHeader
import com.leafy.features.mypage.presentation.main.session.MyPageCalendarSection
import com.leafy.features.mypage.presentation.main.session.MyTeaCabinetSection
import com.leafy.features.mypage.presentation.main.session.SavedNotesSummarySection
import java.time.LocalDate

@Composable
fun MyPageScreen(
    viewModel: MyPageViewModel,
    onSettingsClick: () -> Unit,
    onAddRecordClick: (LocalDate) -> Unit,
    onEditRecordClick: (String) -> Unit,
    onRecordDetailClick: (String) -> Unit,
    onViewAllRecordsClick: (LocalDate) -> Unit,
    onPostDetailClick: (String) -> Unit,
    onViewAllBookmarksClick: () -> Unit,
    onViewAllLikedPostsClick: () -> Unit,
    onFollowerClick: () -> Unit,
    onFollowingClick: () -> Unit,
    onMyTeaCabinetClick: () -> Unit,
    onAnalysisClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(uiState.profileEditMessage) {
        uiState.profileEditMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onProfileMessageShown()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MyPageTopAppBar(
                onSettingsClick = onSettingsClick
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        MyPageContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            uiState = uiState,
            onDateClick = viewModel::onDateSelected,
            onPrevMonth = {
                val current = LocalDate.of(uiState.currentYear, uiState.currentMonth, 1)
                viewModel.onMonthChanged(current.minusMonths(1))
            },
            onNextMonth = {
                val current = LocalDate.of(uiState.currentYear, uiState.currentMonth, 1)
                viewModel.onMonthChanged(current.plusMonths(1))
            },
            onAddRecordClick = { onAddRecordClick(uiState.selectedDate) },
            onEditRecordClick = onEditRecordClick,
            onRecordDetailClick = onRecordDetailClick,
            onViewAllClick = onViewAllRecordsClick,
            onPostDetailClick = onPostDetailClick,
            onViewAllBookmarksClick = onViewAllBookmarksClick,
            onViewAllLikedPostsClick = onViewAllLikedPostsClick,
            onFollowerClick = onFollowerClick,
            onFollowingClick = onFollowingClick,
            onProfileEditClick = {
                if (uiState.isEditingProfile) {
                    viewModel.saveProfile()
                } else {
                    viewModel.toggleEditProfileMode()
                }
            },
            onProfileCancelClick = viewModel::toggleEditProfileMode,
            onNicknameChange = viewModel::onNicknameChange,
            onBioChange = viewModel::onBioChange,
            onProfileImageSelected = viewModel::onProfileImageSelected,
            onMyTeaCabinetClick = onMyTeaCabinetClick,
            onAnalysisClick = onAnalysisClick
        )
    }
}

@Composable
private fun MyPageContent(
    modifier: Modifier = Modifier,
    uiState: MyPageUiState,
    onDateClick: (LocalDate) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddRecordClick: () -> Unit,
    onEditRecordClick: (String) -> Unit,
    onRecordDetailClick: (String) -> Unit,
    onViewAllClick: (LocalDate) -> Unit,
    onPostDetailClick: (String) -> Unit,
    onViewAllBookmarksClick: () -> Unit,
    onViewAllLikedPostsClick: () -> Unit,
    onFollowerClick: () -> Unit,
    onFollowingClick: () -> Unit,
    onProfileEditClick: () -> Unit,
    onProfileCancelClick: () -> Unit,
    onNicknameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onProfileImageSelected: (Uri) -> Unit,
    onMyTeaCabinetClick: () -> Unit,
    onAnalysisClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        if (uiState.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        }

        uiState.myProfile?.let { user ->
            ProfileHeader(
                user = user,
                isEditing = uiState.isEditingProfile,
                editNickname = uiState.editNickname,
                editBio = uiState.editBio,
                isNicknameValid = uiState.isNicknameValid,
                editProfileImageUri = uiState.editProfileImageUri,
                onImageSelected = onProfileImageSelected,
                onEditClick = onProfileEditClick,
                onCancelClick = onProfileCancelClick,
                onNicknameChange = onNicknameChange,
                onBioChange = onBioChange,
                onFollowerClick = onFollowerClick,
                onFollowingClick = onFollowingClick,

            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        MyPageCalendarSection(
            uiState = uiState,
            onDateClick = onDateClick,
            onPrevMonth = onPrevMonth,
            onNextMonth = onNextMonth,
            onAddClick = onAddRecordClick,
            onEditClick = onEditRecordClick,
            onDetailClick = onRecordDetailClick,
            onViewAllClick = onViewAllClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        MyTeaCabinetSection(
            teaCount = uiState.myTeaCabinetCount,
            onClick = onMyTeaCabinetClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.analysisTeaserContent != null && uiState.analysisTeaserIconRes != null) {
            AnalysisTeaserCard(
                content = uiState.analysisTeaserContent,
                iconResId = uiState.analysisTeaserIconRes,
                onClick = onAnalysisClick,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        SavedNotesSummarySection(
            title = "저장한 노트",
            bookmarkedPosts = uiState.bookmarkedPosts,
            onViewAllClick = onViewAllBookmarksClick,
            onPostClick = onPostDetailClick
        )

        if (uiState.likedPosts.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            SavedNotesSummarySection(
                title = "좋아요한 글",
                bookmarkedPosts = uiState.likedPosts,
                onViewAllClick = onViewAllLikedPostsClick,
                onPostClick = onPostDetailClick
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}