package com.subin.leafy.data.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.subin.leafy.data.datasource.local.room.entity.TeaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeaDao {

    // 1. 전체 조회 (즐겨찾기 우선 정렬)
    @Query("SELECT * FROM teas ORDER BY isFavorite DESC, createdAt DESC")
    fun getAllTeas(): Flow<List<TeaEntity>>

    // 2. 검색 쿼리
    @Query("SELECT * FROM teas WHERE name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' ORDER BY isFavorite DESC")
    fun searchTeas(query: String): Flow<List<TeaEntity>>

    // 3. 개수 실시간 조회
    @Query("SELECT COUNT(*) FROM teas")
    fun getTeaCount(): Flow<Int>

    // 4. 상세 조회
    @Query("SELECT * FROM teas WHERE id = :teaId")
    suspend fun getTeaById(teaId: String): TeaEntity?

    // 5. 추가/수정
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTea(tea: TeaEntity)

    // 6. 삭제
    @Query("DELETE FROM teas WHERE id = :teaId")
    suspend fun deleteTea(teaId: String)
}