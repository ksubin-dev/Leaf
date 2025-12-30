package com.leafy.features.community.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.ui.component.RatingStars
import com.leafy.shared.R as SharedR

/**
 * 노트 요약 카드 - 간결한 버전
 * '이번 주 인기 노트' 등 섹션에서 사용됩니다.
 */
@Composable
fun ExploreSummaryNoteCard(
    note: ExploreNoteUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    showHotBadge: Boolean = false,
    hotLabel: String = "인기"
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = modifier
            .width(220.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = note.imageUrl,
                    contentDescription = note.title,
                    placeholder = painterResource(id = SharedR.drawable.ic_sample_tea_1),
                    error = painterResource(id = SharedR.drawable.ic_sample_tea_1),
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )

                if (showHotBadge) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp),
                        shape = RoundedCornerShape(999.dp),
                        color = colors.error
                    ) {
                        Text(
                            text = hotLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.onPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = colors.onSurface,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = note.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RatingStars(
                        rating = note.rating.toInt(),
                        size = 14.dp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    if (note.authorProfileUrl != null) {
                        AsyncImage(
                            model = note.authorProfileUrl,
                            contentDescription = "Author avatar",
                            placeholder = painterResource(id = SharedR.drawable.ic_profile_1),
                            error = painterResource(id = SharedR.drawable.ic_profile_1),
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExploreSummaryNoteCardPreview() {
    LeafyTheme {
        ExploreSummaryNoteCard(
            note = ExploreNoteUi(
                id = "sample_1",
                title = "프리미엄 제주 녹차",
                subtitle = "깔끔하고 상쾌한 맛의 일품",
                imageUrl = null,
                rating = 4.8f,
                authorProfileUrl = null
            )
        )
    }
}