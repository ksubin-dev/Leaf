package com.subin.leafy.domain.usecase.tea

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TeaItem
import com.subin.leafy.domain.repository.TeaRepository

class GetTeaDetailUseCase(
    private val teaRepository: TeaRepository
) {
    suspend operator fun invoke(id: String): DataResourceResult<TeaItem> {
        if (id.isBlank()) return DataResourceResult.Failure(Exception("잘못된 ID입니다."))

        val tea = teaRepository.getTeaDetail(id)
        return if (tea != null) {
            DataResourceResult.Success(tea)
        } else {
            DataResourceResult.Failure(Exception("해당 차를 찾을 수 없습니다."))
        }
    }
}