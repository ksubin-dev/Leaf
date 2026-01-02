package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class UpdateNoteUseCase(private val repository: NoteRepository) {

    operator fun invoke(
        currentUserId: String,
        note: BrewingNote,
        localImageUris: Map<String, String?>
    ): Flow<DataResourceResult<Unit>> {

        // 1. 본인 확인 권한 검사
        if (note.ownerId != currentUserId) {
            return flowOf(DataResourceResult.Failure(Exception("본인의 게시물만 수정할 수 있습니다.")))
        }

        // 2. 차 이름 입력 검사
        if (note.teaInfo.name.isBlank()) {
            return flowOf(DataResourceResult.Failure(Exception("차 이름은 필수 항목입니다.")))
        }

        // 3. 사진 유무 검사
        val hasPhoto = localImageUris.values.any { !it.isNullOrBlank() }
        if (!hasPhoto) {
            return flowOf(DataResourceResult.Failure(Exception("최소 한 장의 사진이 필요합니다.")))
        }

        // 4. 레포지토리 호출
        return repository.update(note, localImageUris)
    }
}