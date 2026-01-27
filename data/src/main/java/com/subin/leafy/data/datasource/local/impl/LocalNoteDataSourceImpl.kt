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
import javax.inject.Inject

class LocalNoteDataSourceImpl @Inject constructor(
    private val noteDao: NoteDao
) : LocalNoteDataSource {

    override fun getAllNotesFlow(ownerId: String): Flow<List<BrewingNote>> {
        return noteDao.getAllNotes(ownerId).map { entities ->
            entities.toNoteDomainList()
        }
    }

    override fun getNotesByMonthFlow(ownerId: String, year: Int, month: Int): Flow<List<BrewingNote>> {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTimestamp = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        val endTimestamp = calendar.timeInMillis

        return noteDao.getNotesByDateRange(ownerId, startTimestamp, endTimestamp)
            .map { entities ->
                entities.toNoteDomainList()
            }
    }

    override suspend fun getNote(noteId: String): BrewingNote? {
        return noteDao.getNoteById(noteId)?.toDomain()
    }

    override fun searchNotes(ownerId: String, query: String): Flow<List<BrewingNote>> {
        return noteDao.searchNotes(ownerId, query).map { entities ->
            entities.toNoteDomainList()
        }
    }

    override suspend fun insertNote(note: BrewingNote) {
        noteDao.insertNote(note.toEntity())
    }

    override suspend fun updateNote(note: BrewingNote) {
        val oldEntity = noteDao.getNoteById(note.id)

        if (oldEntity != null) {
            val newEntity = note.toEntity().copy(
                likeCount = oldEntity.likeCount,
                bookmarkCount = oldEntity.bookmarkCount,
                commentCount = oldEntity.commentCount,
                viewCount = oldEntity.viewCount
            )
            noteDao.insertNote(newEntity)
        } else {
            noteDao.insertNote(note.toEntity())
        }
    }

    override suspend fun insertNotes(notes: List<BrewingNote>) {
        val entities = notes.map { it.toEntity() }
        noteDao.insertNotes(entities)
    }

    override suspend fun deleteNote(noteId: String) {
        noteDao.deleteNote(noteId)
    }

    override suspend fun deleteMyAllNotes(ownerId: String) {
        noteDao.deleteMyAllNotes(ownerId)
    }

    override suspend fun clearAllTables() {
        noteDao.clearDatabase()
    }
}