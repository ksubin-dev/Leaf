package com.subin.leafy.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.subin.leafy.data.datasource.NoteDataSource
import com.subin.leafy.data.model.dto.BrewingNoteDTO
import com.subin.leafy.data.mapper.toDTO
import com.subin.leafy.data.mapper.toDomainNote
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import kotlinx.coroutines.tasks.await

class FirestoreNoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : NoteDataSource {

    /* 이거 만들어서 관리!
    object FirestoreConstants {
    const val COL_NOTES = "brewing_notes"
    const val COL_USERS = "users"
}
     */

    private val noteCollection = firestore.collection("brewing_notes")

    override suspend fun read(): DataResourceResult<List<BrewingNote>> = runCatching {
        val snapshot = noteCollection.get().await()
        val notes = snapshot.toObjects(BrewingNoteDTO::class.java).map { it.toDomainNote() }
        DataResourceResult.Success(notes)
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun create(note: BrewingNote): DataResourceResult<Unit> = runCatching {
        val dto = note.toDTO()
        noteCollection.document(dto._id).set(dto).await()
        DataResourceResult.Success(Unit)
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun update(note: BrewingNote): DataResourceResult<Unit> = runCatching {
        val dto = note.toDTO()
        noteCollection.document(dto._id).set(dto).await()
        DataResourceResult.Success(Unit)
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun delete(id: String): DataResourceResult<Unit> = runCatching {
        noteCollection.document(id).delete().await()
        DataResourceResult.Success(Unit)
    }.getOrElse { DataResourceResult.Failure(it) }
}