package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.BrewingRecord
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    // 1. 새로운 노트 생성
    fun create(
        note: BrewingNote,
        localImageUris: Map<String, String?>
    ): Flow<DataResourceResult<Unit>>

    // 2. 특정 사용자의 모든 노트 조회
    fun read(userId: String): Flow<DataResourceResult<List<BrewingNote>>>

    // 3. 노트 수정
    fun update(
        note: BrewingNote,
        localImageUris: Map<String, String?>
    ): Flow<DataResourceResult<Unit>>

    // 4. 노트 삭제
    fun delete(id: String): Flow<DataResourceResult<Unit>>

    // 5. 특정 사용자의 특정 노트 상세 조회
    fun getNoteById(userId: String, noteId: String): Flow<DataResourceResult<BrewingNote>>

    // 6. 마이페이지 캘린더용: 특정 사용자의 특정 연/월 레코드 조회
    fun getRecordsByMonth(userId: String, year: Int, month: Int): Flow<DataResourceResult<List<BrewingRecord>>>

    // 7. 특정 사용자의 특정 날짜 레코드 조회
    suspend fun getRecordByDate(userId: String, dateString: String): DataResourceResult<BrewingRecord?>
}