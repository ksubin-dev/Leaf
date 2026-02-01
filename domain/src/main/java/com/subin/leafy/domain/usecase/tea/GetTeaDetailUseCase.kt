package com.subin.leafy.domain.usecase.tea

import com.subin.leafy.domain.model.TeaItem
import com.subin.leafy.domain.repository.TeaRepository
import javax.inject.Inject

class GetTeaDetailUseCase @Inject constructor(
    private val teaRepository: TeaRepository
) {
    suspend operator fun invoke(id: String): TeaItem? {
        if (id.isBlank()) return null

        return teaRepository.getTeaDetail(id)
    }
}