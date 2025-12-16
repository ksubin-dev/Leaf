package com.leafy.features.collection.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.collection.data.TeaCollectionItem
import com.leafy.features.collection.ui.components.TeaFilterSection
import com.leafy.features.collection.ui.components.CollectionItemCard
import com.leafy.shared.R as SharedR

@Composable
fun CollectionScreen(
    items: List<TeaCollectionItem>,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredItems = remember(items, selectedFilter) {
        if (selectedFilter == "All") {
            items
        } else {
            items.filter {
                it.name.contains(selectedFilter, ignoreCase = true) ||
                        it.brand.contains(selectedFilter, ignoreCase = true)
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {


        TeaFilterSection(
            filters = DefaultTeaFilters,
            selectedType = selectedFilter,
            onFilterSelected = { newFilter -> selectedFilter = newFilter },
            modifier = Modifier.padding(vertical = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredItems, key = { it.id }) { item ->
                CollectionItemCard(
                    item = item,
                    onRecordClick = { itemId ->
                        // TODO: 기록 화면으로 이동 로직
                        println("Record clicked for item ID: $itemId")
                    }
                )
            }
        }
    }
}

// -----------------------------------------------------------
// Preview를 위한 가짜(Dummy) 데이터 및 구조
// -----------------------------------------------------------

private val DefaultTeaFilters = listOf(
    "All", "Black", "Green", "Oolong", "White", "Herbal"
)

private val dummyCollectionItems = listOf(
    TeaCollectionItem("1", "Earl Grey Supreme", "Twinings", "Plenty", SharedR.drawable.ic_sample_collection_tea_1),
    TeaCollectionItem("2", "Sencha Green", "Ippodo Tea", "Low", SharedR.drawable.ic_sample_collection_tea_2),
    TeaCollectionItem("3", "High Mountain Oolong", "Mountain Tea", "Empty", SharedR.drawable.ic_sample_collection_tea_3),
    TeaCollectionItem("4", "Chamomile Dreams", "Celestial Seasonings", "Plenty", SharedR.drawable.ic_sample_collection_tea_4),
    TeaCollectionItem("5", "Assam Black", "Vahdam Teas", "Plenty", SharedR.drawable.ic_sample_tea_5),
    TeaCollectionItem("6", "Jasmine Green", "Teavana", "Low", SharedR.drawable.ic_sample_tea_8),
    TeaCollectionItem("7", "Peppermint Herbal", "Bigelow", "Plenty", SharedR.drawable.ic_sample_tea_7),
)

@Preview(showBackground = true)
@Composable
private fun CollectionScreenPreview() {
    CollectionScreen(items = dummyCollectionItems)
}