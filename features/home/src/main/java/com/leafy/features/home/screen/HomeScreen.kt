package com.leafy.features.home.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.R


/**
 * Leafy Home 화면
 */
@Preview(showBackground = true, showSystemUi = false)
@Composable
fun HomeScreen() {
    LeafyTheme {
        val colors = MaterialTheme.colorScheme
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp) // 바텀바 살짝 여유
        ) {
            // 1. 히어로 이미지
            HeroTeaImage(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .fillMaxWidth()
                    .height(200.dp)
            )

            // 2. 빠른 브루잉 가이드
            QuickBrewingGuideSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. 지금 인기 있는 시음 기록 Top 3
            PopularTop3Section(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4. 최근 나의 기록
            RecentMyRecordsSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
    }
}

/* ------------------------ 1. 히어로 이미지 ------------------------ */

@Composable
private fun HeroTeaImage(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 배경 이미지
            Image(
                painter = painterResource(id = R.drawable.img_home_hero_tea),
                contentDescription = "Tea hero image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 텍스트 영역 (왼쪽 아래)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Text(
                    text = "Tea of the Month",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Discover premium Dragon Well",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Limited Edition",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )
            }
        }
    }
}

/* ------------------- 2. 빠른 브루잉 가이드 섹션 ------------------- */

@Composable
private fun QuickBrewingGuideSection(
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Column(modifier = modifier) {
        Text(
            text = "빠른 브루잉 가이드",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            ),
            color = colors.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BrewingInfoCard(
                iconRes = R.drawable.ic_temp,
                title = "Temperature",
                value = "85℃",
                modifier = Modifier.weight(1f)
            )
            BrewingInfoCard(
                iconRes = R.drawable.ic_timer,
                title = "Steeping",
                value = "3 min",
                modifier = Modifier.weight(1f)
            )
            BrewingInfoCard(
                iconRes = R.drawable.ic_leaf,
                title = "Amount",
                value = "2g",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BrewingInfoCard(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    title: String,
    value: String
) {

    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = colors.surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                tint = colors.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = colors.onBackground
            )
        }
    }
}

/* ----------------- 3. 인기 시음 기록 Top 3 섹션 ----------------- */

@Immutable
private data class TeaRankingItem(
    val rank: Int,
    val name: String,
    val typeCountry: String,
    val rating: Double,
    val ratingCount: Int,
    val imageRes: Int,
    val badgeColor: Color
)

@Composable
private fun PopularTop3Section(
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    val items = listOf(
        TeaRankingItem(
            rank = 1,
            name = "Premium Sencha",
            typeCountry = "녹차 · 일본",
            rating = 4.8,
            ratingCount = 234,
            imageRes = R.drawable.img_rank_1,
            badgeColor = colors.primary
        ),
        TeaRankingItem(
            rank = 2,
            name = "Earl Grey Supreme",
            typeCountry = "홍차 · 영국",
            rating = 4.7,
            ratingCount = 189,
            imageRes = R.drawable.img_rank_2,
            badgeColor = colors.secondary
        ),
        TeaRankingItem(
            rank = 3,
            name = "Jasmine Pearl",
            typeCountry = "가향차 · 중국",
            rating = 4.6,
            ratingCount = 156,
            imageRes = R.drawable.img_rank_3,
            badgeColor = colors.secondary
        )
    )

    Column(modifier = modifier) {
        Text(
            text = "지금 인기 있는 시음 기록 Top 3",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            ),
            color = colors.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 카테고리 필터 칩 (이번 주 / 녹차 / 가향차 / ...)
        FilterChipRow()

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items.forEach { item ->
                RankedTeaRow(item = item)
            }
        }
    }
}

@Composable
private fun FilterChipRow() {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LeafyFilterChip(text = "이번 주", selected = true)
        LeafyFilterChip(text = "녹차", selected = false)
        LeafyFilterChip(text = "가향차", selected = false)
        LeafyFilterChip(text = "홍차", selected = false)
        LeafyFilterChip(text = "밀크티", selected = false)
    }
}

@Composable
private fun LeafyFilterChip(
    text: String,
    selected: Boolean
) {
    val colors = MaterialTheme.colorScheme

    val bg = if (selected) colors.primary else colors.surfaceVariant
    val fg = if (selected) colors.onPrimary else colors.onSurfaceVariant

    Surface(
        shape = RoundedCornerShape(50),
        color = bg
    ) {
        Text(
            text = text,
            color = fg,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        )
    }
}

@Composable
private fun RankedTeaRow(
    item: TeaRankingItem
) {

    val colors = MaterialTheme.colorScheme

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 순위 배지
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(item.badgeColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.rank.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 썸네일
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.name,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.typeCountry,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "rating",
                        tint = colors.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${item.rating}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = colors.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${item.ratingCount})",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/* --------------------- 4. 최근 나의 기록 섹션 --------------------- */

@Immutable
private data class RecentNoteItem(
    val title: String,
    val rating: Double,
    val imageRes: Int,
    val description: String
)

@Composable
private fun RecentMyRecordsSection(
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    val items = listOf(
        RecentNoteItem(
            title = "Earl Grey Supreme",
            rating = 5.0,
            imageRes = R.drawable.img_recent_1,
            description = "Perfect bergamot balance with smooth black tea base..."
        ),
        RecentNoteItem(
            title = "Jasmine Pearl",
            rating = 4.5,
            imageRes = R.drawable.img_recent_2,
            description = "Delicate floral aroma with gentle tea finish..."
        )
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "최근 나의 기록",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                ),
                modifier = Modifier.weight(1f),
                color = colors.onBackground
            )
            Text(
                text = "More +",
                color = colors.primary,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(start = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items.forEach { item ->
                RecentNoteCard(item = item)
            }
        }
    }
}

@Composable
private fun RecentNoteCard(
    item: RecentNoteItem
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .width(260.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "rating",
                        tint = colors.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${item.rating}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant

                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurface,
                    maxLines = 2
                )
            }
        }
    }
}