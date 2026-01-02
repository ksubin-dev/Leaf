package com.leafy.features.community.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.*
import com.subin.leafy.domain.usecase.CommunityUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

//sealed interface CommunityUiEffect {
//    data class ShowSnackbar(val message: String) : CommunityUiEffect
//}
//
//class CommunityViewModel(
//    private val communityUseCases: CommunityUseCases
//) : ViewModel() {
//
//    private val _selectedTab = MutableStateFlow(ExploreTab.TRENDING)
//
//    private val _effect = MutableSharedFlow<CommunityUiEffect>()
//    val effect = _effect.asSharedFlow()
//
//    val uiState: StateFlow<CommunityUiState> = combine(
//        listOf(
//            communityUseCases.getPopularNotes(),
//            communityUseCases.getRisingNotes(),
//            communityUseCases.getPopularTags(),
//            communityUseCases.getMostSavedNotes(),
//            communityUseCases.getRecommendedMasters(),
//            communityUseCases.getFollowingFeed(),
//            _selectedTab
//        )
//    ) { array ->
//        // 1. 강제 캐스팅을 통해 타입을 정확히 알려줍니다.
//        val popular = array[0] as DataResourceResult<List<CommunityPost>>
//        val rising = array[1] as DataResourceResult<List<CommunityPost>>
//        val tags = array[2] as DataResourceResult<List<CommunityTag>>
//        val saved = array[3] as DataResourceResult<List<CommunityPost>>
//        val masters = array[4] as DataResourceResult<List<TeaMaster>>
//        val following = array[5] as DataResourceResult<List<CommunityPost>>
//        val tab = array[6] as ExploreTab
//
//        val allResults = listOf(popular, rising, tags, saved, masters, following)
//
//        CommunityUiState(
//            isLoading = allResults.any { it is DataResourceResult.Loading },
//            selectedTab = tab,
//            popularNotes = (popular as? DataResourceResult.Success)?.data?.toNoteUi() ?: emptyList(),
//            risingNotes = (rising as? DataResourceResult.Success)?.data?.toNoteUi() ?: emptyList(),
//            popularTags = (tags as? DataResourceResult.Success)?.data?.toTagUi() ?: emptyList(),
//            mostSavedNotes = (saved as? DataResourceResult.Success)?.data?.toNoteUi() ?: emptyList(),
//            teaMasters = (masters as? DataResourceResult.Success)?.data?.toMasterUi() ?: emptyList(),
//            followingFeed = (following as? DataResourceResult.Success)?.data?.toNoteUi() ?: emptyList(),
//            errorMessage = allResults
//                .filterIsInstance<DataResourceResult.Failure>()
//                .firstOrNull()?.exception?.message
//        )
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = CommunityUiState(isLoading = true)
//    )
//
//    fun onTabSelected(tab: ExploreTab) {
//        _selectedTab.value = tab
//    }
//
//    fun toggleLike(postId: String) {
//        viewModelScope.launch {
//            val result = communityUseCases.toggleLike(postId)
//            if (result is DataResourceResult.Failure) {
//                _effect.emit(CommunityUiEffect.ShowSnackbar("좋아요 실패: ${result.exception.message}"))
//            }
//        }
//    }
//
//    fun toggleFollow(masterId: String) {
//        viewModelScope.launch {
//            val result = communityUseCases.toggleFollow(masterId)
//            if (result is DataResourceResult.Failure) {
//                _effect.emit(CommunityUiEffect.ShowSnackbar("팔로우 실패: ${result.exception.message}"))
//            }
//        }
//    }
//}

sealed interface CommunityUiEffect {
    data class ShowSnackbar(val message: String) : CommunityUiEffect
}

class CommunityViewModel(
    private val communityUseCases: CommunityUseCases
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(ExploreTab.TRENDING)
    private val _effect = MutableSharedFlow<CommunityUiEffect>()
    val effect = _effect.asSharedFlow()

    // --- 1. CommunityPost 더미 데이터 (꽉 채움) ---
    private val dummyPosts = listOf(
        CommunityPost(
            id = "1",
            authorId = "user_01",
            authorName = "차 마시는 민지",
            authorProfileUrl = "https://picsum.photos/100/100?random=1",
            title = "제주 서광다원 햇차 시음기",
            subtitle = "제주 · 2024년 첫물 녹차",
            content = "올해 처음 나온 제주 녹차를 마셔봤습니다. 수색이 아주 맑고 끝맛이 달큰하네요. 아침 안개를 머금은 듯한 신선함이 특징입니다.",
            teaTag = "Green Tea",
            imageUrl = "https://picsum.photos/400/300?random=11",
            rating = 4.8f,
            metaInfo = "제주 서광다원 · 95℃ · 3m",
            brewingSteps = listOf("95℃", "3m", "5g", "150ml"),
            likeCount = 124,
            savedCount = 56,
            isLiked = true,
            isSaved = false,
            createdAt = "2시간 전",
            topComment = "저도 이 차 좋아해요! 정말 깔끔하죠."
        ),
        CommunityPost(
            id = "2",
            authorId = "user_02",
            authorName = "우롱러버",
            authorProfileUrl = "https://picsum.photos/100/100?random=2",
            title = "대만 동정오룡차의 매력",
            subtitle = "대만 · 중배화 · 반구형",
            content = "동정오룡 특유의 구수한 풍미와 꽃향기가 조화롭습니다. 여러 번 우려도 맛이 무너지지 않아서 좋아요.",
            teaTag = "Oolong",
            imageUrl = "https://picsum.photos/400/300?random=22",
            rating = 4.5f,
            metaInfo = "대만 난터우 · 100℃ · 5m",
            brewingSteps = listOf("100℃", "5m", "7g", "200ml"),
            likeCount = 89,
            savedCount = 120,
            isLiked = false,
            isSaved = true,
            createdAt = "5시간 전",
            topComment = "배화 정도가 딱 적당해 보이네요."
        )
    )

    // --- 2. TeaMaster 더미 데이터 (꽉 채움) ---
    private val dummyMastersList = listOf(
        TeaMaster(
            id = "m1",
            name = "티마스터 소영",
            title = "홍차 전문 테이스터",
            profileImageUrl = "https://picsum.photos/100/100?random=3",
            isFollowing = false
        ),
        TeaMaster(
            id = "m2",
            name = "그린티 마니아",
            title = "녹차 & 말차 전문가",
            profileImageUrl = "https://picsum.photos/100/100?random=4",
            isFollowing = true
        )
    )

    // --- 3. CommunityTag 더미 데이터 ---
    private val dummyTagsList = listOf(
        CommunityTag(id = "t1", label = "우롱차", isTrendingUp = true),
        CommunityTag(id = "t2", label = "다도세트", isTrendingUp = true),
        CommunityTag(id = "t3", label = "보이차", isTrendingUp = false)
    )

    // ------------------------------------------------------------------
    // Flow 결합 (UseCase 대신 위 더미 데이터들을 사용)
    // ------------------------------------------------------------------
    val uiState: StateFlow<CommunityUiState> = combine(
        listOf(
            flowOf(DataResourceResult.Success(dummyPosts)),      // Popular
            flowOf(DataResourceResult.Success(dummyPosts.shuffled())), // Rising
            flowOf(DataResourceResult.Success(dummyTagsList)),   // Tags
            flowOf(DataResourceResult.Success(dummyPosts)),      // Saved
            flowOf(DataResourceResult.Success(dummyMastersList)),// Masters
            flowOf(DataResourceResult.Success(dummyPosts)),      // Following Feed
            _selectedTab
        )
    ) { array ->
        val popular = array[0] as DataResourceResult<List<CommunityPost>>
        val rising = array[1] as DataResourceResult<List<CommunityPost>>
        val tags = array[2] as DataResourceResult<List<CommunityTag>>
        val saved = array[3] as DataResourceResult<List<CommunityPost>>
        val masters = array[4] as DataResourceResult<List<TeaMaster>>
        val following = array[5] as DataResourceResult<List<CommunityPost>>
        val tab = array[6] as ExploreTab

        val allResults = listOf(popular, rising, tags, saved, masters, following)

        CommunityUiState(
            isLoading = allResults.any { it is DataResourceResult.Loading },
            selectedTab = tab,
            popularNotes = (popular as? DataResourceResult.Success)?.data?.toNoteUi() ?: emptyList(),
            risingNotes = (rising as? DataResourceResult.Success)?.data?.toNoteUi() ?: emptyList(),
            popularTags = (tags as? DataResourceResult.Success)?.data?.toTagUi() ?: emptyList(),
            mostSavedNotes = (saved as? DataResourceResult.Success)?.data?.toNoteUi() ?: emptyList(),
            teaMasters = (masters as? DataResourceResult.Success)?.data?.toMasterUi() ?: emptyList(),
            followingFeed = (following as? DataResourceResult.Success)?.data?.toNoteUi() ?: emptyList(),
            errorMessage = allResults.filterIsInstance<DataResourceResult.Failure>()
                .firstOrNull()?.exception?.message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CommunityUiState(isLoading = true)
    )

    fun onTabSelected(tab: ExploreTab) {
        _selectedTab.value = tab
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            _effect.emit(CommunityUiEffect.ShowSnackbar("좋아요를 눌렀습니다! (게시글 ID: $postId)"))
        }
    }

    fun toggleFollow(masterId: String) {
        viewModelScope.launch {
            _effect.emit(CommunityUiEffect.ShowSnackbar("마스터를 팔로우합니다. (ID: $masterId)"))
        }
    }
}