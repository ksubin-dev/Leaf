package com.leafy.features.note.ui.detail.sections

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.leafy.features.note.ui.common.DetailInfoRow
import com.leafy.features.note.ui.common.DetailSectionCard
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.TeaInfo
import com.subin.leafy.domain.model.TeaType

@Composable
fun TeaInfoSection(
    teaInfo: TeaInfo,
    modifier: Modifier = Modifier
) {
    DetailSectionCard(
        title = "Tea Information",
        modifier = modifier
    ) {
        // 값이 있는 경우에만 행을 표시 (Blank 체크)
        if (teaInfo.name.isNotBlank()) {
            DetailInfoRow(label = "Tea Name", value = teaInfo.name)
        }

        // TeaType은 Enum이므로 label 사용 (예: "홍차")
        DetailInfoRow(label = "Type", value = teaInfo.type.label)

        if (teaInfo.brand.isNotBlank()) {
            DetailInfoRow(label = "Brand", value = teaInfo.brand)
        }

        if (teaInfo.leafStyle.isNotBlank()) {
            DetailInfoRow(label = "Leaf Style", value = teaInfo.leafStyle)
        }

        // 도메인 모델에 origin이 있다면
        if (teaInfo.origin.isNotBlank()) {
            DetailInfoRow(label = "Origin", value = teaInfo.origin)
        }

        if (teaInfo.grade.isNotBlank()) {
            DetailInfoRow(label = "Grade", value = teaInfo.grade)
        }
    }
}

// ----------------------------------------------------------------------
// Preview
// ----------------------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun TeaInfoSectionPreview() {
    val mockTeaInfo = TeaInfo(
        name = "Earl Grey Supreme",
        brand = "Twinings",
        type = TeaType.BLACK,
        leafStyle = "Loose Leaf",
        origin = "China",
        grade = "FTGFOP"
    )
    LeafyTheme {
        TeaInfoSection(teaInfo = mockTeaInfo)
    }
}