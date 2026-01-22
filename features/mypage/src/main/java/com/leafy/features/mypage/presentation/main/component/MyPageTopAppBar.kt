package com.leafy.features.mypage.presentation.main.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.leafy.shared.common.singleClick
import com.leafy.shared.R as SharedR


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageTopAppBar(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = "My Page",
                style = MaterialTheme.typography.titleLarge,
                color = colors.onSurface
            )
        },
        actions = {
            IconButton(onClick = singleClick { onSettingsClick() }) {
                Icon(
                    painter = painterResource(id = SharedR.drawable.ic_settings),
                    contentDescription = "Settings",
                    tint = colors.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colors.background,
            titleContentColor = colors.onBackground
        )
    )
}
