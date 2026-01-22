package com.leafy.features.mypage.presentation.record
//
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
import androidx.compose.ui.unit.dp
import com.leafy.features.mypage.presentation.main.component.BrewingRecordCard
import com.leafy.shared.common.singleClick
import com.leafy.shared.R as SharedR
import com.subin.leafy.domain.model.BrewingNote
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyRecordsScreen(
    date: LocalDate,
    records: List<BrewingNote>,
    onBackClick: () -> Unit,
    onRecordClick: (String) -> Unit,
    onEditClick: (String) -> Unit
) {
    val dateString = date.format(DateTimeFormatter.ofPattern("yyyy. MM. dd"))

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "$dateString 기록",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = singleClick { onBackClick() }) {
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
            ) { note ->
                BrewingRecordCard(
                    imageUrl = note.metadata.imageUrls.firstOrNull(),
                    teaName = note.teaInfo.name,
                    metaInfo = "${note.teaInfo.type.name} · ${note.recipe.waterTemp}°C",
                    rating = note.rating.stars,
                    onEditClick = { onEditClick(note.id) },
                    onCardClick = { onRecordClick(note.id) }
                )
            }
        }
    }
}