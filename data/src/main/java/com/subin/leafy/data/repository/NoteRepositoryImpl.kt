package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.local.LocalNoteDataSource
import com.subin.leafy.data.datasource.remote.AuthDataSource
import com.subin.leafy.data.datasource.remote.RemoteNoteDataSource
import com.subin.leafy.data.datasource.remote.UserDataSource
import com.subin.leafy.data.util.BadgeLibrary
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.UserBadge
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class NoteRepositoryImpl(
    private val localNoteDataSource: LocalNoteDataSource,   // Room (내 폰, 오프라인, 캘린더용)
    private val remoteNoteDataSource: RemoteNoteDataSource, // Firestore (백업, 커뮤니티용)
    private val authDataSource: AuthDataSource,             // 내 ID 확인용
    private val userDataSource: UserDataSource              // 뱃지 지급용
) : NoteRepository {

    // =================================================================
    // 1. 내 노트 관리 (Local First Strategy)
    // =================================================================

    // 전체 목록 (리스트 화면용)
    override fun getMyNotesFlow(): Flow<List<BrewingNote>> {
        return localNoteDataSource.getAllNotesFlow()
    }

    // 캘린더용 (특정 월 데이터만 조회)
    override fun getNotesByMonthFlow(year: Int, month: Int): Flow<List<BrewingNote>> {
        return localNoteDataSource.getNotesByMonthFlow(year, month)
    }

    // 내 노트 검색 (제목, 차 이름 등)
    override fun searchMyNotes(query: String): Flow<List<BrewingNote>> {
        return localNoteDataSource.searchNotes(query)
    }

    // 상세 조회 (로컬 우선 -> 없으면 리모트)
    override suspend fun getNoteDetail(noteId: String): DataResourceResult<BrewingNote> {
        // (1) 로컬에서 먼저 찾기
        val localNote = localNoteDataSource.getNote(noteId)
        if (localNote != null) {
            return DataResourceResult.Success(localNote)
        }
        // (2) 없으면 서버에서 찾기
        return remoteNoteDataSource.getNoteDetail(noteId)
    }


    // =================================================================
    // 2. 저장/수정/삭제 (Local + Remote + Badge)
    // =================================================================

    override suspend fun saveNote(note: BrewingNote): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))
        val noteToSave = note.copy(ownerId = myUid)

        return try {
            localNoteDataSource.insertNote(noteToSave)
            // (2) 리모트 DB 백업 (실패해도 로컬엔 저장됐으니 성공으로 처리 가능)
            remoteNoteDataSource.createNote(noteToSave)
            // (3) [뱃지 시스템] n번째 기록인지 체크하여 뱃지 지급
            checkAndGrantBadges(myUid)
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun updateNote(note: BrewingNote): DataResourceResult<Unit> {
        return try {
            localNoteDataSource.insertNote(note)
            remoteNoteDataSource.updateNote(note)
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun deleteNote(noteId: String): DataResourceResult<Unit> {
        return try {
            localNoteDataSource.deleteNote(noteId)
            remoteNoteDataSource.deleteNote(noteId)
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }


    // =================================================================
    // 3. 타인 노트 조회 (Remote Only)
    // =================================================================

    override suspend fun getUserNotes(userId: String): DataResourceResult<List<BrewingNote>> {
        return remoteNoteDataSource.getUserPublicNotes(userId)
    }


    // =================================================================
    // 4. 동기화 (Sync)
    // =================================================================

    override suspend fun syncNotes(): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        // (1) 서버에서 내 모든 백업 노트 가져오기
        val result = remoteNoteDataSource.getMyBackupNotes(myUid)

        return if (result is DataResourceResult.Success) {
            val remoteNotes = result.data
            if (remoteNotes.isNotEmpty()) {
                localNoteDataSource.insertNotes(remoteNotes)
            }
            DataResourceResult.Success(Unit)
        } else {
            DataResourceResult.Failure((result as DataResourceResult.Failure).exception)
        }
    }


    // =================================================================
    // 뱃지 지급 로직 (Internal)
    // =================================================================

    private suspend fun checkAndGrantBadges(userId: String) {
        // 현재 로컬에 저장된 노트 개수 확인 (Flow의 첫 번째 값을 가져옴)
        // insertNote가 끝난 직후라 최신 개수가 반영되어 있음
        val currentNotes = localNoteDataSource.getAllNotesFlow().first()
        val count = currentNotes.size

        // 뱃지 부여 헬퍼
        suspend fun grant(libraryBadge: BadgeLibrary) {
            val badge = UserBadge(
                id = libraryBadge.id,
                name = libraryBadge.title,
                description = libraryBadge.description,
                imageUrl = libraryBadge.imageUrl,
                obtainedAt = System.currentTimeMillis()
            )
            // 중복 획득 방지는 서버나 DataSource 내부 로직에 맡기거나,
            // 여기서 getUserBadges로 체크할 수 있음. 일단은 저장 시도.
            userDataSource.saveUserBadge(userId, badge)
        }

        // 기록 수에 따른 뱃지 지급 조건
        when (count) {
            1 -> grant(BadgeLibrary.FIRST_BREW)          // 첫 기록
            10 -> grant(BadgeLibrary.BREWING_ENTHUSIAST) // 10회
            50 -> grant(BadgeLibrary.TEA_MASTER)         // 50회
            // 추가 조건들...
        }

        // 3. [추가] '취향 확고' 뱃지 로직 (최근 5개가 같은 차 종류인가?)
        if (count >= 5) {
            // 최신 5개 가져오기
            val recentFive = currentNotes.take(5)
            // 첫 번째 노트의 차 종류
            val firstType = recentFive.first().teaInfo.type

            // 5개가 모두 같은 종류인지 확인
            val isAllSameType = recentFive.all { it.teaInfo.type == firstType }

            if (isAllSameType) {
                // 조건 만족 시 BadgeLibrary에서 꺼내서 지급!
                grant(BadgeLibrary.ONE_LOVE)
            }
        }
    }
}