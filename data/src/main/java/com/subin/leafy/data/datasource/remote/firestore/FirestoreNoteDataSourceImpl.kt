package com.subin.leafy.data.datasource.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.subin.leafy.data.datasource.remote.RemoteNoteDataSource
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.COLLECTION_NOTES
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.FIELD_CREATED_AT
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.FIELD_IS_PUBLIC
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.FIELD_OWNER_ID
import com.subin.leafy.data.mapper.toBrewingDomain
import com.subin.leafy.data.mapper.toDto
import com.subin.leafy.data.model.dto.BrewingNoteDto
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import kotlinx.coroutines.tasks.await

class FirestoreNoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : RemoteNoteDataSource {

    private val notesCollection = firestore.collection(COLLECTION_NOTES)

    // 1. 내 백업 노트 (비공개 포함 전부)
    override suspend fun getMyBackupNotes(userId: String): DataResourceResult<List<BrewingNote>> {
        return try {
            val snapshot = notesCollection
                .whereEqualTo(FIELD_OWNER_ID, userId)
                .orderBy(FIELD_CREATED_AT, Query.Direction.DESCENDING)
                .get()
                .await()

            val notes = snapshot.documents.mapNotNull {
                it.toObject<BrewingNoteDto>()?.toBrewingDomain()
            }
            DataResourceResult.Success(notes)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    // 2. 타인 공개 노트 -> 그 사람의 기록들(프로필+작성한글들)
    override suspend fun getUserPublicNotes(userId: String): DataResourceResult<List<BrewingNote>> {
        return try {
            val snapshot = notesCollection
                .whereEqualTo(FIELD_OWNER_ID, userId)
                .whereEqualTo(FIELD_IS_PUBLIC, true)
                .orderBy(FIELD_CREATED_AT, Query.Direction.DESCENDING)
                .get()
                .await()

            val notes = snapshot.documents.mapNotNull {
                it.toObject<BrewingNoteDto>()?.toBrewingDomain()
            }
            DataResourceResult.Success(notes)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    // 3. 상세 조회
    override suspend fun getNoteDetail(noteId: String): DataResourceResult<BrewingNote> {
        return try {
            val snapshot = notesCollection.document(noteId).get().await()
            val dto = snapshot.toObject<BrewingNoteDto>()

            if (dto != null) {
                DataResourceResult.Success(dto.toBrewingDomain())
            } else {
                DataResourceResult.Failure(Exception("Note not found"))
            }
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    // 4. 생성 (Create)
    override suspend fun createNote(note: BrewingNote): DataResourceResult<Unit> {
        return try {
            val dto = note.toDto()
            notesCollection.document(note.id).set(dto).await()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    // 5. 수정 (Update)
    override suspend fun updateNote(note: BrewingNote): DataResourceResult<Unit> {
        return try {
            val dto = note.toDto()
            notesCollection.document(note.id).set(dto, SetOptions.merge()).await()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    // 6. 삭제 (Delete)
    override suspend fun deleteNote(noteId: String): DataResourceResult<Unit> {
        return try {
            notesCollection.document(noteId).delete().await()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }
}