package com.leafy.features.note.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.common.DetailInfoRow
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.TeaInfo

@Composable
fun TeaInfoDetailSection(
    teaInfo: TeaInfo,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = "Tea Information",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        DetailInfoRow(label = "Tea Name", value = teaInfo.name)
        DetailInfoRow(label = "Type", value = teaInfo.type)
        DetailInfoRow(label = "Brand", value = teaInfo.brand)
        DetailInfoRow(label = "Leaf Style", value = teaInfo.leafStyle)
        DetailInfoRow(label = "Processing", value = teaInfo.processing)
        DetailInfoRow(label = "Grade", value = teaInfo.grade)
    }
}



@Preview(showBackground = true)
@Composable
fun TeaInfoDetailSectionPreview() {
    val mockTeaInfo = TeaInfo(
        name = "Earl Grey Supreme",
        brand = "Twinings",
        type = "Black Tea",
        leafStyle = "Loose Leaf",
        processing = "Whole Leaf",
        grade = "FTGFOP"
    )
    LeafyTheme {
        TeaInfoDetailSection(teaInfo = mockTeaInfo)
    }
}