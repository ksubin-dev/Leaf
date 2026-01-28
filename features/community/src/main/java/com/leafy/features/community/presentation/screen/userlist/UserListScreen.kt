package com.leafy.features.community.presentation.screen.userlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.shared.common.singleClick
import com.leafy.shared.navigation.UserListType
import com.leafy.shared.ui.component.CommunityTeaMasterCard
import com.leafy.shared.ui.model.UserUiModel

@Composable
fun UserListRoute(
    onBackClick: () -> Unit,
    onUserClick: (String) -> Unit,
    viewModel: UserListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is UserListSideEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message.asString(context))
                }
            }
        }
    }

    UserListScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onUserClick = onUserClick,
        onFollowToggle = viewModel::toggleFollow
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    uiState: UserListUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onUserClick: (String) -> Unit,
    onFollowToggle: (UserUiModel) -> Unit
) {
    val title = when (uiState.listType) {
        UserListType.FOLLOWER -> "팔로워"
        UserListType.FOLLOWING -> "팔로잉"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = singleClick { onBackClick() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.users.isEmpty()) {
                Text(
                    text = "아직 ${title}가 없습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.users) { user ->
                        CommunityTeaMasterCard(
                            master = user,
                            currentUserId = uiState.currentUserId,
                            onClick = singleClick { onUserClick(user.userId) },
                            onFollowToggle = { onFollowToggle(user) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}