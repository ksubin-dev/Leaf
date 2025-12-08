package com.leafy.features.home.ui.section

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.shared.R
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.features.home.data.RecentNoteItem
import com.leafy.features.home.ui.components.RecentNoteCard


@Composable
fun RecentMyRecordsSection(
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    // 임시정의 뷰모델로 할 예정
    val items = listOf(
        RecentNoteItem(
            title = "Earl Grey Supreme",
            rating = 5.0,
            imageRes = R.drawable.img_recent_1,
            description = "Perfect bergamot balance with smooth black tea base..."
        ),
        RecentNoteItem(
            title = "Jasmine Pearl",
            rating = 4.5,
            imageRes = R.drawable.img_recent_2,
            description = "Delicate floral aroma with gentle tea finish..."
        )
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "최근 나의 기록",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                ),
                modifier = Modifier.weight(1f),
                color = colors.onBackground
            )
            Text(
                text = "More +",
                color = colors.primary,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(start = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items.forEach { item ->
                RecentNoteCard(item = item)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecentMyRecordsSectionPreview() {
    LeafyTheme {
        RecentMyRecordsSection(
            modifier = Modifier.fillMaxWidth()
        )
    }
}