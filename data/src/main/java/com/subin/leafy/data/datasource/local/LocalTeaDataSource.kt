package com.subin.leafy.data.datasource.local

import com.subin.leafy.domain.model.TeaItem
import kotlinx.coroutines.flow.Flow

interface LocalTeaDataSource {

    // --- 조회 (Read) ---
    // 내 차 보관함 전체 목록
    fun getTeasFlow(): Flow<List<TeaItem>>

    // 검색 (이름, 브랜드)
    fun searchTeas(query: String): Flow<List<TeaItem>>

    // 보유 차 개수 (통계용)
    fun getTeaCountFlow(): Flow<Int>

    // 상세 조회 (수정 화면 등)
    suspend fun getTea(id: String): TeaItem?


    // --- 쓰기 (CUD) ---
    // 추가 & 수정
    suspend fun insertTea(tea: TeaItem)

    // 삭제
    suspend fun deleteTea(id: String)
}