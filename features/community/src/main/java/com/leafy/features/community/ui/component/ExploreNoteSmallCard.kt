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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.R as SharedR

/**
 * 공통 노트 카드
 * - showProfile: 프로필 영역 표시 여부
 * - showHotBadge: 썸네일 위에 '급상승' 같은 뱃지 표시 여부
 */
@Composable
fun ExploreNoteSmallCard(
    note: ExploreNoteSummaryUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    showProfile: Boolean = true,
    showHotBadge: Boolean = false,
    hotLabel: String = "급상승"
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = modifier
            .width(220.dp)              // 가로 스크롤용 카드 폭
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 🔹 썸네일 + (옵션) 급상 뱃지
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
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

            // 텍스트 + 별점 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = colors.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = note.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 별점
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val filledCount = note.rating.toInt().coerceIn(0, 5)
                        (1..5).forEach { index ->
                            val isFilled = index <= filledCount
                            Image(
                                painter = painterResource(
                                    id = if (isFilled)
                                        SharedR.drawable.ic_star_filled
                                    else
                                        SharedR.drawable.ic_star_outline
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                            )
                            if (index < 5) {
                                Spacer(modifier = Modifier.width(2.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = String.format("%.1f", note.rating),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.error
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // 🔹 프로필 아이콘 (옵션)
                    if (showProfile && note.profileImageRes != null) {
                        Image(
                            painter = painterResource(id = note.profileImageRes),
                            contentDescription = note.authorName ?: "Author avatar",
                            modifier = Modifier
                                .size(24.dp)
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
private fun ExploreNoteSmallCardPreview() {
    LeafyTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            ExploreNoteSmallCard(
                note = ExploreNoteSummaryUi(
                    title = "프리미엄 제주 녹차",
                    subtitle = "깔끔하고 상쾌한 맛의 일품",
                    imageRes = SharedR.drawable.ic_sample_tea_1,
                    rating = 4.8f,
                    reviewCount = 234,
                    profileImageRes = SharedR.drawable.ic_profile_1,
                    authorName = "Subin"
                )
            )
        }
    }
}