package com.subin.leafy.data.datasource.remote

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote

interface RemoteNoteDataSource {

    // --- 1. 조회 (Read) ---

    suspend fun getMyBackupNotes(userId: String): DataResourceResult<List<BrewingNote>>

    // 특정 유저의 '공개된(isPublic)' 노트만 가져오기 (타인 프로필+게시글들)
    suspend fun getUserPublicNotes(userId: String): DataResourceResult<List<BrewingNote>>

    // 상세 조회
    suspend fun getNoteDetail(noteId: String): DataResourceResult<BrewingNote>


    // --- 2. 쓰기 (CUD) ---

    // 생성 (Create): 새 노트 백업
    suspend fun createNote(note: BrewingNote): DataResourceResult<Unit>

    // 수정 (Update): 기존 노트 내용 변경
    suspend fun updateNote(note: BrewingNote): DataResourceResult<Unit>

    // 삭제 (Delete)
    suspend fun deleteNote(noteId: String): DataResourceResult<Unit>
}