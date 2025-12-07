package com.leafy.features.community.ui.component


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.R as SharedR

/**
 * 노트 요약 카드 - 간결한 버전 (제목, 서브타이틀, 텍스트 별점, 옵션으로 프로필 사진만 표시)
 * 이 카드는 '이번 주 인기 노트'에 사용됩니다.
 */
@Composable
fun ExploreSummaryNoteCard(
    note: ExploreNoteSummaryUi,
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
            // 🔹 썸네일 + (옵션) 뱃지
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = note.imageRes),
                    contentDescription = note.title,
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )

                if (showHotBadge) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(colors.error)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = hotLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.onPrimary
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
                    color = colors.onSurface
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val filledStars = note.rating.toInt().coerceIn(0, 5)

                        Text(
                            text = "★".repeat(filledStars),
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.error
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    if (note.profileImageRes != null) {
                        Image(
                            painter = painterResource(id = note.profileImageRes),
                            contentDescription = "Author avatar",
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(50)),
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
private fun ExploreSummaryNoteCardWithProfilePreview() {
    LeafyTheme {
        ExploreSummaryNoteCard(
            note = ExploreNoteSummaryUi(
                title = "프리미엄 제주 녹차",
                subtitle = "깔끔하고 상쾌한 맛의 일품",
                imageRes = SharedR.drawable.ic_sample_tea_1,
                rating = 4.8f,
                savedCount = 234,
                profileImageRes = SharedR.drawable.ic_profile_1
            )
        )
    }
}
