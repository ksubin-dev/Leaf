package com.subin.leafy.data.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.subin.leafy.data.datasource.local.room.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    // 1. 전체 조회 (최신순)
    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    // 2. 월별 조회 (범위 검색)
    // 캘린더에서 '2024년 1월'을 누르면 -> 1월 1일 0시 ~ 2월 1일 0시 사이의 데이터만 가져옴
    @Query("SELECT * FROM notes WHERE createdAt >= :startTimestamp AND createdAt < :endTimestamp ORDER BY createdAt DESC")
    fun getNotesByDateRange(startTimestamp: Long, endTimestamp: Long): Flow<List<NoteEntity>>

    // 3. 상세 조회
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: String): NoteEntity?

    // 4. 검색 (차 이름, 브랜드, 메모 등에서 검색)
    // LIKE 연산자 사용 (%검색어%)
    @Query("""
        SELECT * FROM notes 
        WHERE teaName LIKE '%' || :query || '%' 
           OR teaBrand LIKE '%' || :query || '%' 
           OR memo LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchNotes(query: String): Flow<List<NoteEntity>>

    // 5. 저장 & 수정 (ID 같으면 덮어쓰기)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    // 6. 다중 저장 (동기화용)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)

    // 7. 삭제
    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNote(noteId: String)

    // 8. 전체 삭제 (로그아웃 등)
    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()
}