package com.subin.leafy.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.CollectionReference
import com.subin.leafy.data.datasource.NoteDataSource
import com.subin.leafy.data.model.dto.BrewingNoteDTO
import com.subin.leafy.data.mapper.toDTO
import com.subin.leafy.data.mapper.toDomainNote
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await

class FirestoreNoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : NoteDataSource {

    companion object {
        private const val COL_NOTES = "brewing_notes"
    }

    private val noteCollection = firestore.collection(COL_NOTES)

    private fun <T, R> CollectionReference.asSnapshotFlow(
        queryCustomizer: (Query) -> Query = { it },
        dtoClass: Class<T>,
        mapper: (List<T>) -> List<R>
    ): Flow<DataResourceResult<List<R>>> = callbackFlow {
        val subscription = queryCustomizer(this@asSnapshotFlow)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResourceResult.Failure(error))
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val dtos = it.toObjects(dtoClass)
                    trySend(DataResourceResult.Success(mapper(dtos)))
                }
            }
        awaitClose { subscription.remove() }
    }.onStart { emit(DataResourceResult.Loading) }

    override fun read(userId: String): Flow<DataResourceResult<List<BrewingNote>>> =
        noteCollection.asSnapshotFlow(
            queryCustomizer = {
                it.whereEqualTo("userId", userId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
            },
            dtoClass = BrewingNoteDTO::class.java,
            mapper = { dtos -> dtos.map { it.toDomainNote() } }
        )

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