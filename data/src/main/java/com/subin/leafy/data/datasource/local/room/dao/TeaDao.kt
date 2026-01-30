package com.subin.leafy.data.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.subin.leafy.data.datasource.local.room.entity.TeaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeaDao {

    @Query("SELECT * FROM teas ORDER BY isFavorite DESC, createdAt DESC")
    fun getAllTeas(): Flow<List<TeaEntity>>

    @Query("SELECT * FROM teas WHERE name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' ORDER BY isFavorite DESC")
    fun searchTeas(query: String): Flow<List<TeaEntity>>

    @Query("SELECT COUNT(*) FROM teas")
    fun getTeaCount(): Flow<Int>

    @Query("SELECT * FROM teas WHERE id = :teaId")
    suspend fun getTeaById(teaId: String): TeaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTea(tea: TeaEntity)

    @Query("DELETE FROM teas WHERE id = :teaId")
    suspend fun deleteTea(teaId: String)
}