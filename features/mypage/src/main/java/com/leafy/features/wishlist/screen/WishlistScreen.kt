package com.leafy.features.wishlist.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.wishlist.data.SavedCommunityNote
import com.leafy.features.wishlist.data.WishlistItem
import com.leafy.features.wishlist.ui.components.SavedNotesSection
import com.leafy.features.wishlist.ui.components.WishlistItemCard
import com.leafy.shared.R as SharedR

@Composable
fun WishlistScreen(
    wishlistItems: List<WishlistItem>,
    savedNotes: List<SavedCommunityNote>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "마셔보고 싶은 차",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "더보기 →",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = { /* TODO: 위시리스트 전체보기 */ })
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (wishlistItems.isEmpty()) {
                Text(
                    text = "현재 위시리스트에 저장된 차가 없습니다.",
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                wishlistItems.forEach { item ->
                    WishlistItemCard(
                        item = item,
                        onRemoveClick = { itemId -> println("Removed item ID: $itemId") }
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        SavedNotesSection(
            notes = savedNotes,
            onMoreClick = { /* TODO: 저장된 노트 전체보기 */ }
        )

        Spacer(Modifier.height(60.dp))
    }
}


// -----------------------------------------------------------
// Preview (더미 데이터 포함)
// -----------------------------------------------------------

private val dummyWishlistItems = listOf(
    WishlistItem("1", "다즐링 퍼스트 플러시", "인도 | 홍차", SharedR.drawable.ic_sample_tea_4),
    WishlistItem("2", "백모단 화이트티", "중국 | 백차", SharedR.drawable.ic_sample_tea_5),
    WishlistItem("3", "루이보스 바닐라", "남아공 | 허브티", SharedR.drawable.ic_sample_tea_6),
)

private val dummySavedNotes = listOf(
    SavedCommunityNote(
        "n1", "Dragon Well Green Tea", "Fresh, grassy notes with subtle sweetness...", 4.5f, 124,
        SharedR.drawable.ic_sample_tea_13, SharedR.drawable.ic_profile_1
    ),
    SavedCommunityNote(
        "n2", "Earl Grey Supreme", "Citrusy bergamot with floral lavender hints...", 4.0f, 98,
        SharedR.drawable.ic_sample_tea_14, SharedR.drawable.ic_profile_2
    ),
    SavedCommunityNote(
        "n3", "Ti Kuan Yin Oolong", "Complex floral aroma with roasted finish...", 4.8f, 87,
        SharedR.drawable.ic_sample_tea_15, SharedR.drawable.ic_profile_3
    )
)

@Preview(showBackground = true)
@Composable
private fun WishlistScreenPreview() {
    WishlistScreen(wishlistItems = dummyWishlistItems, savedNotes = dummySavedNotes)
}