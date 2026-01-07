package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingInsight
import com.subin.leafy.domain.repository.InsightAnalyzer
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetBrewingInsightsUseCase(
    private val repository: NoteRepository,
    private val analyzer: InsightAnalyzer
) {
    operator fun invoke(userId: String): Flow<List<BrewingInsight>> {
        return repository.read(userId).map { result ->
            when (result) {
                is DataResourceResult.Success -> {
                    val notes = result.data
                    if (notes.isEmpty()) return@map emptyList()
                    listOf(
                        analyzer.analyzeTimePattern(notes),
                        analyzer.analyzeTeaPreference(notes),
                        analyzer.analyzePerfectBrewing(notes),
                        analyzer.analyzeWellness(notes)
                    )
                }
                else -> emptyList()
            }
        }
    }
}