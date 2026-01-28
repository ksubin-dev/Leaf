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
import javax.inject.Inject

class LocalAnalysisDataSourceImpl @Inject constructor(
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

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        val monthlyCount = notes.count { note ->
            calendar.timeInMillis = note.createdAt
            calendar.get(Calendar.YEAR) == currentYear &&
                    calendar.get(Calendar.MONTH) == currentMonth
        }

        val avgTemp = notes.map { it.waterTemp }.average().toInt()
        val avgTime = notes.map { it.brewTimeSeconds }.average().toInt()
        val avgRating = notes.map { it.stars }.average()

        val favoriteType = notes.groupingBy { it.teaType }
            .eachCount()
            .maxByOrNull { it.value }?.key ?: "-"

        val typeDistribution = notes.groupingBy { it.teaType }
            .eachCount()
            .mapValues { (it.value.toDouble() / totalCount) * 100 }

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


    private fun calculateStrictStreak(notes: List<NoteEntity>): Int {
        if (notes.isEmpty()) return 0

        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
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

        if (latestDate != today && latestDate != yesterday) return 0

        var streak = 1
        var checkDateCal = Calendar.getInstance()

        if (latestDate == yesterday) {
            checkDateCal.add(Calendar.DAY_OF_YEAR, -1)
        }

        for (i in 1 until sortedDates.size) {
            checkDateCal.add(Calendar.DAY_OF_YEAR, -1)
            val expectedDate = sdf.format(checkDateCal.time)

            if (sortedDates[i] == expectedDate) {
                streak++
            } else {
                break
            }
        }
        return streak
    }

    private data class CaffeineStats(val weeklyCount: Int, val dailyAvg: Int, val weeklyTrend: List<Int>)

    private fun calculateCaffeineStats(notes: List<NoteEntity>): CaffeineStats {
        val calendar = Calendar.getInstance()

        val trendList = IntArray(7) { 0 }
        var weeklyTotalCaffeine = 0
        var weeklyNoteCount = 0

        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        val targetDates = Array(7) { "" }
        val tempCal = Calendar.getInstance()
        tempCal.add(Calendar.DAY_OF_YEAR, -6)

        for (i in 0..6) {
            targetDates[i] = sdf.format(tempCal.time)
            tempCal.add(Calendar.DAY_OF_YEAR, 1)
        }

        notes.forEach { note ->
            val noteDate = sdf.format(Date(note.createdAt))
            val index = targetDates.indexOf(noteDate)

            if (index != -1) {
                val caffeine = TeaConstants.getCaffeineAmount(note.teaType)
                trendList[index] += caffeine
                weeklyTotalCaffeine += caffeine
                weeklyNoteCount++
            }
        }

        val dailyAvg = if (weeklyNoteCount > 0) weeklyTotalCaffeine / 7 else 0

        return CaffeineStats(
            weeklyCount = weeklyNoteCount,
            dailyAvg = dailyAvg,
            weeklyTrend = trendList.toList()
        )
    }


    private fun calculatePreferredTimeSlot(notes: List<NoteEntity>): String {
        val hourCounts = IntArray(24)
        val cal = Calendar.getInstance()

        notes.forEach {
            cal.timeInMillis = it.createdAt
            hourCounts[cal.get(Calendar.HOUR_OF_DAY)]++
        }

        val maxHour = hourCounts.indices.maxByOrNull { hourCounts[it] } ?: 0
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