package com.leafy.features.mypage.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.mypage.ui.component.BrewingRecordCard
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.R as SharedR
import com.subin.leafy.domain.model.BrewingRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyRecordsScreen(
    date: String,
    records: List<BrewingRecord>,
    onBackClick: () -> Unit,
    onRecordClick: (String) -> Unit,
    onEditClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "${date.replace("-", ". ")} 기록",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = SharedR.drawable.ic_back),
                            contentDescription = "Back",
                            modifier = Modifier.height(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = records,
                key = { it.id }
            ) { record ->
                BrewingRecordCard(
                    imageUrl = record.imageUrl,
                    teaName = record.teaName,
                    metaInfo = record.metaInfo,
                    rating = record.rating,
                    onEditClick = { onEditClick(record.id) },
                    onCardClick = { onRecordClick(record.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
private fun DailyRecordsScreenPreview() {
    val mockDate = "2026-01-02"
    val mockRecords = listOf(
        BrewingRecord(
            id = "1",
            teaName = "우전 녹차",
            metaInfo = "85°C / 3.0g / 2:00",
            rating = 5,
            dateString = mockDate
        ),
        BrewingRecord(
            id = "2",
            teaName = "Oriental Beauty",
            metaInfo = "90°C / 2.5g / 1:30",
            rating = 4,
            dateString = mockDate
        ),
        BrewingRecord(
            id = "3",
            teaName = "다즐링 세컨드 플러시",
            metaInfo = "95°C / 3.0g / 3:00",
            rating = 5,
            dateString = mockDate
        )
    )

    LeafyTheme {
        DailyRecordsScreen(
            date = mockDate,
            records = mockRecords,
            onBackClick = {},
            onRecordClick = {},
            onEditClick = {}
        )
    }
}