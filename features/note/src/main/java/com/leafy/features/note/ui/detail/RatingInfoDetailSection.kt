package com.leafy.features.note.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.RatingInfo
import com.leafy.shared.R as SharedR

@Composable
fun RatingInfoDetailSection(
    modifier: Modifier = Modifier,
    ratingInfo: RatingInfo
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 40.dp, start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Overall Rating",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 별점 표시
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) { index ->
                val isFilled = index < ratingInfo.stars
                Icon(
                    painter = painterResource(
                        id = if (isFilled) SharedR.drawable.ic_star_filled
                        else SharedR.drawable.ic_star_outline
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = Color.Unspecified
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = ratingInfo.stars.toFloat().toString(),
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp
            ),
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 재구매 여부 행
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Would purchase again?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (ratingInfo.purchaseAgain == true) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = androidx.compose.foundation.shape.CircleShape
                ) {
                    Text(
                        text = "Yes",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (ratingInfo.purchaseAgain == false) {
                Surface(
                    color = MaterialTheme.colorScheme.error,
                    shape = androidx.compose.foundation.shape.CircleShape
                ) {
                    Text(
                        text = "No",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onError,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RatingInfoDetailSectionPreview() {
    LeafyTheme {
        RatingInfoDetailSection(
            ratingInfo = RatingInfo(
                stars = 4,
                purchaseAgain = false
            )
        )
    }
}