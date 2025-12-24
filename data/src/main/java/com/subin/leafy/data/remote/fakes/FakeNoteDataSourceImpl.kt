package com.subin.leafy.data.remote.fakes

import android.os.Build
import androidx.annotation.RequiresApi
import com.leafy.shared.ui.utils.TimeUtils
import com.subin.leafy.data.datasource.NoteDataSource
import com.subin.leafy.data.mapper.toDTO
import com.subin.leafy.data.mapper.toDomainNote
import com.subin.leafy.data.model.dto.BrewingNoteDTO
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote

@RequiresApi(Build.VERSION_CODES.O)
class FakeNoteDataSourceImpl : NoteDataSource { //여기가 나중에 FirebaseNoteDateSourceImpl로 바뀜
    // 실제 DB처럼 DTO 리스트로 관리합니다.
    private val db = mutableListOf<BrewingNoteDTO>()

    init {
        // 초기 가짜 데이터 삽입 (Long 타입 타임스탬프 사용)
        db.add(BrewingNoteDTO(_id = "1", teaName = "아리산 고산우롱", stars = 5, createdAt = TimeUtils.getCurrentTimestamp()))
        db.add(BrewingNoteDTO(_id = "2", teaName = "백호은침", stars = 4, createdAt = TimeUtils.getCurrentTimestamp()))
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