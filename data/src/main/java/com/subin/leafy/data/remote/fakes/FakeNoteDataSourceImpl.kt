package com.subin.leafy.data.remote.fakes

import android.os.Build
import androidx.annotation.RequiresApi
import com.subin.leafy.data.datasource.NoteDataSource
import com.subin.leafy.data.mapper.toDTO
import com.subin.leafy.data.mapper.toDomainNote
import com.subin.leafy.data.model.dto.BrewingNoteDTO
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BodyType
import com.subin.leafy.domain.model.BrewingCondition
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.NoteContext
import com.subin.leafy.domain.model.RatingInfo
import com.subin.leafy.domain.model.SensoryEvaluation
import com.subin.leafy.domain.model.TeaInfo
import com.subin.leafy.domain.model.WeatherType

@RequiresApi(Build.VERSION_CODES.O)
class FakeNoteDataSourceImpl : NoteDataSource {
    private val db = mutableListOf<BrewingNoteDTO>()

    init {
        // 상세 페이지 테스트를 위한 풍성한 가짜 데이터 추가
        val sampleNote = BrewingNote(
            id = "sample-123", // 테스트용 고정 ID
            ownerId = "user-1",
            teaInfo = TeaInfo(
                name = "아리산 고산우롱",
                brand = "왕덕전 (Wang De Chuan)",
                type = "Oolong Tea",
                leafStyle = "Loose Leaf",
                processing = "Lightly Roasted",
                grade = "Premium"
            ),
            condition = BrewingCondition(
                waterTemp = "95°C",
                leafAmount = "5g",
                brewTime = "1:00 / 1:15 / 1:30",
                brewCount = "3",
                teaware = "백자 개완 (White Porcelain Gaiwan)"
            ),
            evaluation = SensoryEvaluation(
                selectedTags = setOf("Floral", "Creamy", "Sweet", "Green Apple"),
                sweetness = 8f,
                sourness = 2f,
                bitterness = 1f,
                saltiness = 0f,
                umami = 4f,
                bodyType = BodyType.MEDIUM,
                finishLevel = 0.2f,
                memo = "첫 우림에서의 난꽃향이 매우 인상적입니다. 입안에 남는 은은한 우유 같은 질감이 부드럽고, 끝맛이 매우 깔끔하게 떨어집니다."
            ),
            ratingInfo = RatingInfo(
                stars = 5,
                purchaseAgain = true
            ),
            context = NoteContext(
                dateTime = "2024-11-20 14:30",
                weather = WeatherType.CLEAR,
                withPeople = "Solo",
                dryLeafUri = null
            )
        )

        db.add(sampleNote.toDTO())
    }

    override suspend fun read(): DataResourceResult<List<BrewingNote>> = runCatching {
        // DTO 리스트를 도메인 모델 리스트로 변환하여 반환
        val domainNotes = db.map { it.toDomainNote() }
        DataResourceResult.Success(domainNotes)
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun create(note: BrewingNote): DataResourceResult<Unit> = runCatching {
        db.add(note.toDTO())
        DataResourceResult.Success(Unit)
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun update(note: BrewingNote): DataResourceResult<Unit> = runCatching {
        val index = db.indexOfFirst { it._id == note.id }
        if (index != -1) {
            db[index] = note.toDTO()
            DataResourceResult.Success(Unit)
        } else {
            DataResourceResult.Failure(Exception("Note not found"))
        }
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun delete(id : String): DataResourceResult<Unit> = runCatching {
        db.removeIf { it._id == id }
        DataResourceResult.Success(Unit)
    }.getOrElse { DataResourceResult.Failure(it) }
}