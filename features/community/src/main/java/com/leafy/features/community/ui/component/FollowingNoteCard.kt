package com.leafy.features.community.ui.component


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.zIndex
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun ExploreFollowingNoteCard(
    note: ExploreFollowingNoteUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),          // ✅ 카드 전체 클릭
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // ───── 상단 작성자 정보 ─────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = note.authorAvatarRes),
                    contentDescription = note.authorName,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = note.authorName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = colors.onSurface
                    )
                    Text(
                        text = note.timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.primary
                    )
                }

                IconButton(onClick = { /* TODO: 더보기 */ }) {
                    Icon(
                        painter = painterResource(id = SharedR.drawable.ic_more_vert),
                        contentDescription = "More",
                        tint = colors.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ───── 메인 이미지 + 타입 배지 ─────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = note.imageRes),
                    contentDescription = note.title,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )

                // 상단 왼쪽 티 타입 배지 (예: "Oolong")
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(colors.surface.copy(alpha = 0.85f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = note.tagLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ───── 제목 + 메타 + 짧은 설명 ─────
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = colors.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = note.meta,
                style = MaterialTheme.typography.labelSmall,
                color = colors.secondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = note.description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ───── 브루잉 정보 칩들 (온도/시간/그람/우림차수) ─────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                note.brewingChips.forEach { chip ->
                    ExploreFollowingChip(text = chip)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ───── 별점 + 리뷰 칩 ─────
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                        modifier = Modifier.size(14.dp)
                    )
                    if (index < 5) Spacer(modifier = Modifier.width(2.dp))
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = String.format("%.1f", note.rating),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurface
                )

                Spacer(modifier = Modifier.width(8.dp))

                ExploreFollowingChip(
                    text = note.reviewLabel
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ───── 코멘트 말풍선 ─────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surfaceVariant.copy(alpha = 0.5f))
                    .padding(12.dp)
            ) {
                Text(
                    text = note.comment,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.secondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ───── 하단 액션 1: 아이콘 (좋아요/댓글/북마크) ─────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = SharedR.drawable.ic_like),
                    contentDescription = "Like",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))

                Image(
                    painter = painterResource(id = SharedR.drawable.ic_comment),
                    contentDescription = "Comment",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))

                Image(
                    painter = painterResource(id = SharedR.drawable.ic_bookmark),
                    contentDescription = "Bookmark",
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ───── 하단 액션 2: 겹쳐 보이는 프로필 + "23명이 좋아합니다" ─────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 겹치는 아바타들
                Box {
                    note.likerAvatarResList
                        .take(3)
                        .forEachIndexed { index, resId ->
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(20.dp)
                                    .offset(x = (index * -8).dp)   // 왼쪽으로 겹치기
                                    .zIndex((10 - index).toFloat())
                                    .clip(CircleShape)
                                    .border(
                                        width = 1.dp,
                                        color = colors.surface,
                                        shape = CircleShape
                                    ),
                                contentScale = ContentScale.Crop
                            )
                        }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = note.likeCountText, // 예) "23명이 좋아합니다"
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.secondary
                )
            }
        }
    }
}

// ───── 팔로잉 칩 (브루잉 정보/리뷰 가능) 공통 컴포넌트 ─────
@Composable
private fun ExploreFollowingChip(
    text: String,
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(
                colors.primaryContainer.copy(alpha = 0.7f)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = colors.onPrimaryContainer
        )
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
private fun ExploreFollowingNoteCardPreview() {
    LeafyTheme {
        val dummy = ExploreFollowingNoteUi(
            authorName = "민지",
            authorAvatarRes = SharedR.drawable.ic_profile_1,
            timeAgo = "2시간 전",
            tagLabel = "Oolong",
            imageRes = SharedR.drawable.ic_sample_tea_7,
            title = "동정오룡차",
            meta = "대만 · 중배화 · 반구형",
            description = "은은한 꽃향과 부드러운 과일향이 조화롭게 어우러진 오롱차, 목넘김이 매끄럽고 여운이 깁니다.",
            brewingChips = listOf("95℃", "3m", "5g", "1st Infusion"),
            rating = 4.5f,
            reviewLabel = "Rebrew 가능",
            comment = "오늘 아침에 마신 차 중 최고였어요. 3회까지 우려봤는데 2번째 우림이 가장 좋았답니다. 은은한 난향이 정말 매력적이에요. 😊",
            likerAvatarResList = listOf(
                SharedR.drawable.ic_profile_2,
                SharedR.drawable.ic_profile_3,
                SharedR.drawable.ic_profile_4
            ),
            likeCountText = "23명이 좋아합니다"
        )

        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            ExploreFollowingNoteCard(
                note = dummy,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}