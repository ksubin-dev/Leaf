package com.subin.leafy.data.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.subin.leafy.data.datasource.local.room.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    // 1. 전체 조회
    @Query("SELECT * FROM notes WHERE ownerId = :ownerId ORDER BY createdAt DESC")
    fun getAllNotes(ownerId: String): Flow<List<NoteEntity>>

    // 2. 월별 조회 (내 것만 필터링)
    @Query("""
        SELECT * FROM notes 
        WHERE ownerId = :ownerId 
          AND date >= :startTimestamp 
          AND date < :endTimestamp 
        ORDER BY date DESC
    """)
    fun getNotesByDateRange(ownerId: String, startTimestamp: Long, endTimestamp: Long): Flow<List<NoteEntity>>

    // 3. 상세 조회
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: String): NoteEntity?

    // 4. 검색
    @Query("""
        SELECT * FROM notes 
        WHERE ownerId = :ownerId AND (
            teaName LIKE '%' || :query || '%' 
            OR teaBrand LIKE '%' || :query || '%' 
            OR memo LIKE '%' || :query || '%'
        )
        ORDER BY createdAt DESC
    """)
    fun searchNotes(ownerId: String, query: String): Flow<List<NoteEntity>>

    // 5. 저장 & 수정
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    // 6. 다중 저장
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)

    // 7. 삭제
    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNote(noteId: String)

    // 8. 특정 사용자의 데이터만 전체 삭제 (탈퇴 시 필요)
    @Query("DELETE FROM notes WHERE ownerId = :ownerId")
    suspend fun deleteMyAllNotes(ownerId: String)

    // 9. 로컬 DB 완전 초기화 (로그아웃 시 필요)
    @Query("DELETE FROM notes")
    suspend fun clearDatabase()
}