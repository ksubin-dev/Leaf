package com.leafy.features.note.viewmodel

import com.leafy.shared.ui.model.BrewingSessionNavArgs
import com.leafy.shared.ui.model.InfusionRecordDto
import com.subin.leafy.domain.model.TeaType
import com.subin.leafy.domain.model.TeawareType
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.TeaUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
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
    fun `initFromTimerData applies timer tea type and brewing fields`() = runTest {
        val args = BrewingSessionNavArgs(
            teaName = "향긋한 우롱차",
            teaType = TeaType.OOLONG.name,
            waterTemp = 90,
            leafAmount = 4f,
            waterAmount = 150,
            teaware = TeawareType.GAIWAN.name,
            records = listOf(
                InfusionRecordDto(count = 1, timeSeconds = 120, waterTemp = 90),
                InfusionRecordDto(count = 2, timeSeconds = 90, waterTemp = 92)
            )
        )

        viewModel.initFromTimerData(Json.encodeToString(args))

        val state = viewModel.uiState.value
        assertEquals("향긋한 우롱차", state.teaName)
        assertEquals(TeaType.OOLONG, state.teaType)
        assertEquals("90", state.waterTemp)
        assertEquals("4.0", state.leafAmount)
        assertEquals("150", state.waterAmount)
        assertEquals("210", state.brewTime)
        assertEquals("2", state.infusionCount)
        assertEquals(TeawareType.GAIWAN, state.teaware)
        assertTrue(state.memo.contains("[타이머 기록]"))
        assertTrue(state.memo.contains("1회차: 90°C / 120초"))
        assertTrue(state.memo.contains("2회차: 92°C / 90초"))
    }

    @Test
    fun `initFromTimerData keeps previous tea type and uses default teaware when enum values are invalid`() = runTest {
        viewModel.updateTeaType(TeaType.BLACK)
        viewModel.updateTeaware(TeawareType.KYUSU)

        val args = BrewingSessionNavArgs(
            teaName = "잘못된 enum 테스트",
            teaType = "INVALID_TEA_TYPE",
            waterTemp = 80,
            leafAmount = 3f,
            waterAmount = 120,
            teaware = "INVALID_TEWARE",
            records = listOf(
                InfusionRecordDto(count = 1, timeSeconds = 60, waterTemp = 80)
            )
        )

        viewModel.initFromTimerData(Json.encodeToString(args))

        val state = viewModel.uiState.value
        assertEquals(TeaType.BLACK, state.teaType)
        assertEquals(TeawareType.MUG, state.teaware)
        assertEquals("60", state.brewTime)
        assertEquals("1", state.infusionCount)
        assertTrue(state.memo.contains("1회차: 80°C / 60초"))
    }
}
