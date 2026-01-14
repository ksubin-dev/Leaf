package com.leafy.features.community.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.ExploreContent


@Composable
fun CustomExploreTabRow(
    selectedTab: ExploreContent,
    onTabSelected: (ExploreContent) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(colors.background)
        ) {
            ExploreContent.entries.forEach { tab ->
                val selected = tab == selectedTab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onTabSelected(tab) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        color = if (selected) colors.primary else colors.secondary,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    val indicatorWidth by animateDpAsState(
                        targetValue = if (selected) 40.dp else 0.dp,
                        label = "IndicatorWidthAnimation"
                    )

                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(indicatorWidth)
                            .background(colors.primary)
                    )
                }
            }
        }
        HorizontalDivider(
            color = colors.onSurfaceVariant.copy(alpha = 1f),
            thickness = 1.dp
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun CustomExploreTabRowPreview() {
    LeafyTheme {
        var currentTab by remember { mutableStateOf(ExploreContent.TRENDING) }

        CustomExploreTabRow(
            selectedTab = currentTab,
            onTabSelected = { newTab ->
                currentTab = newTab
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomExploreTabRowFollowingSelectedPreview() {
    LeafyTheme {
        CustomExploreTabRow(
            selectedTab = ExploreContent.FOLLOWING,
            onTabSelected = { }
        )
    }
}