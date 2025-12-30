package com.subin.leafy.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.subin.leafy.data.remote.firestore.*
import com.subin.leafy.data.repository.*
import com.subin.leafy.domain.usecase.*
import com.subin.leafy.domain.usecase.community.*
import com.subin.leafy.domain.usecase.note.*
import com.subin.leafy.domain.usecase.timer.GetPresetsUseCase
import com.subin.leafy.domain.usecase.user.*
import com.leafy.shared.di.ApplicationContainer
import com.subin.leafy.domain.repository.AuthRepository
import com.subin.leafy.domain.usecase.auth.GetAuthUserUseCase
import com.subin.leafy.domain.usecase.auth.LoginUseCase
import com.subin.leafy.domain.usecase.auth.LogoutUseCase
import com.subin.leafy.domain.usecase.auth.SignUpUseCase

class ApplicationContainerImpl() : ApplicationContainer {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // 1. DataSources (이제 Fake가 아닌 실제 Firestore 구현체 사용)
    @RequiresApi(Build.VERSION_CODES.O)
    private val noteDataSource = FirestoreNoteDataSourceImpl(firestore)
    private val userDataSource = FirestoreUserDataSourceImpl(firebaseAuth, firestore)
    private val timerDataSource = FirestoreTimerDataSourceImpl(firestore)
    private val communityDataSource = FirestoreCommunityDataSourceImpl(firestore)

    // 2. Repositories
    val communityRepository = CommunityRepositoryImpl(communityDataSource)
    @RequiresApi(Build.VERSION_CODES.O)
    val noteRepository = NoteRepositoryImpl(noteDataSource)
    val timerRepository = TimerRepositoryImpl(timerDataSource)
    val userRepository = UserRepositoryImpl(userDataSource)
    val userStatsRepository = UserStatsRepositoryImpl(userDataSource)

    val authRepository: AuthRepository = FirebaseAuthRepositoryImpl(
        firebaseAuth = firebaseAuth,
        firestore = firestore
    )

    // 3. Note UseCases
    @RequiresApi(Build.VERSION_CODES.O)
    override val noteUseCases = NoteUseCases(
        getNotes = GetNotesUseCase(noteRepository),
        getNoteById = GetNoteByIdUseCase(noteRepository),
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

    // 7. Auth UseCases
    override val authUseCases = AuthUseCases(
        signUp = SignUpUseCase(authRepository),
        login = LoginUseCase(authRepository),
        logout = LogoutUseCase(authRepository),
        getAuthUser = GetAuthUserUseCase(authRepository)
    )
}