package com.leafy.features.note.ui.sections.detail

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
        title = "차 정보",
        modifier = modifier
    ) {
        if (teaInfo.name.isNotBlank()) {
            DetailInfoRow(label = "차 이름", value = teaInfo.name)
        }

        DetailInfoRow(label = "종류", value = teaInfo.type.label)

        if (teaInfo.brand.isNotBlank()) {
            DetailInfoRow(label = "브랜드 / 제조사", value = teaInfo.brand)
        }

        if (teaInfo.origin.isNotBlank()) {
            DetailInfoRow(label = "산지", value = teaInfo.origin)
        }

        if (teaInfo.leafStyle.isNotBlank()) {
            DetailInfoRow(label = "찻잎 형태", value = teaInfo.leafStyle)
        }

        if (teaInfo.grade.isNotBlank()) {
            DetailInfoRow(label = "등급", value = teaInfo.grade)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TeaInfoSectionPreview() {
    val mockTeaInfo = TeaInfo(
        name = "우전 녹차",
        brand = "오설록",
        type = TeaType.GREEN,
        origin = "제주",
        leafStyle = "잎차",
        grade = "특선"
    )
    LeafyTheme {
        TeaInfoSection(teaInfo = mockTeaInfo)
    }
}