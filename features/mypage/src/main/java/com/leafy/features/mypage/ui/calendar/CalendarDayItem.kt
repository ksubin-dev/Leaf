package com.leafy.features.mypage.ui.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.aspectRatio
import com.leafy.shared.R as SharedR
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarDayItem(
    modifier: Modifier = Modifier,
    date: LocalDate,
    isSelected: Boolean,
    hasSession: Boolean,
    isCurrentMonth: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val circleColor = when {
        isSelected -> colors.onSurface
        else -> MaterialTheme.colorScheme.surface
    }
    val contentColor = when {
        isSelected -> colors.surface
        isCurrentMonth -> colors.onBackground
        else -> colors.onSurfaceVariant.copy(alpha = 0.5f)
    }

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick)
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(circleColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = contentColor
            )
        }

        if (hasSession) {
            Icon(
                painter = painterResource(id = SharedR.drawable.ic_leaf),
                contentDescription = "Tea Session Marker",
                tint = colors.primary,
                modifier = Modifier.size(10.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}