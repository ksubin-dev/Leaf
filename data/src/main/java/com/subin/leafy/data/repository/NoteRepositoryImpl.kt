package com.subin.leafy.data.repository

import android.util.Log
import com.subin.leafy.data.datasource.local.LocalNoteDataSource
import com.subin.leafy.data.datasource.remote.AuthDataSource
import com.subin.leafy.data.datasource.remote.RemoteNoteDataSource
import com.subin.leafy.data.datasource.remote.UserDataSource
import com.subin.leafy.data.util.BadgeLibrary
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.PostSocialState
import com.subin.leafy.domain.model.UserBadge
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class NoteRepositoryImpl(
    private val localNoteDataSource: LocalNoteDataSource,
    private val remoteNoteDataSource: RemoteNoteDataSource,
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource
) : NoteRepository {

    override fun getMyNotesFlow(): Flow<List<BrewingNote>> {
        val myUid = authDataSource.getCurrentUserId() ?: return flowOf(emptyList())
        return localNoteDataSource.getAllNotesFlow(myUid)
    }

    override fun getNotesByMonthFlow(userId: String, year: Int, month: Int): Flow<List<BrewingNote>> {
        val myUid = authDataSource.getCurrentUserId() ?: return flowOf(emptyList())
        return localNoteDataSource.getNotesByMonthFlow(myUid, year, month)
    }

    override fun searchMyNotes(query: String): Flow<List<BrewingNote>> {
        val myUid = authDataSource.getCurrentUserId() ?: return flowOf(emptyList())
        return localNoteDataSource.searchNotes(myUid, query)
    }

    override suspend fun getNoteDetail(noteId: String): DataResourceResult<BrewingNote> {
        // 1. 노트 데이터 가져오기 (로컬 우선 -> 없으면 리모트)
        var note = localNoteDataSource.getNote(noteId)

        if (note == null) {
            val remoteResult = remoteNoteDataSource.getNoteDetail(noteId)
            if (remoteResult is DataResourceResult.Success) {
                note = remoteResult.data
            } else {
                // 리모트에서도 실패하면 에러 리턴
                return remoteResult
            }
        }

        val myUid = authDataSource.getCurrentUserId()
        if (myUid != null) {
            val userResult = userDataSource.getUser(myUid)
            if (userResult is DataResourceResult.Success) {
                val me = userResult.data
                val isLiked = me.likedPostIds.contains(note.id)
                val isBookmarked = me.bookmarkedPostIds.contains(note.id)
                note = note.copy(
                    myState = PostSocialState(isLiked, isBookmarked)
                )
            }
        }

        return DataResourceResult.Success(note)
    }

    override suspend fun saveNote(note: BrewingNote): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        val noteToSave = note.copy(ownerId = myUid)

        return try {
            localNoteDataSource.insertNote(noteToSave)
            remoteNoteDataSource.createNote(noteToSave)
            checkAndGrantBadges(myUid)
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun updateNote(note: BrewingNote): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId() ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        val noteToUpdate = note.copy(ownerId = myUid)

        return try {
            localNoteDataSource.insertNote(noteToUpdate)
            remoteNoteDataSource.updateNote(noteToUpdate)
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun deleteNote(noteId: String): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId() ?: return DataResourceResult.Failure(Exception("로그인 필요"))

        return try {
            localNoteDataSource.deleteNote(noteId)
            remoteNoteDataSource.deleteNote(noteId, myUid)
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun getUserNotes(userId: String): DataResourceResult<List<BrewingNote>> {
        return remoteNoteDataSource.getUserPublicNotes(userId)
    }

    override suspend fun syncNotes(): DataResourceResult<Unit> {
        Log.d("SYNC_LOG", "🔄 동기화 시작...")

        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인 필요"))

        val result = remoteNoteDataSource.getMyBackupNotes(myUid)

        return if (result is DataResourceResult.Success) {
            val remoteNotes = result.data
            if (remoteNotes.isNotEmpty()) {
                localNoteDataSource.insertNotes(remoteNotes)
            }
            DataResourceResult.Success(Unit)
        } else {
            val exception = (result as DataResourceResult.Failure).exception
            DataResourceResult.Failure(exception)
        }
    }

    override suspend fun clearLocalCache(): DataResourceResult<Unit> {
        return try {
            localNoteDataSource.clearAllTables()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    private suspend fun checkAndGrantBadges(userId: String) {
        val currentNotes = localNoteDataSource.getAllNotesFlow(userId).first()
        val count = currentNotes.size

        suspend fun grant(libraryBadge: BadgeLibrary) {
            val badge = UserBadge(
                id = libraryBadge.id,
                name = libraryBadge.title,
                description = libraryBadge.description,
                imageUrl = libraryBadge.imageUrl,
                obtainedAt = System.currentTimeMillis()
            )
            userDataSource.saveUserBadge(userId, badge)
        }

        when (count) {
            1 -> grant(BadgeLibrary.FIRST_BREW)
            10 -> grant(BadgeLibrary.BREWING_ENTHUSIAST)
            50 -> grant(BadgeLibrary.TEA_MASTER)
        }

        if (count >= 5) {
            val recentFive = currentNotes.take(5)
            val firstType = recentFive.first().teaInfo.type
            val isAllSameType = recentFive.all { it.teaInfo.type == firstType }

            if (isAllSameType) {
                grant(BadgeLibrary.ONE_LOVE)
            }
        }
    }
}