package com.subin.leafy.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.subin.leafy.data.datasource.NoteDataSource
import com.subin.leafy.data.mapper.toRecord
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.BrewingRecord
import com.subin.leafy.domain.model.id.NoteId
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.ZoneId

class NoteRepositoryImpl(
    private val dataSource: NoteDataSource
) : NoteRepository {

    // 공통 CUD 작업 래퍼
    private fun wrapCUDOperation(
        operation: suspend () -> DataResourceResult<Unit>
    ): Flow<DataResourceResult<Unit>> = flow {
        emit(DataResourceResult.Loading)
        delay(500)
        emit(operation())
    }.catch { e ->
        emit(DataResourceResult.Failure(e))
    }.flowOn(Dispatchers.IO)

    override fun create(note: BrewingNote) = wrapCUDOperation { dataSource.create(note) }

    override fun read() = flow {
        emit(DataResourceResult.Loading)
        delay(500)
        emit(dataSource.read())
    }.catch { emit(DataResourceResult.Failure(it)) }.flowOn(Dispatchers.IO)

    override fun update(note: BrewingNote) = wrapCUDOperation { dataSource.update(note) }

    override fun delete(noteId: NoteId) = wrapCUDOperation { dataSource.delete(noteId) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getRecordsByMonth(year: Int, month: Int): Flow<DataResourceResult<List<BrewingRecord>>> = flow {
        emit(DataResourceResult.Loading)
        delay(300)

        val result = dataSource.read()
        if (result is DataResourceResult.Success) {
            val records = result.data.filter { note ->
                val localDate = note.createdAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                localDate.year == year && localDate.monthValue == month
            }.map { it.toRecord() }
            emit(DataResourceResult.Success(records))
        } else if (result is DataResourceResult.Failure) {
            emit(DataResourceResult.Failure(result.exception))
        }
    }.catch { emit(DataResourceResult.Failure(it)) }.flowOn(Dispatchers.IO)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getRecordByDate(date: LocalDate): DataResourceResult<BrewingRecord?> {
        val result = dataSource.read()
        return if (result is DataResourceResult.Success) {
            val note = result.data.find { note ->
                val localDate = note.createdAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                localDate.isEqual(date)
            }
            DataResourceResult.Success(note?.toRecord())
        } else {
            DataResourceResult.Failure(Exception("Data Load Failed"))
        }
    }
}