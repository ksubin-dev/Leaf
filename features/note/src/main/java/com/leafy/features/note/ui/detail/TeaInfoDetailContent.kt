 package com.leafy.features.note.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.leafy.features.note.ui.common.DetailInfoRow
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.TeaInfo

 @Composable
 fun TeaInfoDetailContent(teaInfo: TeaInfo, modifier: Modifier = Modifier) {
     Column(modifier = modifier.fillMaxWidth()) {
         if (teaInfo.name.isNotBlank()) DetailInfoRow(label = "Tea Name", value = teaInfo.name)
         if (teaInfo.type.isNotBlank()) DetailInfoRow(label = "Type", value = teaInfo.type)
         if (teaInfo.brand.isNotBlank()) DetailInfoRow(label = "Brand", value = teaInfo.brand)
         if (teaInfo.leafStyle.isNotBlank()) DetailInfoRow(label = "Leaf Style", value = teaInfo.leafStyle)
         if (teaInfo.processing.isNotBlank()) DetailInfoRow(label = "Processing", value = teaInfo.processing)
         if (teaInfo.grade.isNotBlank()) DetailInfoRow(label = "Grade", value = teaInfo.grade)
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
        TeaInfoDetailContent(teaInfo = mockTeaInfo)
    }
}