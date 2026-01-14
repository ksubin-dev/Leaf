package com.subin.leafy.domain.usecase.tea

import com.subin.leafy.domain.model.TeaItem
import com.subin.leafy.domain.repository.TeaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SearchTeasUseCase(
    private val teaRepository: TeaRepository
) {
    operator fun invoke(query: String): Flow<List<TeaItem>> {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) return flowOf(emptyList())

        return teaRepository.searchTeas(trimmedQuery)
    }
}