package com.leafy.features.community.presentation.screen.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.features.community.presentation.screen.profile.section.UserProfileGallery
import com.leafy.features.community.presentation.screen.profile.section.UserProfileHeader
import com.leafy.features.community.presentation.screen.profile.section.UserProfileList
import com.leafy.shared.navigation.UserListType

enum class ProfileTab(val icon: ImageVector) {
    GRID(Icons.Default.GridOn),
    LIST(Icons.AutoMirrored.Filled.List)
}

@Composable
fun UserProfileScreen(
    onBackClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onNavigateToUserList: (userId: String, nickname: String, type: UserListType) -> Unit,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is UserProfileSideEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        effect.message.asString(context),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    UserProfileContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onPostClick = onPostClick,
        onFollowClick = viewModel::toggleFollow,
        onNavigateToUserList = onNavigateToUserList
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileContent(
    uiState: UserProfileUiState,
    onBackClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onFollowClick: () -> Unit,
    onNavigateToUserList: (userId: String, nickname: String, type: UserListType) -> Unit
) {
    val user = uiState.userProfile
    var selectedTab by remember { mutableStateOf(ProfileTab.GRID) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = user?.nickname ?: "",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (user != null) {
            Column(modifier = Modifier.padding(padding)) {

                UserProfileHeader(
                    user = user,
                    isMe = uiState.isMe,
                    isFollowing = uiState.isFollowing,
                    postCount = uiState.userPosts.size,
                    onFollowClick = onFollowClick,
                    onFollowerClick = {
                        onNavigateToUserList(user.userId, user.nickname, UserListType.FOLLOWER)
                    },
                    onFollowingClick = {
                        onNavigateToUserList(user.userId, user.nickname, UserListType.FOLLOWING)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PrimaryTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {},
                    indicator = {
                        TabRowDefaults.PrimaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(selectedTab.ordinal),
                            color = MaterialTheme.colorScheme.primary,
                            width = Dp.Unspecified,
                            shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                        )
                    }
                ) {
                    ProfileTab.entries.forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = { Icon(imageVector = tab.icon, contentDescription = null) },
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        ProfileTab.GRID -> {
                            UserProfileGallery(
                                posts = uiState.userPosts,
                                onPostClick = onPostClick
                            )
                        }
                        ProfileTab.LIST -> {
                            UserProfileList(
                                posts = uiState.userPosts,
                                onPostClick = onPostClick
                            )
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = uiState.errorMessage?.asString() ?: "유저 정보를 불러올 수 없습니다.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
