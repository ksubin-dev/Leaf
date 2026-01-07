package com.subin.leafy.data.datasource

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import kotlinx.coroutines.flow.Flow

interface NoteDataSource {
    suspend fun create(note: BrewingNote): DataResourceResult<Unit>
    fun read(userId: String): Flow<DataResourceResult<List<BrewingNote>>>
    fun getNoteById(noteId: String): Flow<DataResourceResult<BrewingNote>>
    suspend fun update(note: BrewingNote): DataResourceResult<Unit>
    suspend fun delete(id : String): DataResourceResult<Unit>
}