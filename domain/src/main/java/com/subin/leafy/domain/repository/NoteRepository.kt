package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    // --- 1. 내 노트 관리 (Local First) ---

    // 전체 목록
    fun getMyNotesFlow(): Flow<List<BrewingNote>>

    // 캘린더용 (특정 월 데이터만 조회) - 효율성 UP!
    fun getNotesByMonthFlow(year: Int, month: Int): Flow<List<BrewingNote>>

    // 내 노트 검색 (제목, 차 이름 등)
    fun searchMyNotes(query: String): Flow<List<BrewingNote>>

    // 상세 조회
    suspend fun getNoteDetail(noteId: String): DataResourceResult<BrewingNote>


    // --- 2. 노트 작성/수정/삭제 (Local + Remote) ---
    suspend fun saveNote(note: BrewingNote): DataResourceResult<Unit>
    suspend fun updateNote(note: BrewingNote): DataResourceResult<Unit>
    suspend fun deleteNote(noteId: String): DataResourceResult<Unit>


    // --- 3. 타인 노트 조회 (Remote Only) ---
    suspend fun getUserNotes(userId: String): DataResourceResult<List<BrewingNote>>


    // --- 4. 동기화 (Sync) ---
    suspend fun syncNotes(): DataResourceResult<Unit>
}