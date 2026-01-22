package com.leafy.features.mypage.presentation.social

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.CommunityTeaMasterCard
import com.leafy.shared.ui.model.UserUiModel
import com.leafy.shared.R as SharedR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowListScreen(
    title: String,
    users: List<UserUiModel>,
    currentUserId: String?,
    onBackClick: () -> Unit,
    onUserClick: (String) -> Unit,
    onFollowToggle: (String) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = singleClick { onBackClick() }) {
                        Icon(
                            painter = painterResource(id = SharedR.drawable.ic_back),
                            contentDescription = "뒤로가기",
                            modifier = Modifier.height(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
        ) {
            items(
                items = users,
                key = { it.userId }
            ) { user ->
                CommunityTeaMasterCard(
                    master = user,
                    currentUserId = currentUserId,
                    onClick = singleClick { onUserClick(user.userId) },
                    onFollowToggle = singleClick { onFollowToggle(user.userId) }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}