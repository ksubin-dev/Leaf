package com.subin.leafy.domain.usecase.tea

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.TeaRepository
import javax.inject.Inject

class ToggleFavoriteTeaUseCase @Inject constructor(
    private val teaRepository: TeaRepository
) {
    suspend operator fun invoke(teaId: String): DataResourceResult<Unit> {
        if (teaId.isBlank()) return DataResourceResult.Failure(Exception("잘못된 ID입니다."))
        return teaRepository.toggleFavorite(teaId)
    }
}