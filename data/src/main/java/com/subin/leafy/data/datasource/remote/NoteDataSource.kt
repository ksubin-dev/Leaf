package com.subin.leafy.data.datasource.remote

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import kotlinx.coroutines.flow.Flow

interface NoteDataSource {
    // --- 기본 CRUD ---
    suspend fun create(note: BrewingNote): DataResourceResult<Unit>
    fun read(userId: String): Flow<DataResourceResult<List<BrewingNote>>>
    fun getNoteById(noteId: String): Flow<DataResourceResult<BrewingNote>>
    suspend fun update(note: BrewingNote): DataResourceResult<Unit>
    suspend fun delete(id : String): DataResourceResult<Unit>

    // --- 마이페이지 캘린더용 ---
    suspend fun getNoteByDate(userId: String, dateString: String): DataResourceResult<BrewingNote?>

    // 특정 유저의 글만 Flow로 실시간 감시 (이미지 중심)
    fun getUserNotesFlow(userId: String): Flow<DataResourceResult<List<BrewingNote>>>

    // teaType(녹차, 홍차 등)을 받아서 좋아요 순으로 정렬해 가져옴
    suspend fun getPopularNotesByType(teaType: String, limit: Int = 3): DataResourceResult<List<BrewingNote>>

    // 팔로우한 사람들의 글이나 전체 공개된 글을 최신순으로 가져옴
    fun getAllNotesFlow(limit: Int = 20): Flow<DataResourceResult<List<BrewingNote>>>

    // 유저 데이터소스에서도 처리하지만, 노트 자체의 likeCount도 업데이트해야 함
    suspend fun updateLikeCount(noteId: String, increment: Int): DataResourceResult<Unit>

}