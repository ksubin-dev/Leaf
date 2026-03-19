package com.leafy.features.note.viewmodel

import app.cash.turbine.test
import com.leafy.shared.R
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.FlavorTag
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.TeaUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    lateinit var noteUseCases: NoteUseCases

    @MockK
    lateinit var userUseCases: UserUseCases

    @MockK
    lateinit var teaUseCases: TeaUseCases

    private lateinit var viewModel: NoteViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = NoteViewModel(noteUseCases, userUseCases, teaUseCases)
    }

    @Test
    fun `updateFlavorTag 호출시_기존에_없는_태그면_추가되고_있던_태그면_삭제된다`() {
        val initialTag = FlavorTag.FRUITY

        viewModel.updateFlavorTag(initialTag)

        assertTrue(viewModel.uiState.value.flavorTags.contains(initialTag))

        viewModel.updateFlavorTag(initialTag)

        assertTrue(viewModel.uiState.value.flavorTags.isEmpty())
    }

    @Test
    fun `saveNote 호출시_로그인이_안되어있으면_예외처리되고_로그인필요_토스트를_띄운다`() = runTest {
        coEvery { userUseCases.getCurrentUserId() } returns DataResourceResult.Failure(Exception("Not Logged In"))

        viewModel.sideEffect.test {

            viewModel.saveNote()

            val effect = awaitItem()

            assertTrue(effect is NoteSideEffect.ShowToast)

            val toastEffect = effect as NoteSideEffect.ShowToast
            assertTrue(toastEffect.message is UiText.StringResource)
            assertEquals(R.string.msg_login_required, (toastEffect.message as UiText.StringResource).resId)
        }
    }


    @Test
    fun `saveNote 호출시_성공하면_업로드예약을_실행하고_뒤로가기_이벤트를_발생시킨다`() = runTest {
        val dummyUserId = "user123"
        coEvery { userUseCases.getCurrentUserId() } returns DataResourceResult.Success(dummyUserId)
        coEvery { noteUseCases.scheduleNoteUpload(any(), any(), any()) } returns Unit // 업로드 호출시 그냥 통과시킴

        viewModel.sideEffect.test {
            viewModel.saveNote()

            val firstEffect = awaitItem()
            assertTrue(firstEffect is NoteSideEffect.ShowToast)

            val secondEffect = awaitItem()
            assertTrue(secondEffect is NoteSideEffect.NavigateBack)

            assertEquals(false, viewModel.uiState.value.isLoading)
        }
    }
}