package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingRecord
import com.subin.leafy.domain.repository.NoteRepository

class GetRecordByDateUseCase(private val repository: NoteRepository) {
    operator suspend fun invoke(userId: String, dateString: String): DataResourceResult<BrewingRecord?> {
        return repository.getRecordByDate(userId, dateString)
    }
}