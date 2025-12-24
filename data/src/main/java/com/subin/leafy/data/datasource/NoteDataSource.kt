package com.subin.leafy.data.datasource

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote

interface NoteDataSource {
    suspend fun create(note: BrewingNote): DataResourceResult<Unit>
    suspend fun read(): DataResourceResult<List<BrewingNote>>
    suspend fun update(note: BrewingNote): DataResourceResult<Unit>
    suspend fun delete(id : String): DataResourceResult<Unit>
}