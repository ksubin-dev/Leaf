package com.subin.leafy.data.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class MockNoteRepositoryImpl : NoteRepository {
    private val mockDb = mutableListOf<BrewingNote>()

    private fun wrapCUDOperation(
        operation: suspend () -> Unit
    ): Flow<DataResourceResult<Unit>> = flow {
        emit(DataResourceResult.Loading)
        delay(1000)
        operation()
        emit(DataResourceResult.Success(Unit))
    }.catch { e ->
        emit(DataResourceResult.Failure(e))
    }.flowOn(Dispatchers.IO)

    override fun create(note: BrewingNote) = wrapCUDOperation {
        mockDb.add(note)
    }

    override fun read() = flow {
        emit(DataResourceResult.Loading)
        delay(500)
        emit(DataResourceResult.Success(mockDb.toList()))
    }.catch { e ->
        emit(DataResourceResult.Failure(e))
    }.flowOn(Dispatchers.IO)

    override fun update(note: BrewingNote) = wrapCUDOperation {
        val index = mockDb.indexOfFirst { it.id == note.id }
        if (index != -1) mockDb[index] = note
    }

    override fun delete(noteId: String) = wrapCUDOperation {
        mockDb.removeIf { it.id.toString() == noteId }
    }
}