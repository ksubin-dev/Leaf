package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.BrewingRecord
import com.subin.leafy.domain.model.id.NoteId
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface NoteRepository {
    // 기존 CRUD
    fun create(note: BrewingNote): Flow<DataResourceResult<Unit>>
    fun read(): Flow<DataResourceResult<List<BrewingNote>>>
    fun update(note: BrewingNote): Flow<DataResourceResult<Unit>>
    fun delete(noteId: NoteId): Flow<DataResourceResult<Unit>>

    // 캘린더/마이페이지를 위한 확장 기능
    /** 특정 연/월의 노트들을 요약(BrewingRecord) 형태로 가져오기 */
    fun getRecordsByMonth(year: Int, month: Int): Flow<DataResourceResult<List<BrewingRecord>>>

    /** 특정 날짜의 노트 하나를 요약 형태로 가져오기 */
    suspend fun getRecordByDate(date: LocalDate): DataResourceResult<BrewingRecord?>
}