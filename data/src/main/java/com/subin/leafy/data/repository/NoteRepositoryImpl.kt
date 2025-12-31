package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.NoteDataSource
import com.subin.leafy.data.datasource.StorageDataSource
import com.subin.leafy.data.mapper.toRecord
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.BrewingRecord
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class NoteRepositoryImpl(
    private val dataSource: NoteDataSource,
    private val storageDataSource: StorageDataSource
) : NoteRepository {

    // --- [CUD 작업 공통 래퍼] ---
    private fun wrapCUDOperation(
        operation: suspend () -> DataResourceResult<Unit>
    ): Flow<DataResourceResult<Unit>> = flow {
        emit(DataResourceResult.Loading)
        val result = operation()
        emit(result)
    }.catch { e ->
        emit(DataResourceResult.Failure(e))
    }.flowOn(Dispatchers.IO)

    // --- [이미지 업로드 헬퍼] ---
    private suspend fun uploadImages(note: BrewingNote, localUris: Map<String, String?>): BrewingNote {
        val updatedUrls = localUris.mapValues { (key, uri) ->
            if (uri != null && uri.startsWith("content://")) {
                val result = storageDataSource.uploadImage(uri, "notes/${note.ownerId}/${note.id}/$key")
                if (result is DataResourceResult.Success) result.data else uri
            } else {
                uri
            }
        }
        return note.copy(
            context = note.context.copy(
                dryLeafUri = updatedUrls["dryLeaf"],
                liquorUri = updatedUrls["liquor"],
                teawareUri = updatedUrls["teaware"],
                additionalUri = updatedUrls["additional"]
            )
        )
    }

    // --- [인터페이스 구현부: CREATE & UPDATE] ---

    override fun create(note: BrewingNote, localImageUris: Map<String, String?>) = wrapCUDOperation {
        val finalNote = uploadImages(note, localImageUris)
        dataSource.create(finalNote)
    }

    override fun update(note: BrewingNote, localImageUris: Map<String, String?>) = wrapCUDOperation {
        val finalNote = uploadImages(note, localImageUris)
        dataSource.update(finalNote)
    }

    override fun delete(id: String) = wrapCUDOperation {
        dataSource.delete(id)
    }

    // --- [인터페이스 구현부: READ] ---

    override fun read(userId: String): Flow<DataResourceResult<List<BrewingNote>>> =
        dataSource.read(userId)
            .map { list -> DataResourceResult.Success(list) as DataResourceResult<List<BrewingNote>> }
            .onStart { emit(DataResourceResult.Loading) }
            .catch { e -> emit(DataResourceResult.Failure(e)) }
            .flowOn(Dispatchers.IO)

    override fun getNoteById(userId: String, noteId: String): Flow<DataResourceResult<BrewingNote>> =
        dataSource.read(userId)
            .map { list ->
                val note = list.find { it.id == noteId }
                if (note != null) {
                    DataResourceResult.Success(note) as DataResourceResult<BrewingNote>
                } else {
                    DataResourceResult.Failure(Exception("노트를 찾을 수 없습니다."))
                }
            }
            .catch { e -> emit(DataResourceResult.Failure(e)) }
            .flowOn(Dispatchers.IO)

    override fun getRecordsByMonth(userId: String, year: Int, month: Int): Flow<DataResourceResult<List<BrewingRecord>>> =
        dataSource.read(userId)
            .map { list ->
                val prefix = String.format("%04d-%02d", year, month)
                val records = list.filter { it.context.dateTime.startsWith(prefix) }
                    .map { it.toRecord() }
                DataResourceResult.Success(records) as DataResourceResult<List<BrewingRecord>>
            }
            .onStart { emit(DataResourceResult.Loading) }
            .catch { e -> emit(DataResourceResult.Failure(e)) }
            .flowOn(Dispatchers.IO)

    override suspend fun getRecordByDate(userId: String, dateString: String): DataResourceResult<BrewingRecord?> {
        return try {
            val list = dataSource.read(userId).first()
            val record = list.find { it.context.dateTime == dateString }?.toRecord()
            DataResourceResult.Success(record)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }
}