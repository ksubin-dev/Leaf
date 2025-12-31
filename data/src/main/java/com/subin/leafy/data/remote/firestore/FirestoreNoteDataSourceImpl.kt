package com.subin.leafy.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.subin.leafy.data.datasource.NoteDataSource
import com.subin.leafy.data.model.dto.BrewingNoteDTO
import com.subin.leafy.data.mapper.toDTO
import com.subin.leafy.data.mapper.toDomainNote
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
    override fun read(userId: String): Flow<List<BrewingNote>> = callbackFlow {
        val listener = noteCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                try {
                    val notes = snapshot?.toObjects(BrewingNoteDTO::class.java)
                        ?.map { it.toDomainNote() } ?: emptyList()
                    trySend(notes)
                } catch (mappingError: Exception) {
                    close(mappingError)
                }
            }
        awaitClose { listener.remove() }
    }

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