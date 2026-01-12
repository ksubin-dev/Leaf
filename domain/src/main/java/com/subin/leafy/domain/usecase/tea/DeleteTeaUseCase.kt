package com.subin.leafy.domain.usecase.tea

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.TeaRepository

class DeleteTeaUseCase(
    private val teaRepository: TeaRepository
) {
    suspend operator fun invoke(id: String): DataResourceResult<Unit> {
        if (id.isBlank()) return DataResourceResult.Failure(Exception("삭제할 대상이 없습니다."))
        return teaRepository.deleteTea(id)
    }
}