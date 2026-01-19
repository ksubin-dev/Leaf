package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getMyNotesFlow(): Flow<List<BrewingNote>>
    fun getNotesByMonthFlow(year: Int, month: Int): Flow<List<BrewingNote>>
    fun searchMyNotes(query: String): Flow<List<BrewingNote>>

    suspend fun getNoteDetail(noteId: String): DataResourceResult<BrewingNote>

    suspend fun deleteNote(noteId: String): DataResourceResult<Unit>

    suspend fun saveNote(note: BrewingNote): DataResourceResult<Unit>
    suspend fun updateNote(note: BrewingNote): DataResourceResult<Unit>
    suspend fun getUserNotes(userId: String): DataResourceResult<List<BrewingNote>>
    suspend fun syncNotes(): DataResourceResult<Unit>
    suspend fun clearLocalCache(): DataResourceResult<Unit>
}