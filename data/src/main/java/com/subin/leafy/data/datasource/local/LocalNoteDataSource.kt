package com.subin.leafy.data.datasource.local

import com.subin.leafy.domain.model.BrewingNote
import kotlinx.coroutines.flow.Flow

interface LocalNoteDataSource {

    fun getAllNotesFlow(ownerId: String): Flow<List<BrewingNote>>

    fun getNotesByMonthFlow(ownerId: String, year: Int, month: Int): Flow<List<BrewingNote>>

    suspend fun getNote(noteId: String): BrewingNote?

    fun searchNotes(ownerId: String, query: String): Flow<List<BrewingNote>>

    suspend fun insertNote(note: BrewingNote)

    suspend fun insertNotes(notes: List<BrewingNote>)

    suspend fun updateNote(note: BrewingNote)

    suspend fun deleteNote(noteId: String)

    suspend fun deleteMyAllNotes(ownerId: String)

    suspend fun clearAllTables()
}