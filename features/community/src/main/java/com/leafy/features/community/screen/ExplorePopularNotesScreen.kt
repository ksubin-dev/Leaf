package com.leafy.features.community.screen

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.leafy.features.community.ui.component.ExploreNoteUi

@Composable
fun ExplorePopularNotesScreen(
    notes: List<ExploreNoteUi>,
    onBackClick: () -> Unit,
    onNoteClick: (String) -> Unit
) {
//    Scaffold(
//        topBar = {
//            LeafyTopBar( // shared 공통 탑바 가정
//                title = "이번 주 인기 노트",
//                onBackClick = onBackClick,
//                actions = {
//                    IconButton(onClick = { /* 검색 */ }) {
//                        Icon(painterResource(id = SharedR.drawable.ic_search), null)
//                    }
//                    IconButton(onClick = { /* 필터 */ }) {
//                        Icon(painterResource(id = SharedR.drawable.ic_filter), null)
//                    }
//                }
//            )
//        }
//    ) { padding ->
//        Column(modifier = Modifier.padding(padding)) {
//            // 🎯 카테고리 필터 칩 (녹차, 홍차, 우롱차 등)
//            CategoryFilterChips()
//
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(2),
//                contentPadding = PaddingValues(16.dp),
//                horizontalArrangement = Arrangement.spacedBy(12.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp),
//                modifier = Modifier.fillMaxSize()
//            ) {
//                items(notes) { note ->
//                    // 🎯 Grid 전용 카드 컴포넌트 (이미지 강조형)
//                    ExploreGridNoteCard(
//                        note = note,
//                        onClick = { onNoteClick(note.id) }
//                    )
//                }
//            }
//        }
//    }
}