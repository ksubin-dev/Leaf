package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.BrewingRecord
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    // --- [1. 나의 기록 CRUD] ---
    fun create(note: BrewingNote, localImageUris: Map<String, String?>): Flow<DataResourceResult<Unit>>
    fun read(userId: String): Flow<DataResourceResult<List<BrewingNote>>>
    fun update(note: BrewingNote, localImageUris: Map<String, String?>): Flow<DataResourceResult<Unit>>
    fun delete(id: String): Flow<DataResourceResult<Unit>>


    // --- [2. 상세 및 캘린더 조회] ---
    fun getNoteById(noteId: String): Flow<DataResourceResult<BrewingNote>>
    fun getRecordsByMonth(userId: String, year: Int, month: Int): Flow<DataResourceResult<List<BrewingRecord>>>
    suspend fun getRecordByDate(userId: String, dateString: String): DataResourceResult<BrewingRecord?>
}