package com.subin.leafy.data.datasource.local

import com.subin.leafy.domain.model.BrewingNote
import kotlinx.coroutines.flow.Flow

interface LocalNoteDataSource {

    // --- 조회 (Read) ---

    // 1. 특정 사용자의 모든 노트 실시간 감지
    fun getAllNotesFlow(ownerId: String): Flow<List<BrewingNote>>

    // 2. 특정 사용자의 특정 달(Month) 노트 조회
    fun getNotesByMonthFlow(ownerId: String, year: Int, month: Int): Flow<List<BrewingNote>>

    // 3. 특정 사용자의 노트 1개 상세 조회
    suspend fun getNote(noteId: String): BrewingNote?

    // 4. 특정 사용자의 데이터 내에서 검색
    fun searchNotes(ownerId: String, query: String): Flow<List<BrewingNote>>


    // --- 쓰기 (Write) ---

    // 저장 & 수정
    suspend fun insertNote(note: BrewingNote)

    // 다중 저장
    suspend fun insertNotes(notes: List<BrewingNote>)

    // 특정 사용자의 특정 노트 삭제
    suspend fun deleteNote(noteId: String)

    // 특정 사용자의 모든 데이터 삭제 (계정 탈퇴 등)
    suspend fun deleteMyAllNotes(ownerId: String)

    // 전체 삭제 (로그아웃 시 로컬 캐시 정리용)
    suspend fun clearAllTables()
}