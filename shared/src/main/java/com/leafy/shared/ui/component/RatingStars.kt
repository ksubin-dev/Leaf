package com.leafy.shared.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.leafy.shared.R as SharedR

@Composable
fun RatingStars(rating: Int) {
    Row {
        repeat(5) { index ->
            val isFilled = index < rating
            Icon(
                painter = painterResource(
                    id = if (isFilled) SharedR.drawable.ic_star_filled
                    else SharedR.drawable.ic_star_outline
                ),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = if (isFilled) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}