package com.subin.leafy.data.repository

import android.annotation.SuppressLint
import com.subin.leafy.data.datasource.remote.NoteDataSource
import com.subin.leafy.data.datasource.remote.UserDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.common.mapData
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.UserStats
import com.subin.leafy.domain.repository.UserStatsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.time.LocalDate

class UserStatsRepositoryImpl(
    private val userDataSource: UserDataSource,
    private val noteDataSource: NoteDataSource
) : UserStatsRepository {

    override fun getUserStats(userId: String): Flow<DataResourceResult<UserStats>> =
        noteDataSource.read(userId).map { result ->
            result.mapData { notes ->
                calculateStats(notes)
            }
        }.onStart {
            emit(DataResourceResult.Loading)
        }.catch { e ->
            emit(DataResourceResult.Failure(e))
        }.flowOn(Dispatchers.IO)

    @SuppressLint("DefaultLocale")
    private fun calculateStats(notes: List<BrewingNote>): UserStats {
        val now = LocalDate.now()
        val currentMonthPrefix = "${now.year}-${String.format("%02d", now.monthValue)}"

        return UserStats(
            totalBrewingCount = notes.size,
            monthlyBrewingCount = notes.count { it.context.dateTime.startsWith(currentMonthPrefix) },
            currentStreak = calculateStreak(notes.map { it.context.dateTime }),
            // 나머지 데이터는 나중에 추가 구현
            preferredTimeSlot = "-",
            averageBrewingTime = "0:00",
            weeklyBrewingCount = 0,
            averageRating = notes.map { it.ratingInfo.stars }.average().takeIf { !it.isNaN() } ?: 0.0,
            myTeaChestCount = 0,
            wishlistCount = 0
        )
    }

    private fun calculateStreak(dates: List<String>): Int {
        if (dates.isEmpty()) return 0

        val sortedDates = dates.filter { it.isNotEmpty() }
            .distinct()
            .sortedDescending()

        var streak = 0
        var targetDate = LocalDate.now()

        if (sortedDates.first() != targetDate.toString()) {
            targetDate = targetDate.minusDays(1)
            if (sortedDates.first() != targetDate.toString()) return 0
        }

        for (dateStr in sortedDates) {
            if (dateStr == targetDate.toString()) {
                streak++
                targetDate = targetDate.minusDays(1)
            } else {
                break
            }
        }
        return streak
    }
}