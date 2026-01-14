package com.leafy.features.note.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.leafy.shared.R

@Composable
fun LeafyRatingInput(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    starSize: Dp = 32.dp,
    spacing: Dp = 8.dp
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        (1..5).forEach { index ->
            val isFilled = index <= rating

            IconButton(
                onClick = { onRatingChange(index) },
                modifier = Modifier.size(starSize + 12.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isFilled) R.drawable.ic_star_filled
                        else R.drawable.ic_star_outline
                    ),
                    contentDescription = "$index 점",
                    modifier = Modifier.size(starSize),
                    tint = if (isFilled) colors.error else colors.outlineVariant
                )
            }
        }
    }
}