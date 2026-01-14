package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TeaItem
import kotlinx.coroutines.flow.Flow

interface TeaRepository {

    // --- 1. 조회 (Local First) ---
    // 내 차 목록 (실시간)
    fun getTeasFlow(): Flow<List<TeaItem>>

    // 검색 (이름, 브랜드)
    fun searchTeas(query: String): Flow<List<TeaItem>>

    // 내가 가진 차 개수 (통계용)
    fun getTeaCountFlow(): Flow<Int>

    // 상세 조회
    suspend fun getTeaDetail(id: String): TeaItem?


    // --- 2. 관리 (Local + Remote Backup) ---
    // 저장 (추가/수정 겸용)
    suspend fun saveTea(tea: TeaItem): DataResourceResult<Unit>

    // 삭제
    suspend fun deleteTea(id: String): DataResourceResult<Unit>

    // 즐겨찾기 토글 (편의 기능)
    suspend fun toggleFavorite(teaId: String): DataResourceResult<Unit>


    // --- 3. 동기화 ---
    // 로그인 시 서버 백업본을 로컬로 가져오기
    suspend fun syncTeas(): DataResourceResult<Unit>
}