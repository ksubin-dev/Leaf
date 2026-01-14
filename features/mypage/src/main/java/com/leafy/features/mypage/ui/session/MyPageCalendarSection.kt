package com.leafy.features.mypage.ui.session
//
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import com.leafy.features.mypage.ui.MyPageUiState
//import com.leafy.features.mypage.ui.component.BrewingCalendar
//import com.leafy.features.mypage.ui.component.BrewingRecordCard
//import com.leafy.features.mypage.ui.component.BrewingRecordEmptyCard
//import com.leafy.features.mypage.ui.component.StatsCard
//
//@Composable
//fun MyPageCalendarSection(
//    uiState: MyPageUiState,
//    onDateClick: (Int) -> Unit,
//    onPrevMonth: () -> Unit,
//    onNextMonth: () -> Unit,
//    onAddClick: () -> Unit,
//    onEditClick: (String) -> Unit,
//    onDetailClick: (String) -> Unit,
//    onViewAllClick: (String) -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 16.dp)
//    ) {
//        BrewingCalendar(
//            currentMonth = uiState.currentMonth,
//            selectedDay = uiState.selectedDay,
//            recordedDays = uiState.recordedDays,
//            onDateClick = onDateClick,
//            onPrevMonth = onPrevMonth,
//            onNextMonth = onNextMonth
//        ) {
//            Column(modifier = Modifier.padding(top = 10.dp)) {
//                if (uiState.selectedRecord != null) {
//                    BrewingRecordCard(
//                        imageUrl = uiState.selectedRecord.imageUrl,
//                        teaName = uiState.selectedRecord.teaName,
//                        metaInfo = uiState.selectedRecord.metaInfo,
//                        rating = uiState.selectedRecord.rating,
//                        onEditClick = { onEditClick(uiState.selectedRecord.id) },
//                        onCardClick = { onDetailClick(uiState.selectedRecord.id) }
//                    )
//
//                    val dailyRecords = uiState.monthlyRecords.filter {
//                        it.dateString == uiState.selectedDateString
//                    }
//
//                    if (dailyRecords.size > 1) {
//                        Spacer(modifier = Modifier.height(12.dp))
//                        OutlinedButton(
//                            onClick = { onViewAllClick(uiState.selectedDateString) },
//                            modifier = Modifier.fillMaxWidth().height(52.dp),
//                            shape = RoundedCornerShape(16.dp),
//                            border = BorderStroke(
//                                1.dp,
//                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
//                            )
//                        ) {
//                            Text(
//                                text = "오늘의 모든 기록 보기 (${dailyRecords.size}) →",
//                                style = MaterialTheme.typography.labelLarge,
//                                color = MaterialTheme.colorScheme.primary,
//                                fontWeight = FontWeight.Bold
//                            )
//                        }
//                    }
//                } else {
//                    BrewingRecordEmptyCard(onAddClick = onAddClick)
//                }
//                Spacer(modifier = Modifier.height(24.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    val stats = uiState.userStats
//
//                    StatsCard(
//                        modifier = Modifier.weight(1f),
//                        label = "총 우림",
//                        value = "${stats?.totalBrewingCount ?: 0}",
//                        unit = "회"
//                    )
//                    StatsCard(
//                        modifier = Modifier.weight(1f),
//                        label = "연속 스트릭",
//                        value = "${stats?.currentStreak ?: 0}",
//                        unit = "일"
//                    )
//                    StatsCard(
//                        modifier = Modifier.weight(1f),
//                        label = "이번 달",
//                        value = "${stats?.monthlyBrewingCount ?: 0}",
//                        unit = "회"
//                    )
//                }
//            }
//        }
//    }
//}