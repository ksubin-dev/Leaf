package com.leafy.features.note.ui.sections.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.common.LeafyRatingInput
import com.leafy.features.note.ui.common.NoteSectionHeader
import com.leafy.shared.R
import com.leafy.shared.ui.component.LeafySelectableButton
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun FinalRatingSection(
    rating: Int,
    purchaseAgain: Boolean?,
    onRatingChange: (Int) -> Unit,
    onPurchaseAgainChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxWidth()) {
        NoteSectionHeader(
            icon = painterResource(id = R.drawable.ic_note_section_final_rating),
            title = "최종 평가"
        )

        Text(
            text = "종합 별점",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.secondary,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        LeafyRatingInput(
            rating = rating,
            onRatingChange = onRatingChange,
            modifier = Modifier.padding(start = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "이 차를 다시 구매하시겠습니까?",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LeafySelectableButton(
                text = "네",
                isSelected = purchaseAgain == true,
                onClick = { onPurchaseAgainChange(true) },
                modifier = Modifier.weight(1f)
            )
            LeafySelectableButton(
                text = "아니오",
                isSelected = purchaseAgain == false,
                onClick = { onPurchaseAgainChange(false) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun FinalRatingSectionPreview() {
    LeafyTheme {
        var rating by remember { mutableIntStateOf(0) }
        var purchaseAgain by remember { mutableStateOf<Boolean?>(null) }

        Column(modifier = Modifier.padding(16.dp)) {
            FinalRatingSection(
                rating = rating,
                purchaseAgain = purchaseAgain,
                onRatingChange = { rating = it },
                onPurchaseAgainChange = { purchaseAgain = it }
            )
        }
    }
}