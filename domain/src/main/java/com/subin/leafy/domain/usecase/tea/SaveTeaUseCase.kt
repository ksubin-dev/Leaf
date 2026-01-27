package com.subin.leafy.domain.usecase.tea

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TeaItem
import com.subin.leafy.domain.repository.TeaRepository
import javax.inject.Inject

class SaveTeaUseCase @Inject constructor(
    private val teaRepository: TeaRepository
) {
    suspend operator fun invoke(tea: TeaItem): DataResourceResult<Unit> {

        val trimmedName = tea.name.trim()
        if (trimmedName.isBlank()) {
            return DataResourceResult.Failure(Exception("차 이름을 입력해주세요."))
        }

        if (trimmedName.length > 20) {
            return DataResourceResult.Failure(Exception("차 이름은 20자 이내로 입력해주세요."))
        }

        val validTea = tea.copy(
            name = trimmedName,
            brand = tea.brand.trim(),
            origin = tea.origin.trim(),
            memo = tea.memo.trim()
        )

        return teaRepository.saveTea(validTea)
    }
}