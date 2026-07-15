package com.leafy.features.note.viewmodel

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteUiStateTest {

    @Test
    fun `form is valid without selected images when required fields are filled`() {
        val state = NoteUiState(
            teaName = "향긋한 우롱차"
        )

        assertTrue(state.isFormValid)
    }

    @Test
    fun `form remains invalid without tea name even when images are optional`() {
        val state = NoteUiState(
            teaName = ""
        )

        assertFalse(state.isFormValid)
    }
}
