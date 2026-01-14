package com.subin.leafy.data.datasource.local.impl

import com.subin.leafy.data.datasource.local.LocalNoteDataSource
import com.subin.leafy.data.datasource.local.room.dao.NoteDao
import com.subin.leafy.data.mapper.toDomain
import com.subin.leafy.data.mapper.toEntity
import com.subin.leafy.data.mapper.toNoteDomainList
import com.subin.leafy.domain.model.BrewingNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class LocalNoteDataSourceImpl(
    private val noteDao: NoteDao
) : LocalNoteDataSource {

    // --- 조회 (Read) ---
    override fun getAllNotesFlow(): Flow<List<BrewingNote>> {
        return noteDao.getAllNotes().map { entities ->
            entities.toNoteDomainList()
        }
    }

    override fun getNotesByMonthFlow(year: Int, month: Int): Flow<List<BrewingNote>> {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTimestamp = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        val endTimestamp = calendar.timeInMillis

        return noteDao.getNotesByDateRange(startTimestamp, endTimestamp)
            .map { entities ->
                entities.toNoteDomainList()
            }
    }

    override suspend fun getNote(noteId: String): BrewingNote? {
        return noteDao.getNoteById(noteId)?.toDomain()
    }

    override fun searchNotes(query: String): Flow<List<BrewingNote>> {
        return noteDao.searchNotes(query).map { entities ->
            entities.toNoteDomainList()
        }
    }

    // --- 쓰기 (Write) ---
    override suspend fun insertNote(note: BrewingNote) {
        noteDao.insertNote(note.toEntity())
    }

    override suspend fun insertNotes(notes: List<BrewingNote>) {
        val entities = notes.map { it.toEntity() }
        noteDao.insertNotes(entities)
    }

    override suspend fun deleteNote(noteId: String) {
        noteDao.deleteNote(noteId)
    }

    override suspend fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }
}