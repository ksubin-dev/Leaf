package com.subin.leafy.di

import com.subin.leafy.data.remote.fakes.*
import com.subin.leafy.data.repository.*
import com.subin.leafy.domain.usecase.*
import com.subin.leafy.domain.usecase.community.*
import com.subin.leafy.domain.usecase.note.*
import com.subin.leafy.domain.usecase.timer.GetPresetsUseCase
import com.subin.leafy.domain.usecase.user.*
import com.leafy.shared.di.ApplicationContainer

class ApplicationContainerImpl : ApplicationContainer {

    // 1. DataSources (가짜 데이터 소스들)
    private val communityDataSource = FakeCommunityDataSourceImpl()
    private val noteDataSource = FakeNoteDataSourceImpl()
    private val timerDataSource = FakeTimerDataSourceImpl()
    private val userDataSource = FakeUserDataSourceImpl()

    // 2. Repositories (DataSource를 주입받음)
    val communityRepository = CommunityRepositoryImpl(communityDataSource)
    val noteRepository = NoteRepositoryImpl(noteDataSource)
    val timerRepository = TimerRepositoryImpl(timerDataSource)
    val userRepository = UserRepositoryImpl(userDataSource)
    val userStatsRepository = UserStatsRepositoryImpl(userDataSource)

    // 3. Note UseCases
    override val noteUseCases = NoteUseCases(
        getNotes = GetNotesUseCase(noteRepository),
        insertNote = InsertNoteUseCase(noteRepository),
        updateNote = UpdateNoteUseCase(noteRepository),
        deleteNote = DeleteNoteUseCase(noteRepository),
        getCurrentUserId = GetCurrentUserIdUseCase(userRepository),
        getMonthlyRecords = GetMonthlyRecordsUseCase(noteRepository),
        getRecordByDate = GetRecordByDateUseCase(noteRepository)
    )

    // 4. Timer UseCases
    override val timerUseCases = TimerUseCases(
        getPresets = GetPresetsUseCase(timerRepository)
    )

    // 5. User UseCases
    override val userUseCases = UserUseCases(
        getCurrentUserId = GetCurrentUserIdUseCase(userRepository),
        getUser = GetUserUseCase(userRepository),
        getUserStats = GetUserStatsUseCase(userStatsRepository)
    )

    // 6. Community UseCases
    override val communityUseCases = CommunityUseCases(
        getPopularNotes = GetPopularNotesUseCase(communityRepository),
        getRisingNotes = GetRisingNotesUseCase(communityRepository),
        getMostSavedNotes = GetMostSavedNotesUseCase(communityRepository),
        getRecommendedMasters = GetRecommendedMastersUseCase(communityRepository),
        getPopularTags = GetPopularTagsUseCase(communityRepository),
        getFollowingFeed = GetFollowingFeedUseCase(communityRepository),
        toggleLike = ToggleLikeUseCase(communityRepository),
        toggleFollow = ToggleFollowUseCase(communityRepository)
    )
}