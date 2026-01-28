package com.subin.leafy.data.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.subin.leafy.data.datasource.local.room.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE ownerId = :ownerId ORDER BY createdAt DESC")
    fun getAllNotes(ownerId: String): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes 
        WHERE ownerId = :ownerId 
          AND date >= :startTimestamp 
          AND date < :endTimestamp 
        ORDER BY date DESC
    """)
    fun getNotesByDateRange(ownerId: String, startTimestamp: Long, endTimestamp: Long): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: String): NoteEntity?

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNote(noteId: String)

    @Query("DELETE FROM notes WHERE ownerId = :ownerId")
    suspend fun deleteMyAllNotes(ownerId: String)

    @Query("DELETE FROM notes")
    suspend fun clearDatabase()
}