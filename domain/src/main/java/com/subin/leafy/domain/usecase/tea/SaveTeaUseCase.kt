package com.subin.leafy.domain.usecase.tea

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TeaItem
import com.subin.leafy.domain.repository.TeaRepository

class SaveTeaUseCase(
    private val teaRepository: TeaRepository
) {
    suspend operator fun invoke(tea: TeaItem): DataResourceResult<Unit> {

        // 1. 이름 검사
        val trimmedName = tea.name.trim()
        if (trimmedName.isBlank()) {
            return DataResourceResult.Failure(Exception("차 이름을 입력해주세요."))
        }

        if (trimmedName.length > 20) {
            return DataResourceResult.Failure(Exception("차 이름은 20자 이내로 입력해주세요."))
        }

        // 2. 정제된 데이터로 저장
        val validTea = tea.copy(
            name = trimmedName,
            brand = tea.brand.trim(),
            origin = tea.origin.trim(),
            memo = tea.memo.trim()
        )

        return teaRepository.saveTea(validTea)
    }
}