package com.subin.leafy.data.datasource.local.impl

import android.annotation.SuppressLint
import com.subin.leafy.data.datasource.local.AnalysisDataSource
import com.subin.leafy.data.datasource.local.room.dao.NoteDao
import com.subin.leafy.data.datasource.local.room.entity.NoteEntity
import com.subin.leafy.data.util.TeaConstants
import com.subin.leafy.domain.model.UserAnalysis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class LocalAnalysisDataSourceImpl(
    private val noteDao: NoteDao
) : AnalysisDataSource {

    override fun getUserAnalysis(ownerId: String): Flow<UserAnalysis> {
        return noteDao.getAllNotes(ownerId).map { notes ->
            if (notes.isEmpty()) {
                createEmptyAnalysis()
            } else {
                calculateAnalysis(notes)
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun calculateAnalysis(notes: List<NoteEntity>): UserAnalysis {
        val totalCount = notes.size

        // 1. 이번 달 기록 수 계산
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        val monthlyCount = notes.count { note ->
            calendar.timeInMillis = note.createdAt
            calendar.get(Calendar.YEAR) == currentYear &&
                    calendar.get(Calendar.MONTH) == currentMonth
        }

        // 2. 평균 데이터 (안전하게 계산)
        val avgTemp = notes.map { it.waterTemp }.average().toInt()
        val avgTime = notes.map { it.brewTimeSeconds }.average().toInt()
        val avgRating = notes.map { it.stars }.average()

        // 3. 선호도 분석
        val favoriteType = notes.groupingBy { it.teaType }
            .eachCount()
            .maxByOrNull { it.value }?.key ?: "-"

        val typeDistribution = notes.groupingBy { it.teaType }
            .eachCount()
            .mapValues { (it.value.toDouble() / totalCount) * 100 }

        // 4. 복합 로직 계산 (스트릭, 카페인, 시간대)
        val streak = calculateStrictStreak(notes)
        val caffeineStats = calculateCaffeineStats(notes)
        val timeSlot = calculatePreferredTimeSlot(notes)

        return UserAnalysis(
            totalBrewingCount = totalCount,
            currentStreakDays = streak,
            monthlyBrewingCount = monthlyCount,

            preferredTimeSlot = timeSlot,
            averageBrewingTime = formatTime(avgTime),
            preferredTemperature = avgTemp,
            preferredBrewingSeconds = avgTime,

            weeklyCount = caffeineStats.weeklyCount,
            dailyCaffeineAvg = caffeineStats.dailyAvg,
            weeklyCaffeineTrend = caffeineStats.weeklyTrend, // [중요] 차트용 실제 데이터

            averageRating = String.format("%.1f", avgRating).toDouble(),
            favoriteTeaType = favoriteType,
            teaTypeDistribution = typeDistribution
        )
    }

    // --- Helper Logic ---

    // [스트릭] 자정 기준 연속 기록 계산
    private fun calculateStrictStreak(notes: List<NoteEntity>): Int {
        if (notes.isEmpty()) return 0

        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        // 날짜만 뽑아서 중복 제거 후 최신순 정렬
        val sortedDates = notes
            .map { sdf.format(Date(it.createdAt)) }
            .distinct()
            .sortedDescending()

        if (sortedDates.isEmpty()) return 0

        val today = sdf.format(Date())
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = sdf.format(calendar.time)

        val latestDate = sortedDates[0]

        // 오늘이나 어제 기록이 없으면 스트릭은 이미 깨짐
        if (latestDate != today && latestDate != yesterday) return 0

        var streak = 1
        var checkDateCal = Calendar.getInstance()

        // 최신 기록이 어제라면, 기준을 어제로 잡고 그 전날부터 체크
        if (latestDate == yesterday) {
            checkDateCal.add(Calendar.DAY_OF_YEAR, -1)
        }

        // 2번째 데이터부터 과거로 가며 연속성 확인
        for (i in 1 until sortedDates.size) {
            checkDateCal.add(Calendar.DAY_OF_YEAR, -1) // 하루 전으로 이동
            val expectedDate = sdf.format(checkDateCal.time)

            if (sortedDates[i] == expectedDate) {
                streak++
            } else {
                break // 연속 끊김
            }
        }
        return streak
    }

    // [카페인] 주간 통계 및 추세 (차트용)
    private data class CaffeineStats(val weeklyCount: Int, val dailyAvg: Int, val weeklyTrend: List<Int>)

    private fun calculateCaffeineStats(notes: List<NoteEntity>): CaffeineStats {
        val calendar = Calendar.getInstance()

        // 1. 최근 7일 (오늘 포함) 데이터 생성 로직
        // 결과: [6일전 합계, 5일전 합계, ... , 어제 합계, 오늘 합계] (총 7개)
        val trendList = IntArray(7) { 0 }
        var weeklyTotalCaffeine = 0
        var weeklyNoteCount = 0

        // 날짜 비교를 위해 정규화 ("yyyyMMdd")
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        // D-6 부터 D-0(오늘)까지 날짜 문자열 미리 생성
        val targetDates = Array(7) { "" }
        val tempCal = Calendar.getInstance()
        tempCal.add(Calendar.DAY_OF_YEAR, -6) // 6일 전부터 시작

        for (i in 0..6) {
            targetDates[i] = sdf.format(tempCal.time)
            tempCal.add(Calendar.DAY_OF_YEAR, 1)
        }

        // 노트 순회하며 카페인 합산
        notes.forEach { note ->
            val noteDate = sdf.format(Date(note.createdAt))
            // 이 노트가 최근 7일 안에 있는가?
            val index = targetDates.indexOf(noteDate)

            if (index != -1) {
                val caffeine = TeaConstants.getCaffeineAmount(note.teaType)
                trendList[index] += caffeine // 해당 요일에 누적
                weeklyTotalCaffeine += caffeine
                weeklyNoteCount++
            }
        }

        val dailyAvg = if (weeklyNoteCount > 0) weeklyTotalCaffeine / 7 else 0 // 7일 평균

        return CaffeineStats(
            weeklyCount = weeklyNoteCount,
            dailyAvg = dailyAvg,
            weeklyTrend = trendList.toList() // [D-6, ... , Today]
        )
    }

    // [시간대] 선호 시간 계산
    private fun calculatePreferredTimeSlot(notes: List<NoteEntity>): String {
        val hourCounts = IntArray(24)
        val cal = Calendar.getInstance()

        notes.forEach {
            cal.timeInMillis = it.createdAt
            hourCounts[cal.get(Calendar.HOUR_OF_DAY)]++
        }

        val maxHour = hourCounts.indices.maxByOrNull { hourCounts[it] } ?: 0
        // 데이터가 아예 없을 때 예외처리
        if (notes.isEmpty()) return "-"

        return when(maxHour) {
            in 5..11 -> "상쾌한 오전"
            in 12..17 -> "나른한 오후"
            in 18..22 -> "차분한 저녁"
            else -> "조용한 새벽"
        }
    }

    private fun formatTime(seconds: Int): String {
        return if (seconds < 60) "${seconds}초" else "${seconds / 60}분 ${seconds % 60}초"
    }

    private fun createEmptyAnalysis() = UserAnalysis(
        0, 0, 0, "-", "0초", 0, 0,
        0, 0, listOf(0,0,0,0,0,0,0), 0.0, null, emptyMap()
    )
}