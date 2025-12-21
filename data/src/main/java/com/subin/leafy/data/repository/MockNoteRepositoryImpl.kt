package com.subin.leafy.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
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

class MockNoteRepositoryImpl : NoteRepository {
    // 1. 초기 데이터 로드 (MockDataSource 활용)
    private val mockDb = MockDataSource.createMockNotes().toMutableList()

    // 공통 CUD 작업 래퍼 (Loading -> Action -> Success/Failure)
    private fun wrapCUDOperation(
        operation: suspend () -> Unit
    ): Flow<DataResourceResult<Unit>> =
        flow {
            emit(DataResourceResult.Loading)
            delay(500)
            operation()
            emit(DataResourceResult.Success(Unit))
        }.catch { e ->
            emit(DataResourceResult.Failure(e))
        }.flowOn(Dispatchers.IO)

    override fun create(note: BrewingNote) = wrapCUDOperation { mockDb.add(note) }

    override fun read() = flow {
        emit(DataResourceResult.Loading)
        delay(500)
        emit(DataResourceResult.Success(mockDb.toList()))
    }.catch { emit(DataResourceResult.Failure(it)) }.flowOn(Dispatchers.IO)

    override fun update(note: BrewingNote) = wrapCUDOperation {
        val index = mockDb.indexOfFirst { it.id == note.id }
        if (index != -1) mockDb[index] = note
    }

    override fun delete(noteId: NoteId) = wrapCUDOperation {
        mockDb.removeIf { it.id == noteId }
    }

    // --- 캘린더 및 마이페이지 전용 기능 ---

    /** 캘린더에 잎사귀 아이콘을 표시하기 위해 해당 월의 모든 기록 날짜를 가져옴 */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getRecordsByMonth(year: Int, month: Int): Flow<DataResourceResult<List<BrewingRecord>>> = flow {
        emit(DataResourceResult.Loading)
        delay(300)

        val records = mockDb.filter { note ->
            val localDate = note.createdAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            localDate.year == year && localDate.monthValue == month
        }.map { it.toRecord() } // Note(상세) -> Record(요약) 매퍼 사용

        emit(DataResourceResult.Success(records))
    }.catch { emit(DataResourceResult.Failure(it)) }.flowOn(Dispatchers.IO)

    /** 캘린더 특정 날짜 클릭 시 하단 카드에 보여줄 요약 정보 한 건을 가져옴 */
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getRecordByDate(date: LocalDate): DataResourceResult<BrewingRecord?> {
        val note = mockDb.find { note ->
            val localDate = note.createdAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            localDate.isEqual(date)
        }
        // 찾은 Note가 있으면 요약본으로 변환해서 반환
        return DataResourceResult.Success(note?.toRecord())
    }
}