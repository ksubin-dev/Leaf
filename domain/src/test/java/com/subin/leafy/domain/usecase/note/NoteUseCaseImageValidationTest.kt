package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BodyType
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.BrewingRecipe
import com.subin.leafy.domain.model.NoteMetadata
import com.subin.leafy.domain.model.PostSocialState
import com.subin.leafy.domain.model.PostStatistics
import com.subin.leafy.domain.model.RatingInfo
import com.subin.leafy.domain.model.SensoryEvaluation
import com.subin.leafy.domain.model.TeaInfo
import com.subin.leafy.domain.model.TeaType
import com.subin.leafy.domain.model.TeawareType
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteUseCaseImageValidationTest {

    @Test
    fun `save note succeeds without images when required note fields are valid`() = runTest {
        val repository = FakeNoteRepository()
        val useCase = SaveNoteUseCase(repository)

        val result = useCase(createNote(imageUrls = emptyList()))

        assertTrue(result is DataResourceResult.Success)
    }

    @Test
    fun `update note succeeds without images when required note fields are valid`() = runTest {
        val repository = FakeNoteRepository()
        val useCase = UpdateNoteUseCase(repository)

        val result = useCase(createNote(imageUrls = emptyList()))

        assertTrue(result is DataResourceResult.Success)
    }

    private fun createNote(imageUrls: List<String>): BrewingNote {
        return BrewingNote(
            id = "note-1",
            ownerId = "user-1",
            isPublic = false,
            teaInfo = TeaInfo(
                name = "향긋한 우롱차",
                brand = "",
                type = TeaType.OOLONG
            ),
            recipe = BrewingRecipe(
                waterTemp = 90,
                leafAmount = 4f,
                waterAmount = 150,
                brewTimeSeconds = 120,
                infusionCount = 1,
                teaware = TeawareType.GAIWAN
            ),
            evaluation = SensoryEvaluation(
                body = BodyType.MEDIUM
            ),
            rating = RatingInfo(stars = 3),
            metadata = NoteMetadata(imageUrls = imageUrls),
            stats = PostStatistics(),
            myState = PostSocialState(),
            date = 1L,
            createdAt = 1L
        )
    }

    private class FakeNoteRepository : NoteRepository {
        override fun getMyNotesFlow(): Flow<List<BrewingNote>> = flowOf(emptyList())

        override fun getNotesByMonthFlow(userId: String, year: Int, month: Int): Flow<List<BrewingNote>> {
            return flowOf(emptyList())
        }

        override fun searchMyNotes(query: String): Flow<List<BrewingNote>> = flowOf(emptyList())

        override suspend fun getNoteDetail(noteId: String): DataResourceResult<BrewingNote> {
            return DataResourceResult.Failure(Exception("not implemented"))
        }

        override suspend fun deleteNote(noteId: String): DataResourceResult<Unit> {
            return DataResourceResult.Success(Unit)
        }

        override suspend fun saveNote(note: BrewingNote): DataResourceResult<Unit> {
            return DataResourceResult.Success(Unit)
        }

        override suspend fun updateNote(note: BrewingNote): DataResourceResult<Unit> {
            return DataResourceResult.Success(Unit)
        }

        override suspend fun getUserNotes(userId: String): DataResourceResult<List<BrewingNote>> {
            return DataResourceResult.Success(emptyList())
        }

        override suspend fun syncNotes(): DataResourceResult<Unit> {
            return DataResourceResult.Success(Unit)
        }

        override suspend fun clearLocalCache(): DataResourceResult<Unit> {
            return DataResourceResult.Success(Unit)
        }

        override suspend fun scheduleNoteUpload(
            note: BrewingNote,
            imageUriStrings: List<String>,
            isEditMode: Boolean
        ) = Unit
    }
}
