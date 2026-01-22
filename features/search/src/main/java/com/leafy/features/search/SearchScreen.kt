package com.leafy.features.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.features.search.components.LeafySearchInput
import com.leafy.shared.common.singleClick


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onBackClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onUserClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    LeafySearchInput(
                        query = uiState.query,
                        onQueryChange = viewModel::onQueryChange,
                        onSearchAction = {
                            viewModel.performSearch()
                            keyboardController?.hide()
                        },
                        focusRequester = focusRequester
                    )
                },
                navigationIcon = {
                    IconButton(onClick = singleClick { onBackClick() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            SearchTabRow(
                selectedTab = uiState.selectedTab,
                onTabSelected = viewModel::onTabSelected
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    SearchResultSection(
                        uiState = uiState,
                        onPostClick = onPostClick,
                        onUserClick = onUserClick,
                        onLoadMore = viewModel::loadMore
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTabRow(
    selectedTab: SearchTab,
    onTabSelected: (SearchTab) -> Unit
) {
    SecondaryTabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedTab.ordinal),
                color = MaterialTheme.colorScheme.primary
            )
        },
        divider = {}
    ) {
        SearchTab.entries.forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = singleClick { onTabSelected(tab) },
                text = {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium
                    )
                }
            )
        }
    }
}
