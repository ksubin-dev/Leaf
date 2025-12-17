package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun create(note: BrewingNote): Flow<DataResourceResult<Unit>>
    fun read(): Flow<DataResourceResult<List<BrewingNote>>>
    fun update(note: BrewingNote): Flow<DataResourceResult<Unit>>
    fun delete(noteId: String): Flow<DataResourceResult<Unit>>
}