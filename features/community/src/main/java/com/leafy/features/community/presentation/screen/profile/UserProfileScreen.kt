package com.leafy.features.community.presentation.screen.profile

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.leafy.features.community.presentation.common.model.CommunityPostUiModel
import com.leafy.features.community.presentation.common.model.UserUiModel
import com.leafy.features.community.presentation.screen.profile.section.UserProfileGallery
import com.leafy.features.community.presentation.screen.profile.section.UserProfileHeader
import com.leafy.features.community.presentation.screen.profile.section.UserProfileList
import com.leafy.shared.navigation.UserListType
import com.leafy.shared.ui.theme.LeafyTheme

enum class ProfileTab(val icon: ImageVector) {
    GRID(Icons.Default.GridOn),
    LIST(Icons.AutoMirrored.Filled.List)
}

@Composable
fun UserProfileScreen(
    viewModel: UserProfileViewModel,
    onBackClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onNavigateToUserList: (userId: String, nickname: String, type: UserListType) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

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
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UserProfileScreenPreview() {
    LeafyTheme {
        val dummyUser = UserUiModel(
            userId = "uid",
            nickname = "차 마시는 루시",
            title = "PRO BREWER",
            bio = "따뜻한 우롱차와 무이암차를 사랑합니다. 매일 아침 차 한 잔으로 시작하는 기록들. 🌿",
            profileImageUrl = null,
            isFollowing = false,
            followerCount = "1.2k",
            followingCount = "480",
            postCount = "156",
            expertTags = listOf("무이암차", "봉황단총")
        )

        val dummyPosts = List(5) { index ->
            CommunityPostUiModel(
                postId = "$index",
                authorId = "uid",
                authorName = "루시",
                authorProfileUrl = null,
                isFollowingAuthor = false,
                title = "[무이암차] 오늘의 기록 $index",
                content = "암운이 아주 강렬하게 느껴지는 날이었습니다. 첫 잔부터 압도적이네요.",
                imageUrls = if (index % 2 == 0) listOf("https://dummy") else emptyList(),
                timeAgo = "1시간 전",
                teaType = "OOLONG",
                brewingSummary = "95℃ · 30s · 5g",
                rating = 5,
                likeCount = "10",
                commentCount = "5",
                viewCount = "100",
                bookmarkCount = "2",
                isLiked = false,
                isBookmarked = false
            )
        }

        UserProfileContent(
            uiState = UserProfileUiState(
                isLoading = false,
                userProfile = dummyUser,
                userPosts = dummyPosts,
                isMe = false,
                isFollowing = false
            ),
            onBackClick = {},
            onPostClick = {},
            onFollowClick = {},
            onNavigateToUserList = { _, _, _ -> }
        )
    }
}