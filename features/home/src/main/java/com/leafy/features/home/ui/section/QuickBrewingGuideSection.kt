package com.leafy.features.home.ui.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.features.home.ui.components.BrewingInfoCard
import com.leafy.shared.R
import com.leafy.shared.ui.component.LeafySectionHeader
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun QuickBrewingGuideSection(
    modifier: Modifier = Modifier,
    temperature: Int = 85,
    time: String = "3 min",
    amount: String = "2g",
    onClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {

        LeafySectionHeader(
            title = "빠른 브루잉 가이드",
            titleStyle = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = colors.primary
            ),
            onMoreClick = onClick,
            modifier = Modifier.padding(horizontal = 0.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BrewingInfoCard(
                iconRes = R.drawable.ic_temp,
                title = "온도",
                value = "${temperature}℃",
                modifier = Modifier.weight(1f)
            )

            BrewingInfoCard(
                iconRes = R.drawable.ic_timer,
                title = "시간",
                value = time,
                modifier = Modifier.weight(1f)
            )

            BrewingInfoCard(
                iconRes = R.drawable.ic_leaf,
                title = "찻잎",
                value = amount,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuickBrewingGuideSectionPreview() {
    LeafyTheme {
        QuickBrewingGuideSection(
            modifier = Modifier.padding(vertical = 16.dp),
            onClick = { }
        )
    }
}