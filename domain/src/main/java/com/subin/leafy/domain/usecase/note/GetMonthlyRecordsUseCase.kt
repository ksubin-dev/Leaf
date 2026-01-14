package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingRecord
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetMonthlyRecordsUseCase(private val repository: NoteRepository) {
    operator fun invoke(year: Int, month: Int): Flow<DataResourceResult<List<BrewingRecord>>> {
        return repository.getRecordsByMonth(year, month)
    }
}