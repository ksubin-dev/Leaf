package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.BrewingRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface NoteRepository {
    // 기존 CRUD
    fun create(note: BrewingNote): Flow<DataResourceResult<Unit>>
    fun read(): Flow<DataResourceResult<List<BrewingNote>>>
    fun update(note: BrewingNote): Flow<DataResourceResult<Unit>>
    fun delete(id: String): Flow<DataResourceResult<Unit>>

    fun getNoteById(noteId: String): Flow<DataResourceResult<BrewingNote>>
    fun getRecordsByMonth(year: Int, month: Int): Flow<DataResourceResult<List<BrewingRecord>>>

    suspend fun getRecordByDate(dateString: String): DataResourceResult<BrewingRecord?>
}