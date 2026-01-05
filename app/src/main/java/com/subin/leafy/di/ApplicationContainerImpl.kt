package com.subin.leafy.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()


    // 2. DataSources
    private val noteDataSource = FirestoreNoteDataSourceImpl(firestore)

    private val storageDataSource = FirebaseStorageDataSourceImpl(firebaseStorage)
    private val userDataSource = FirestoreUserDataSourceImpl(firebaseAuth, firestore)
    private val timerDataSource = FirestoreTimerDataSourceImpl(firestore)
    private val communityDataSource = FirestoreCommunityDataSourceImpl(firestore)

    // 2. Repositories
    val noteRepository = NoteRepositoryImpl(
        dataSource = noteDataSource,
        storageDataSource = storageDataSource
    )
    val timerRepository = TimerRepositoryImpl(timerDataSource)
    val userRepository = UserRepositoryImpl(userDataSource)
    val userStatsRepository = UserStatsRepositoryImpl(userDataSource)

    val authRepository: AuthRepository = FirebaseAuthRepositoryImpl(
        firebaseAuth = firebaseAuth,
        firestore = firestore,
        firebaseStorage  = firebaseStorage
    )
    val communityRepository = CommunityRepositoryImpl(
        targetDataSource = communityDataSource,
        authRepository = authRepository
    )

    private val insightAnalyzer = InsightAnalyzerImpl()

    // 3. Note UseCases
    override val noteUseCases = NoteUseCases(
        getNotes = GetNotesUseCase(noteRepository),
        getNoteById = GetNoteByIdUseCase(noteRepository),
        insertNote = InsertNoteUseCase(noteRepository),
        updateNote = UpdateNoteUseCase(noteRepository),
        deleteNote = DeleteNoteUseCase(noteRepository),
        getCurrentUserId = GetCurrentUserIdUseCase(userRepository),
        getMonthlyRecords = GetMonthlyRecordsUseCase(noteRepository),
        getRecordByDate = GetRecordByDateUseCase(noteRepository),
        getBrewingInsights = GetBrewingInsightsUseCase(noteRepository, insightAnalyzer)
    )

    // 4. Timer UseCases
    override val timerUseCases = TimerUseCases(
        getPresets = GetPresetsUseCase(timerRepository)
    )

    // 5. User UseCases
    override val userUseCases = UserUseCases(
        getCurrentUserId = GetCurrentUserIdUseCase(userRepository),
        getUser = GetUserUseCase(userRepository),
        getUserStats = GetUserStatsUseCase(userStatsRepository),
        updateProfile = UpdateProfileUseCase(userRepository)
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
        toggleSave = ToggleSaveUseCase(communityRepository),
        toggleFollow = ToggleFollowUseCase(communityRepository),
        getComments = GetCommentsUseCase(communityRepository),
        addComment = AddCommentUseCase(communityRepository),
        deleteComment = DeleteCommentUseCase(communityRepository)
    )

    // 7. Auth UseCases
    override val authUseCases = AuthUseCases(
        signUp = SignUpUseCase(authRepository),
        login = LoginUseCase(authRepository),
        logout = LogoutUseCase(authRepository),
        getAuthUser = GetAuthUserUseCase(authRepository)
    )
}