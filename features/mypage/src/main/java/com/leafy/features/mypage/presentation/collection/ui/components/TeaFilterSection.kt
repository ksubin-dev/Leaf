package com.leafy.features.mypage.presentation.collection.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leafy.features.mypage.presentation.collection.data.TeaFilterType

@Composable
fun TeaFilterSection(
    modifier: Modifier = Modifier,
    filters: List<TeaFilterType>,
    onFilterSelected: (String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filterItem ->
            TeaTypeFilterChip(
                type = filterItem.type,
                isSelected = filterItem.isSelected,
                onChipClicked = onFilterSelected
            )
        }
    }
}