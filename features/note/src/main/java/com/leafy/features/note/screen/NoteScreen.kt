package com.leafy.features.note.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.NotePhotoSlot
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyGreen
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.ui.theme.LeafyWhite

/**
 * New Brewing Note 입력 화면
 * - 상단 AppBar
 * - Photos 섹션 (Dry Leaf / Tea Liquor / Teaware / Additional)
 * - 나머지 섹션(Basic Info, Tasting Context, Sensory...)는 이후 단계에서 추가 예정
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NoteScreen() {
    LeafyTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "New Brewing Note",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { /* TODO: navController.navigateUp() */ }) {
                            Icon(
                                painter = painterResource(id = SharedR.drawable.ic_back),
                                contentDescription = "Back",
                                modifier = Modifier
                                    .height(20.dp),
                                tint = LeafyGreen
                            )
                        }
                    },
                    actions = {
                        TextButton(onClick = { /* TODO: Save action */ }) {
                            Text(
                                text = "Save",
                                color = LeafyGreen,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LeafyWhite)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                // ---------------- Photos 섹션 ----------------
                PhotosSection(
                    onClickDryLeaf = { /* TODO: 사진 선택/촬영 */ },
                    onClickTeaLiquor = { /* TODO */ },
                    onClickTeaware = { /* TODO */ },
                    onClickAdditional = { /* TODO */ }
                )

                // 이 아래부터 Basic Tea Information, Tasting Context 등
                // 섹션을 차례대로 추가해 나가면 됨.
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * 상단 "Photos" 섹션
 * - 타이틀 + 2x2 Photo 슬롯 그리드
 */
@Composable
private fun PhotosSection(
    onClickDryLeaf: () -> Unit,
    onClickTeaLiquor: () -> Unit,
    onClickTeaware: () -> Unit,
    onClickAdditional: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        // 섹션 타이틀 (아이콘 + 텍스트)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = SharedR.drawable.ic_note_section_photos),
                contentDescription = "Photos",
                tint = LeafyGreen,
                modifier = Modifier
                    .height(18.dp)
                    .padding(end = 6.dp)
            )
            Text(
                text = "Photos",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFF303437)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2 x 2 그리드
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NotePhotoSlot(
                    title = "Dry Leaf",
                    iconRes = SharedR.drawable.ic_note_photo_dry_leaf,
                    onClick = onClickDryLeaf,
                    modifier = Modifier.weight(1f)
                )
                NotePhotoSlot(
                    title = "Tea Liquor",
                    iconRes = SharedR.drawable.ic_note_photo_tea_liquor,
                    onClick = onClickTeaLiquor,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NotePhotoSlot(
                    title = "Teaware",
                    iconRes = SharedR.drawable.ic_note_photo_teaware,
                    onClick = onClickTeaware,
                    modifier = Modifier.weight(1f)
                )
                NotePhotoSlot(
                    title = "Additional",
                    iconRes = SharedR.drawable.ic_note_photo_add,
                    onClick = onClickAdditional,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}