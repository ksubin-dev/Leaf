package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.repository.NoteRepository
import javax.inject.Inject

class SaveNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(note: BrewingNote): DataResourceResult<Unit> {

        if (note.metadata.imageUrls.isEmpty()) {
            return DataResourceResult.Failure(Exception("노트 이미지를 최소 1장 등록해주세요."))
        }

        val rawTeaName = note.teaInfo.name.trim()
        if (rawTeaName.isBlank()) {
            return DataResourceResult.Failure(Exception("차 이름을 입력해주세요."))
        }

        if (rawTeaName.length > 20) {
            return DataResourceResult.Failure(Exception("차 이름은 20자 이내로 입력해주세요."))
        }

        if (note.recipe.waterTemp !in 0..100) {
            return DataResourceResult.Failure(Exception("물 온도는 0~100℃ 사이여야 합니다."))
        }
        if (note.recipe.brewTimeSeconds <= 0) {
            return DataResourceResult.Failure(Exception("우림 시간은 0초보다 커야 합니다."))
        }

        if (note.recipe.waterAmount <= 0) {
            return DataResourceResult.Failure(Exception("물 양은 0ml보다 커야 합니다."))
        }
        if (note.recipe.leafAmount <= 0f) {
            return DataResourceResult.Failure(Exception("찻잎 양은 0g보다 커야 합니다."))
        }

        val validTeaInfo = note.teaInfo.copy(name = rawTeaName)
        val validNote = note.copy(teaInfo = validTeaInfo)

        return noteRepository.saveNote(validNote)
    }
}