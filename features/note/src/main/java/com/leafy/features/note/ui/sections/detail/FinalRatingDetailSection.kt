package com.leafy.features.note.ui.sections.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.features.note.ui.common.DetailSectionCard
import com.leafy.shared.R
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.RatingInfo

@SuppressLint("DefaultLocale")
@Composable
fun FinalRatingSection(
    rating: RatingInfo,
    modifier: Modifier = Modifier
) {
    DetailSectionCard(
        title = "최종 평가 (Overall Rating)",
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. 별점 아이콘 (5개)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    val isFilled = index < rating.stars
                    Icon(
                        painter = painterResource(
                            id = if (isFilled) R.drawable.ic_star_filled else R.drawable.ic_star_outline
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = if (isFilled) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = String.format("%.1f", rating.stars.toFloat()),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
                fontSize = 40.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "재구매 의사",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (rating.purchaseAgain != null) {
                    val isYes = rating.purchaseAgain == true
                    val bgColor = if (isYes) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    val contentColor = if (isYes) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onError
                    val icon = if (isYes) Icons.Default.Check else Icons.Default.Close
                    val text = if (isYes) "Yes" else "No"

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(bgColor)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = text,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = contentColor
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = contentColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else {
                    // 선택 안 함
                    Text(
                        text = "-",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------------------------
// Preview
// ----------------------------------------------------------------------
@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun FinalRatingSectionPreview() {
    LeafyTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Case 1: 5점 + Yes
            FinalRatingSection(
                rating = RatingInfo(stars = 5, purchaseAgain = true)
            )

            // Case 2: 2점 + No
            FinalRatingSection(
                rating = RatingInfo(stars = 2, purchaseAgain = false)
            )
        }
    }
}