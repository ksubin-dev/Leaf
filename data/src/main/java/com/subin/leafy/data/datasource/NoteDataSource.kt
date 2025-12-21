package com.subin.leafy.data.datasource

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.id.NoteId

interface NoteDataSource {
    suspend fun create(note: BrewingNote): DataResourceResult<Unit>
    suspend fun read(): DataResourceResult<List<BrewingNote>>
    suspend fun update(note: BrewingNote): DataResourceResult<Unit>
    suspend fun delete(noteId: NoteId): DataResourceResult<Unit>
}