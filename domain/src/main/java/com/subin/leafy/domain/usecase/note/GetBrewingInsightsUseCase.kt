package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.common.mapData
import com.subin.leafy.domain.model.BrewingInsight
import com.subin.leafy.domain.repository.InsightAnalyzer
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetBrewingInsightsUseCase(
    private val noteRepository: NoteRepository,
    private val analyzer: InsightAnalyzer
) {
    /**
     * 특정 사용자의 데이터를 기반으로 4가지 핵심 인사이트 리스트를 반환합니다.
     */
    operator fun invoke(userId: String): Flow<DataResourceResult<List<BrewingInsight>>> {
        return noteRepository.read(userId).map { result ->
            result.mapData { notes ->
                // 데이터가 성공적으로 로드되었을 때만 분석 실행
                if (notes.isEmpty()) {
                    emptyList()
                } else {
                    listOf(
                        analyzer.analyzeTimePattern(notes),
                        analyzer.analyzeTeaPreference(notes),
                        analyzer.analyzePerfectBrewing(notes),
                        analyzer.analyzeWellness(notes)
                    )
                }
            }
        }
    }
}