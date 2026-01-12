package com.subin.leafy.data.datasource.local

import com.subin.leafy.domain.model.BrewingNote
import kotlinx.coroutines.flow.Flow

interface LocalNoteDataSource {

    // --- 조회 (Read) ---

    // 1. 모든 노트 실시간 감지 (전체 리스트)
    fun getAllNotesFlow(): Flow<List<BrewingNote>>

    // 2. 특정 달(Month)의 노트만 가져오기 (캘린더 화면 최적화)
    // 예: 2024년 1월 데이터만 줘! -> 쿼리 속도 UP
    fun getNotesByMonthFlow(year: Int, month: Int): Flow<List<BrewingNote>>

    // 3. 노트 1개 상세 조회 (수정 화면 들어갈 때)
    suspend fun getNote(noteId: String): BrewingNote?

    // 4. 검색 (차 이름, 브랜드, 맛 메모 등)
    fun searchNotes(query: String): Flow<List<BrewingNote>>


    // --- 쓰기 (Write) ---

    // 저장 & 수정 (ConflictStrategy.REPLACE 사용 예정)
    suspend fun insertNote(note: BrewingNote)

    // [동기화용] 로그인 시 서버에서 받아온 리스트를 한방에 저장
    suspend fun insertNotes(notes: List<BrewingNote>)

    // 삭제
    suspend fun deleteNote(noteId: String)

    // 전체 삭제 (로그아웃 시 데이터 정리용)
    suspend fun deleteAllNotes()
}