package com.subin.leafy.domain.usecase.note


import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class UpdateNoteUseCase(private val repository: NoteRepository) {
    operator fun invoke(note: BrewingNote): Flow<DataResourceResult<Unit>> {
        if (note.teaInfo.name.isBlank()) {
            return flowOf(DataResourceResult.Failure(Exception("차 이름은 필수 항목입니다.")))
        }

        val hasPhoto = listOf(
            note.context.dryLeafUri,
            note.context.liquorUri,
            note.context.teawareUri,
            note.context.additionalUri
        ).any { !it.isNullOrBlank() }

        if (!hasPhoto) {
            return flowOf(DataResourceResult.Failure(Exception("최소 한 장의 사진이 필요합니다.")))
        }

        return repository.update(note)
    }
}