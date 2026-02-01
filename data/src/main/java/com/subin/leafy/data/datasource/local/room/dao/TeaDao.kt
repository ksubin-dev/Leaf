package com.subin.leafy.data.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.subin.leafy.data.datasource.local.room.entity.TeaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeaDao {

    @Query("SELECT * FROM teas WHERE ownerId = :ownerId ORDER BY isFavorite DESC, createdAt DESC")
    fun getAllTeas(ownerId: String): Flow<List<TeaEntity>>

    @Query("SELECT * FROM teas WHERE ownerId = :ownerId AND (name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%') ORDER BY isFavorite DESC")
    fun searchTeas(ownerId: String, query: String): Flow<List<TeaEntity>>

    @Query("SELECT COUNT(*) FROM teas WHERE ownerId = :ownerId")
    fun getTeaCount(ownerId: String): Flow<Int>

    @Query("SELECT * FROM teas WHERE id = :teaId")
    suspend fun getTeaById(teaId: String): TeaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTea(tea: TeaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeas(teas: List<TeaEntity>)

    @Query("DELETE FROM teas WHERE id = :teaId")
    suspend fun deleteTea(teaId: String)

    @Query("DELETE FROM teas WHERE ownerId = :ownerId")
    suspend fun deleteMyAllTeas(ownerId: String)
}