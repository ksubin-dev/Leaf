package com.leafy.features.note.viewmodel

import app.cash.turbine.test
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.*
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK lateinit var noteUseCases: NoteUseCases
    @MockK lateinit var userUseCases: UserUseCases
    @MockK lateinit var postUseCases: PostUseCases

    private lateinit var viewModel: DetailViewModel

    private val dummyNote = BrewingNote(
        id = "note_1", ownerId = "user_me", isPublic = true, date = 0L, createdAt = 0L,
        teaInfo = TeaInfo("녹차", "", TeaType.GREEN, "", "", ""),
        recipe = BrewingRecipe(90, 3f, 150, 180, 1, TeawareType.MUG),
        evaluation = SensoryEvaluation(emptyList(), 3, 3, 3, 3, 3, BodyType.MEDIUM, 3, ""),
        rating = RatingInfo(5, true), metadata = NoteMetadata(WeatherType.SUNNY, "", emptyList()),
        stats = PostStatistics(likeCount = 0, bookmarkCount = 0, 0, 0),
        myState = PostSocialState(isLiked = false, isBookmarked = false)
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        coEvery { postUseCases.observePostChanges() } returns emptyFlow()

        viewModel = DetailViewModel(noteUseCases, userUseCases, postUseCases)
    }

    @Test
    fun `loadNote 호출시_내가_작성자가_아니고_비공개글이면_접근을_차단하고_뒤로가기_이벤트를_발생시킨다`() = runTest {
        val myId = "user_me"
        val otherUserPrivateNote = dummyNote.copy(ownerId = "user_other", isPublic = false)

        coEvery { userUseCases.getCurrentUserId() } returns DataResourceResult.Success(myId)
        coEvery { noteUseCases.getNoteDetail("note_1") } returns DataResourceResult.Success(otherUserPrivateNote)

        viewModel.sideEffect.test {
            viewModel.loadNote("note_1")

            val toastEffect = awaitItem()
            assertTrue(toastEffect is DetailSideEffect.ShowToast)

            val backEffect = awaitItem()
            assertTrue(backEffect is DetailSideEffect.NavigateBack)
        }
    }

    @Test
    fun `toggleLike 호출시_서버통신에_실패하면_낙관적으로_올렸던_좋아요를_원래상태로_롤백한다`() = runTest {
        coEvery { userUseCases.getCurrentUserId() } returns DataResourceResult.Success("user_me")
        coEvery { noteUseCases.getNoteDetail("note_1") } returns DataResourceResult.Success(dummyNote)

        viewModel.loadNote("note_1")
        advanceUntilIdle()

        coEvery { postUseCases.toggleLike("note_1") } returns DataResourceResult.Failure(Exception("Network Error"))

        viewModel.sideEffect.test {
            viewModel.toggleLike()

            val toastEffect = awaitItem()
            assertTrue(toastEffect is DetailSideEffect.ShowToast)

            val currentState = viewModel.uiState.value
            assertEquals(false, currentState.isLiked)
            assertEquals(0, currentState.note?.stats?.likeCount)
        }
    }
}

