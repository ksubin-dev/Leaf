package com.subin.leafy.data.repository

import com.subin.leafy.domain.model.*
import com.subin.leafy.domain.model.id.NoteId
import com.subin.leafy.domain.model.id.UserId
import java.util.*

object MockDataSource {

    private val mockUserId = UserId("mock_user_leafy")

    fun createMockNotes(): List<BrewingNote> {
        return listOf(
            createNote("1", "아리산 고산우롱", 12, 1, 5, "95°C", "3분"),
            createNote("2", "백호은침", 12, 3, 4, "85°C", "2분 30초"),
            createNote("3", "문산포종", 12, 5, 5, "90°C", "2분"),
            createNote("4", "일월담 홍차", 12, 10, 3, "100°C", "3분 30초")
        )
    }

    private fun createNote(
        rawId: String,
        name: String,
        month: Int,
        day: Int,
        stars: Int,
        temp: String,
        time: String
    ): BrewingNote {
        val calendar = Calendar.getInstance().apply {
            set(2025, month - 1, day, 14, 0)
        }

        return BrewingNote(
            id = NoteId(rawId),
            ownerId = mockUserId,
            teaInfo = TeaInfo(
                name = name,
                brand = "Leafy Tea",
                type = "Oolong",
                leafStyle = "Loose",
                processing = "Whole",
                grade = "Premium"
            ),
            condition = BrewingCondition(
                waterTemp = temp,
                leafAmount = "5g",
                brewTime = time,
                brewCount = "1",
                teaware = "Gaiwan"
            ),
            evaluation = SensoryEvaluation(
                sweetness = 4,
                bodyType = BodyType.MEDIUM
            ),
            ratingInfo = RatingInfo(
                stars = stars,
                purchaseAgain = true
            ),
            context = NoteContext(
                weather = WeatherType.CLEAR
            ),
            createdAt = calendar.time
        )
    }
}