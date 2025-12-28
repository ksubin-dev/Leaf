package com.subin.leafy.data.repository

import android.annotation.SuppressLint
import com.subin.leafy.data.datasource.NoteDataSource
import com.subin.leafy.data.mapper.toRecord
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.BrewingRecord
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class NoteRepositoryImpl(
    private val dataSource: NoteDataSource
) : NoteRepository {

    private fun wrapCUDOperation(
        operation: suspend () -> DataResourceResult<Unit>
    ): Flow<DataResourceResult<Unit>> = flow {
        emit(operation())
    }.onStart { emit(DataResourceResult.Loading) }
        .catch { emit(DataResourceResult.Failure(it)) }
        .flowOn(Dispatchers.IO)

    override fun create(note: BrewingNote) = wrapCUDOperation { dataSource.create(note) }
    override fun read() = flow {
        emit(dataSource.read())
    }.onStart { emit(DataResourceResult.Loading) }
        .catch { emit(DataResourceResult.Failure(it)) }
        .flowOn(Dispatchers.IO)

    override fun update(note: BrewingNote) = wrapCUDOperation { dataSource.update(note) }
    override fun delete(id: String) = wrapCUDOperation { dataSource.delete(id) }

    override fun getNoteById(id: String): Flow<DataResourceResult<BrewingNote>> = flow {
        val result = dataSource.read()
        when (result) {
            is DataResourceResult.Success -> {
                val note = result.data.find { it.id == id }
                if (note != null) {
                    emit(DataResourceResult.Success(note))
                } else {
                    emit(DataResourceResult.Failure(Exception("해당 ID($id)의 노트를 찾을 수 없습니다.")))
                }
            }
            is DataResourceResult.Failure -> {
                emit(DataResourceResult.Failure(result.exception))
            }
            else -> {
                emit(DataResourceResult.Failure(Exception("데이터를 불러오는 중 알 수 없는 오류가 발생했습니다.")))
            }
        }
    }.onStart { emit(DataResourceResult.Loading) }
        .catch { emit(DataResourceResult.Failure(it)) }
        .flowOn(Dispatchers.IO)

    /**
     * 특정 연/월의 레코드 가져오기 (String 비교 방식)
     */
    @SuppressLint("DefaultLocale")
    override fun getRecordsByMonth(year: Int, month: Int): Flow<DataResourceResult<List<BrewingRecord>>> = flow {
        val result = dataSource.read()
        if (result is DataResourceResult.Success) {
            val prefix = String.format("%04d-%02d", year, month)

            val records = result.data.filter { note ->
                note.context.dateTime.startsWith(prefix)
            }.map { it.toRecord() }

            emit(DataResourceResult.Success(records))
        } else if (result is DataResourceResult.Failure) {
            emit(DataResourceResult.Failure(result.exception))
        }
    }.onStart { emit(DataResourceResult.Loading) }
        .flowOn(Dispatchers.IO)

    /**
     * 특정 날짜의 레코드 가져오기 (String 직접 비교 방식)
     */
    override suspend fun getRecordByDate(dateString: String): DataResourceResult<BrewingRecord?> {
        val result = dataSource.read()
        return if (result is DataResourceResult.Success) {
            val note = result.data.find { note ->
                note.context.dateTime == dateString
            }
            DataResourceResult.Success(note?.toRecord())
        } else if (result is DataResourceResult.Failure) {
            DataResourceResult.Failure(result.exception)
        } else {
            DataResourceResult.Failure(Exception("알 수 없는 오류가 발생했습니다."))
        }
    }
}