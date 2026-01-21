package com.subin.leafy.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.utils.ImageCompressor
import com.subin.leafy.data.datasource.local.datastore.dataStore
import com.subin.leafy.data.datasource.local.datastore.timerDataStore
import com.subin.leafy.data.datasource.local.impl.LocalAnalysisDataSourceImpl
import com.subin.leafy.data.datasource.local.impl.LocalNoteDataSourceImpl
import com.subin.leafy.data.datasource.local.impl.LocalSettingDataSourceImpl
import com.subin.leafy.data.datasource.local.impl.LocalTeaDataSourceImpl
import com.subin.leafy.data.datasource.local.impl.LocalTimerDataSourceImpl
import com.subin.leafy.data.datasource.local.room.LeafyDatabase
import com.subin.leafy.data.datasource.remote.auth.FirebaseAuthDataSourceImpl
import com.subin.leafy.data.datasource.remote.firestore.*
import com.subin.leafy.data.datasource.remote.storage.FirebaseStorageDataSourceImpl
import com.subin.leafy.data.repository.*
import com.subin.leafy.domain.repository.*
import com.subin.leafy.domain.usecase.*
import com.subin.leafy.domain.usecase.analysis.GetUserAnalysisUseCase
import com.subin.leafy.domain.usecase.auth.*
import com.subin.leafy.domain.usecase.home.GetHomeContentUseCase
import com.subin.leafy.domain.usecase.image.*
import com.subin.leafy.domain.usecase.note.*
import com.subin.leafy.domain.usecase.post.*
import com.subin.leafy.domain.usecase.setting.*
import com.subin.leafy.domain.usecase.tea.DeleteTeaUseCase
import com.subin.leafy.domain.usecase.tea.GetTeaCountUseCase
import com.subin.leafy.domain.usecase.tea.GetTeaDetailUseCase
import com.subin.leafy.domain.usecase.tea.GetTeasUseCase
import com.subin.leafy.domain.usecase.tea.SaveTeaUseCase
import com.subin.leafy.domain.usecase.tea.SearchTeasUseCase
import com.subin.leafy.domain.usecase.tea.SyncTeasUseCase
import com.subin.leafy.domain.usecase.tea.ToggleFavoriteTeaUseCase
import com.subin.leafy.domain.usecase.timer.*
import com.subin.leafy.domain.usecase.user.*

class ApplicationContainerImpl(
    private val context: Context
) : ApplicationContainer {

    // =================================================================
    // 1. Firebase Instances (Singletons)
    // =================================================================
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    // Local DB Instance
    private val database = LeafyDatabase.getInstance(context)


    // =================================================================
    // 2. DataSources (Impl 생성)
    // =================================================================

    // [Remote] Firestore & Auth & Storage
    // Auth
    private val authDataSource = FirebaseAuthDataSourceImpl(firebaseAuth)

    private val homeDataSource = FirestoreHomeDataSourceImpl(firestore)

    // Note (Remote)
    private val remoteNoteDataSource = FirestoreNoteDataSourceImpl(firestore)

    // Post (Remote)
    private val postDataSource = FirestorePostDataSourceImpl(firestore)

    // User (Remote)
    private val userDataSource = FirestoreUserDataSourceImpl(firestore)

    // Tea Master (Remote)
    private val teaMasterDataSource = FirestoreTeaMasterDataSourceImpl(firestore)

    // Storage
    private val storageDataSource = FirebaseStorageDataSourceImpl(firebaseStorage)
    private val remoteTeaDataSource = FirestoreTeaDataSourceImpl(firestore)


    // [Local] Room & DataStore
    // Note (Local)
    private val localNoteDataSource = LocalNoteDataSourceImpl(
        noteDao = database.noteDao()
    )

    private val timerDataSource = LocalTimerDataSourceImpl(
        timerDao = database.timerDao(),
        dataStore = context.timerDataStore
    )

    private val analysisDataSource = LocalAnalysisDataSourceImpl(
        noteDao = database.noteDao()
    )

    private val settingDataSource = LocalSettingDataSourceImpl(
        dataStore = context.dataStore
    )

    private val localTeaDataSource = LocalTeaDataSourceImpl(
        teaDao = database.teaDao()
    )


    // =================================================================
    // 3. Repositories (의존성 주입 연결)
    // =================================================================

    // Auth Repository
    private val authRepository: AuthRepository = AuthRepositoryImpl(
        authDataSource = authDataSource,
        userDataSource = userDataSource,
        settingDataSource = settingDataSource,
        storageDataSource = storageDataSource
    )

    // User Repository
    private val userRepository: UserRepository = UserRepositoryImpl(
        authDataSource = authDataSource,
        userDataSource = userDataSource
    )

    private val homeRepository: HomeRepository = HomeRepositoryImpl(
        homeDataSource = homeDataSource
    )

    // Note Repository
    private val noteRepository: NoteRepository = NoteRepositoryImpl(
        localNoteDataSource = localNoteDataSource,
        remoteNoteDataSource = remoteNoteDataSource,
        authDataSource = authDataSource,
        userDataSource = userDataSource
    )

    // Community (Post) Repository
    private val postRepository: PostRepository = PostRepositoryImpl(
        authDataSource = authDataSource,
        postDataSource = postDataSource,
        userDataSource = userDataSource,
        teaMasterDataSource = teaMasterDataSource
    )

    // Timer Repository
    private val timerRepository: TimerRepository = TimerRepositoryImpl(
        timerDataSource = timerDataSource
    )

    // Image Repository
    private val imageRepository: ImageRepository = ImageRepositoryImpl(
        storageDataSource = storageDataSource
    )

    // Setting Repository
    private val settingRepository: SettingRepository = SettingRepositoryImpl(
        localSettingDataSource = settingDataSource,
        authDataSource = authDataSource,
        userDataSource = userDataSource
    )

    // Analysis Repository
    private val analysisRepository: AnalysisRepository = AnalysisRepositoryImpl(
        analysisDataSource = analysisDataSource
    )

    private val teaRepository: TeaRepository = TeaRepositoryImpl(
        localTeaDataSource = localTeaDataSource,
        remoteTeaDataSource = remoteTeaDataSource,
        authDataSource = authDataSource
    )


    // =================================================================
    // 4. UseCases Initialization
    // =================================================================

    override val imageCompressor: ImageCompressor by lazy {
        ImageCompressor(context)
    }

    // [A] Auth UseCases
    override val authUseCases = AuthUseCases(
        signUp = SignUpUseCase(authRepository),
        login = LoginUseCase(authRepository),
        logout = LogoutUseCase(authRepository),
        checkLoginStatus = CheckLoginStatusUseCase(authRepository),
        checkNickname = CheckNicknameUseCase(authRepository),
        deleteAccount = DeleteAccountUseCase(authRepository)
    )

    // [B] User UseCases
    override val userUseCases = UserUseCases(
        getCurrentUserId = GetCurrentUserIdUseCase(authRepository),
        getMyProfile = GetMyProfileUseCase(userRepository),
        getUserProfile = GetUserProfileUseCase(userRepository),
        updateProfile = UpdateProfileUseCase(userRepository),
        followUser = FollowUserUseCase(userRepository),
        getFollowList = GetFollowListUseCase(userRepository),
        getUserBadges = GetUserBadgesUseCase(userRepository),
        searchUsers = SearchUsersUseCase(userRepository),
        checkNickname = CheckProfileNicknameUseCase(userRepository),
        getFollowingIdsFlow = GetFollowingIdsFlowUseCase(userRepository)
    )

    override val homeUseCases = HomeUseCases(
        getHomeContent = GetHomeContentUseCase(homeRepository)
    )

    // [C] Note UseCases
    override val noteUseCases = NoteUseCases(
        getMyNotes = GetMyNotesUseCase(noteRepository),
        getNotesByMonth = GetNotesByMonthUseCase(noteRepository),
        searchMyNotes = SearchMyNotesUseCase(noteRepository),
        getNoteDetail = GetNoteDetailUseCase(noteRepository),
        getUserNotes = GetUserNotesUseCase(noteRepository),
        saveNote = SaveNoteUseCase(noteRepository),
        updateNote = UpdateNoteUseCase(noteRepository),
        deleteNote = DeleteNoteUseCase(noteRepository),
        syncNotes = SyncNotesUseCase(noteRepository),
        clearLocalCache = ClearLocalCacheUseCase(noteRepository)
    )

    // [D] Timer UseCases
    override val timerUseCases = TimerUseCases(
        getPresets = GetPresetsUseCase(timerRepository),
        savePreset = SavePresetUseCase(timerRepository),
        deletePreset = DeletePresetUseCase(timerRepository),
        saveLastUsedRecipe = SaveLastUsedRecipeUseCase(timerRepository),
        getLastUsedRecipe = GetLastUsedRecipeUseCase(timerRepository),
        getTimerSettings = GetTimerSettingsUseCase(timerRepository),
        updateTimerSettings = UpdateTimerSettingsUseCase(timerRepository)
    )

    // [E] Community (Post) UseCases
    override val postUseCases = PostUseCases(
        getWeeklyRanking = GetWeeklyRankingUseCase(postRepository),
        getPopularPosts = GetPopularPostsUseCase(postRepository),
        getMostBookmarkedPosts = GetMostBookmarkedPostsUseCase(postRepository),
        getFollowingFeed = GetFollowingFeedUseCase(postRepository),
        getPostDetail = GetPostDetailUseCase(postRepository),
        searchPosts = SearchPostsUseCase(postRepository),
        getRecommendedMasters = GetRecommendedMastersUseCase(postRepository),
        getUserPosts = GetUserPostsUseCase(postRepository),
        getLikedPosts = GetLikedPostsUseCase(postRepository),
        getBookmarkedPosts = GetBookmarkedPostsUseCase(postRepository),
        createPost = CreatePostUseCase(postRepository),
        updatePost = UpdatePostUseCase(postRepository),
        deletePost = DeletePostUseCase(postRepository),
        toggleLike = ToggleLikeUseCase(postRepository),
        toggleBookmark = ToggleBookmarkUseCase(postRepository),
        incrementViewCount = IncrementViewCountUseCase(postRepository),
        getComments = GetCommentsUseCase(postRepository),
        addComment = AddCommentUseCase(postRepository),
        deleteComment = DeleteCommentUseCase(postRepository),
        shareNoteAsPost = ShareNoteAsPostUseCase(noteRepository = noteRepository, postRepository = postRepository),
        postChangeFlow = postRepository.postChangeFlow
    )

    // [F] Image UseCases
    override val imageUseCases = ImageUseCases(
        uploadImage = UploadImageUseCase(imageRepository),
        uploadImages = UploadImagesUseCase(imageRepository),
        deleteImage = DeleteImageUseCase(imageRepository)
    )
    override val analysisUseCases = AnalysisUseCases(
        getUserAnalysis = GetUserAnalysisUseCase(analysisRepository)
    )

    // [G] Setting UseCases
    override val settingUseCases = SettingUseCases(
        getAppSettings = GetAppSettingsUseCase(settingRepository),
        updateDisplaySetting = UpdateDisplaySettingUseCase(settingRepository),
        updateNotificationSetting = UpdateNotificationSettingUseCase(settingRepository),
        manageLoginSetting = ManageLoginSettingUseCase(settingRepository),
        clearAppSettings = ClearAppSettingsUseCase(settingRepository)
    )

    override val teaUseCases = TeaUseCases(
        getTeas = GetTeasUseCase(teaRepository),
        getTeaCount = GetTeaCountUseCase(teaRepository),
        searchTeas = SearchTeasUseCase(teaRepository),
        getTeaDetail = GetTeaDetailUseCase(teaRepository),
        saveTea = SaveTeaUseCase(teaRepository),
        deleteTea = DeleteTeaUseCase(teaRepository),
        toggleFavorite = ToggleFavoriteTeaUseCase(teaRepository),
        syncTeas = SyncTeasUseCase(teaRepository)
    )
}