package com.leafy.features.mypage.presentation.badges.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.leafy.features.mypage.presentation.badges.data.BadgeItem

@Composable
fun BadgeCard(
    modifier: Modifier = Modifier,
    badge: BadgeItem
) {
    val colors = MaterialTheme.colorScheme


    val cardBackgroundColor = if (badge.isAcquired) colors.primaryContainer else colors.surfaceContainerHigh
    val iconBackgroundColor = if (badge.isAcquired) colors.primary else colors.onSurfaceVariant.copy(alpha = 0.5f)
    val contentColor = if (badge.isAcquired) colors.onPrimaryContainer else colors.onSurface

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = badge.iconRes),
                    contentDescription = badge.title,
                    modifier = Modifier.size(32.dp),
                    tint = if (badge.isAcquired) colors.onPrimary else colors.surface
                )
            }

            Spacer(Modifier.height(16.dp))


            Text(
                text = badge.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(4.dp))


            Text(
                text = badge.description,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )


            if (badge.progress != null && !badge.isAcquired) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = badge.progress,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.error
                )
            }
        }
    }
}