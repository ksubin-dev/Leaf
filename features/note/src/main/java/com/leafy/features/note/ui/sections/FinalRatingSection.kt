package com.leafy.features.note.ui.sections

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.components.LeafyFieldLabel
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun FinalRatingSection(
    modifier: Modifier = Modifier,
    rating: Int,
    purchaseAgain: Boolean?,
    onRatingChange: (Int) -> Unit,
    onPurchaseAgainChange: (Boolean) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(modifier = modifier) {
        // 섹션 타이틀
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = SharedR.drawable.ic_note_section_final_rating),
                contentDescription = "Final Rating",
                tint = colors.error,
                modifier = Modifier
                    .height(18.dp)
                    .padding(end = 6.dp)
            )
            Text(
                text = "Final Rating",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = colors.secondary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Overall Rating
        LeafyFieldLabel(text = "Overall Rating")
        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            (1..5).forEach { starIndex ->
                val isFilled = starIndex <= rating
                IconButton(
                    onClick = { onRatingChange(starIndex) }
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isFilled) SharedR.drawable.ic_star_filled
                            else SharedR.drawable.ic_star_outline
                        ),
                        contentDescription = "$starIndex stars",
                        tint = Color.Unspecified,
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Would you purchase this tea again?
        Text(
            text = "Would you purchase this tea again?",
            style = MaterialTheme.typography.bodySmall,
            color = colors.secondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Yes 버튼
            val yesSelected = purchaseAgain == true
            Button(
                onClick = { onPurchaseAgainChange(true) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (yesSelected) colors.primary else colors.background,
                    contentColor = if (yesSelected) colors.onPrimary else colors.primary
                ),
                border = if (yesSelected) null else BorderStroke(1.dp, colors.outlineVariant)
            ) {
                Text(
                    text = "Yes",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // No 버튼
            val noSelected = purchaseAgain == false
            OutlinedButton(
                onClick = { onPurchaseAgainChange(false) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (noSelected) colors.primaryContainer else colors.background,
                    contentColor = colors.onBackground
                ),
                border = BorderStroke(1.dp, colors.outlineVariant)
            ) {
                Text(
                    text = "No",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FinalRatingSectionPreview() {
    LeafyTheme {
        FinalRatingSection(
            rating = 4,
            purchaseAgain = true,
            onRatingChange = {},
            onPurchaseAgainChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}