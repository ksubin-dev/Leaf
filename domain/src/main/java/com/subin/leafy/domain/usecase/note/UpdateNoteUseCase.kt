package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.repository.NoteRepository

class UpdateNoteUseCase(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(note: BrewingNote): DataResourceResult<Unit> {

        // 1. 이미지 검사 (최소 1장)
        if (note.metadata.imageUrls.isEmpty()) {
            return DataResourceResult.Failure(Exception("노트 이미지를 최소 1장 등록해주세요."))
        }

        // 2. 차 이름 검사
        val rawTeaName = note.teaInfo.name.trim()
        if (rawTeaName.isBlank()) {
            return DataResourceResult.Failure(Exception("차 이름을 입력해주세요."))
        }

        if (rawTeaName.length > 20) {
            return DataResourceResult.Failure(Exception("차 이름은 20자 이내로 입력해주세요."))
        }

        // 3. 레시피 기본 검사 (물 온도, 시간)
        if (note.recipe.waterTemp !in 0..100) {
            return DataResourceResult.Failure(Exception("물 온도는 0~100℃ 사이여야 합니다."))
        }
        if (note.recipe.brewTimeSeconds <= 0) {
            return DataResourceResult.Failure(Exception("우림 시간은 0초보다 커야 합니다."))
        }

        // 4. 레시피 핵심 데이터 검사 (물 양, 찻잎 양)
        if (note.recipe.waterAmount <= 0) {
            return DataResourceResult.Failure(Exception("물 양은 0ml보다 커야 합니다."))
        }
        if (note.recipe.leafAmount <= 0f) {
            return DataResourceResult.Failure(Exception("찻잎 양은 0g보다 커야 합니다."))
        }

        // 5. 정제된 데이터 생성
        val validTeaInfo = note.teaInfo.copy(name = rawTeaName)
        val validNote = note.copy(teaInfo = validTeaInfo)

        return noteRepository.updateNote(validNote)
    }
}