package com.leafy.features.mypage.presentation.record

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.shared.common.clickableSingle
import com.leafy.shared.R as SharedR

@Composable
fun CalendarDayItem(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    hasRecord: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isToday -> MaterialTheme.colorScheme.primary
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        else -> Color.Transparent
    }

    val borderColor = when {
        isSelected && !isToday -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }

    val textColor = when {
        isToday -> Color.White
        isSelected -> MaterialTheme.colorScheme.primary
        else -> Color.DarkGray
    }

    val fontWeight = when {
        isToday || isSelected -> FontWeight.Bold
        else -> FontWeight.Medium
    }

    val iconTint = when {
        isToday -> Color.White.copy(alpha = 0.8f)
        else -> MaterialTheme.colorScheme.primary
    }

    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickableSingle { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = day.toString(),
            fontSize = 12.sp,
            fontWeight = fontWeight,
            color = textColor
        )

        if (hasRecord) {
            Icon(
                painter = painterResource(id = SharedR.drawable.ic_leaf),
                contentDescription = null,
                modifier = Modifier.size(10.dp),
                tint = iconTint
            )
        } else {
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}