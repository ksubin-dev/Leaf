package com.subin.leafy.di

import com.subin.leafy.domain.usecase.note.*
import com.leafy.shared.di.ApplicationContainer
import com.subin.leafy.data.repository.FakeUserRepository
import com.subin.leafy.data.repository.FakeUserStatsRepository
import com.subin.leafy.data.repository.MockNoteRepositoryImpl
import com.subin.leafy.data.repository.MockTimerRepositoryImpl
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.timer.GetPresetsUseCase
import com.subin.leafy.domain.usecase.TimerUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import com.subin.leafy.domain.usecase.user.GetCurrentUserIdUseCase
import com.subin.leafy.domain.usecase.user.GetUserStatsUseCase
import com.subin.leafy.domain.usecase.user.GetUserUseCase

class ApplicationContainerImpl : ApplicationContainer {
    private val noteRepository = MockNoteRepositoryImpl()
    private val timerRepository = MockTimerRepositoryImpl()
    private val userRepository = FakeUserRepository()
    private val userStatsRepository = FakeUserStatsRepository()

    // 2. Note UseCases
    override val noteUseCases = NoteUseCases(
        getNotes = GetNotesUseCase(noteRepository),
        insertNote = InsertNoteUseCase(noteRepository),
        updateNote = UpdateNoteUseCase(noteRepository),
        deleteNote = DeleteNoteUseCase(noteRepository),
        getCurrentUserId = GetCurrentUserIdUseCase(userRepository),
        getMonthlyRecords = GetMonthlyRecordsUseCase(noteRepository),
        getRecordByDate = GetRecordByDateUseCase(noteRepository)
    )

    // 3. Timer UseCases
    override val timerUseCases = TimerUseCases(
        getPresets = GetPresetsUseCase(timerRepository)
    )

    // 4. User UseCases 추가
    override val userUseCases = UserUseCases(
        getCurrentUserId = GetCurrentUserIdUseCase(userRepository),
        getUser = GetUserUseCase(userRepository),
        getUserStats = GetUserStatsUseCase(userStatsRepository)
    )
}