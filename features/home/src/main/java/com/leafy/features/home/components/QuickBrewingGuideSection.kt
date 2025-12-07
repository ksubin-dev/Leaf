package com.leafy.features.home.components

import BrewingInfoCard
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.shared.R
import com.leafy.shared.ui.theme.LeafyTheme


@Composable
fun QuickBrewingGuideSection(
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Column(modifier = modifier) {
        Text(
            text = "빠른 브루잉 가이드",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            ),
            color = colors.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BrewingInfoCard(
                iconRes = R.drawable.ic_temp,
                title = "Temperature",
                value = "85℃",
                modifier = Modifier.weight(1f)
            )
            BrewingInfoCard(
                iconRes = R.drawable.ic_timer,
                title = "Steeping",
                value = "3 min",
                modifier = Modifier.weight(1f)
            )
            BrewingInfoCard(
                iconRes = R.drawable.ic_leaf,
                title = "Amount",
                value = "2g",
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}