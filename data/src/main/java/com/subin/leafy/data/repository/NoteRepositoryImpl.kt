package com.subin.leafy.data.repository

import com.leafy.shared.ui.utils.LeafyTimeUtils
import com.subin.leafy.data.datasource.NoteDataSource
import com.subin.leafy.data.datasource.StorageDataSource
import com.subin.leafy.data.mapper.toRecord
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.common.mapData
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.BrewingRecord
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class NoteRepositoryImpl(
    private val dataSource: NoteDataSource,
    private val storageDataSource: StorageDataSource
) : NoteRepository {

    private fun wrapCUDOperation(
        operation: suspend () -> DataResourceResult<Unit>
    ): Flow<DataResourceResult<Unit>> = flow {
        emit(DataResourceResult.Loading)
        emit(operation())
    }.catch { e ->
        emit(DataResourceResult.Failure(e))
    }.flowOn(Dispatchers.IO)

    private suspend fun uploadImages(note: BrewingNote, localUris: Map<String, String?>): BrewingNote {
        val updatedUrls = localUris.mapValues { (key, uri) ->
            if (uri != null && uri.startsWith("content://")) {
                val result = storageDataSource.uploadImage(uri, "notes/${note.ownerId}/${note.id}/$key")
                if (result is DataResourceResult.Success) result.data else null
            } else { uri }
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

    override fun create(note: BrewingNote, localImageUris: Map<String, String?>) = wrapCUDOperation {
        val finalNote = uploadImages(note, localImageUris)
        dataSource.create(finalNote)
    }

    override fun update(note: BrewingNote, localImageUris: Map<String, String?>) = wrapCUDOperation {
        val finalNote = uploadImages(note, localImageUris)
        dataSource.update(finalNote)
    }

    override fun delete(id: String) = wrapCUDOperation { dataSource.delete(id) }

    override fun read(userId: String): Flow<DataResourceResult<List<BrewingNote>>> =
        dataSource.read(userId).flowOn(Dispatchers.IO)

    override fun getNoteById(noteId: String): Flow<DataResourceResult<BrewingNote>> =
        dataSource.getNoteById(noteId).flowOn(Dispatchers.IO)

    override fun getRecordsByMonth(userId: String, year: Int, month: Int): Flow<DataResourceResult<List<BrewingRecord>>> =
        dataSource.read(userId).map { result ->
            result.mapData { list ->
                val prefix = LeafyTimeUtils.getMonthPrefix(year, month)
                list.filter { it.context.dateTime.startsWith(prefix) }
                    .map { it.toRecord() }
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun getRecordByDate(userId: String, dateString: String): DataResourceResult<BrewingRecord?> {
        return try {
            dataSource.read(userId).first().mapData { list ->
                list.find { it.context.dateTime == dateString }?.toRecord()
            }
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }


}