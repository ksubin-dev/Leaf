package com.subin.leafy.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.subin.leafy.data.datasource.NoteDataSource
import com.subin.leafy.data.mapper.toRecord
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.BrewingRecord
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
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

    override fun delete(id : String) = wrapCUDOperation { dataSource.delete(id) }
    override fun getNoteById(id: String): Flow<DataResourceResult<BrewingNote>> = flow {
        val result = dataSource.read() // 혹은 dataSource.getNoteById(id)
        if (result is DataResourceResult.Success) {
            val note = result.data.find { it.id == id } // 도메인 모델의 id(String) 비교
            if (note != null) emit(DataResourceResult.Success(note))
            else emit(DataResourceResult.Failure(Exception("노트를 찾을 수 없습니다.")))
        } else if (result is DataResourceResult.Failure) {
            emit(DataResourceResult.Failure(result.exception))
        }
    }.onStart { emit(DataResourceResult.Loading) }
        .flowOn(Dispatchers.IO)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getRecordsByMonth(year: Int, month: Int): Flow<DataResourceResult<List<BrewingRecord>>> = flow {
        val result = dataSource.read()
        if (result is DataResourceResult.Success) {
            val records = result.data.filter { note ->
                val localDate = note.createdAt.toInstant().atZone(ZoneId.of("Asia/Seoul")).toLocalDate()
                localDate.year == year && localDate.monthValue == month
            }.map { it.toRecord() }
            emit(DataResourceResult.Success(records))
        } else if (result is DataResourceResult.Failure) {
            emit(DataResourceResult.Failure(result.exception))
        }
    }.onStart { emit(DataResourceResult.Loading) }
        .flowOn(Dispatchers.IO)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getRecordByDate(date: LocalDate): DataResourceResult<BrewingRecord?> {
        val result = dataSource.read()
        return if (result is DataResourceResult.Success) {
            val note = result.data.find { note ->
                val localDate = note.createdAt.toInstant().atZone(ZoneId.of("Asia/Seoul")).toLocalDate()
                localDate.isEqual(date)
            }
            DataResourceResult.Success(note?.toRecord())
        } else if (result is DataResourceResult.Failure) {
            DataResourceResult.Failure(result.exception)
        } else {
            DataResourceResult.Failure(Exception("알 수 없는 오류가 발생했습니다."))
        }
    }
}