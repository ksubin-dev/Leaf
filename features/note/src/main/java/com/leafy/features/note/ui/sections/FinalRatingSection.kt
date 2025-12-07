package com.leafy.features.note.ui.sections

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.components.LeafyFieldLabel // Import
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun FinalRatingSection(
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    var rating by remember { mutableStateOf(0) }
    var purchaseAgain by remember { mutableStateOf<Boolean?>(null) }

    Column(modifier = modifier) {

        // 섹션 타이틀
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
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

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            (1..5).forEach { starIndex ->
                val isFilled = starIndex <= rating
                IconButton(
                    onClick = { rating = starIndex }
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isFilled) {
                                SharedR.drawable.ic_star_filled
                            } else {
                                SharedR.drawable.ic_star_outline
                            }
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
                onClick = { purchaseAgain = true },
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
                onClick = { purchaseAgain = false },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (noSelected) colors.primaryContainer else colors.background,
                    contentColor = colors.onBackground
                ),
                border = BorderStroke(
                    1.dp,
                    if (noSelected) colors.outlineVariant else colors.outlineVariant
                )
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
            modifier = Modifier.padding(16.dp)
        )
    }
}