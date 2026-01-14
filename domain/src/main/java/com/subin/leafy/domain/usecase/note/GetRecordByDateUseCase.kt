package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingRecord
import com.subin.leafy.domain.repository.NoteRepository
import java.time.LocalDate

class GetRecordByDateUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(date: LocalDate): DataResourceResult<BrewingRecord?> {
        return repository.getRecordByDate(date)
    }
}