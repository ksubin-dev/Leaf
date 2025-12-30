package com.leafy.features.community.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.features.community.ui.component.ExploreNoteUi
import com.leafy.features.community.ui.component.ExploreTagUi
import com.leafy.features.community.ui.component.ExploreTeaMasterUi
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.ExploreTab
import com.subin.leafy.domain.usecase.CommunityUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface CommunityUiEffect {
    data class ShowSnackbar(val message: String) : CommunityUiEffect
}

class CommunityViewModel(
    private val communityUseCases: CommunityUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()
    private val _effect = MutableSharedFlow<CommunityUiEffect>()
    val effect: SharedFlow<CommunityUiEffect> = _effect.asSharedFlow()

    init {
        readAll()
    }

    fun readAll() {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true) }
//            launch { fetchPopularNotes() }
//            launch { fetchRisingNotes() }
//            launch { fetchMasters() }
//            launch { fetchFollowingFeed() }
//            launch { fetchPopularTags() }
//            launch { fetchMostSavedNotes() }
//        }

        _uiState.update { it.copy(isLoading = true) }

        // 실제 UseCase 호출 대신, 아래 샘플 데이터를 바로 할당합니다.
        _uiState.update { state ->
            state.copy(
                isLoading = false,
                // 1. 인기 노트 샘플 (Top Section)
                popularNotes = listOf(
                    ExploreNoteUi(
                        id = "p1",
                        title = "제주 첫물 녹차",
                        subtitle = "제주 서광다원 · 2024년 봄",
                        imageUrl = "https://picsum.photos/400/300?random=1",
                        rating = 4.8f,
                        authorName = "차 마시는 루이",
                        authorProfileUrl = "https://picsum.photos/100/100?random=11"
                    ),
                    ExploreNoteUi(
                        id = "p2",
                        title = "우롱 밀크티 베이스",
                        subtitle = "대만 오룡 · 고소한 풍미",
                        imageUrl = "https://picsum.photos/400/300?random=2",
                        rating = 4.5f,
                        authorName = "밀크티러버"
                        //authorProfileUrl = "https://picsum.photos/100/100?random=11"
                         //이거 없으니까 그냥 안보임 없더라도 보여주는 로직 필요함
                    )
                ),
                // 2. 급상승 노트 샘플 (Rising Section)
                risingNotes = listOf(
                    ExploreNoteUi(
                        id = "r1",
                        title = "상큼한 히비스커스",
                        subtitle = "블렌딩 티의 정석",
                        imageUrl = "https://picsum.photos/400/300?random=3",
                        rating = 4.2f,
                        authorName = "티 소믈리에",
                        likeCount = 120
                    )
                ),
                // 3. 인기 태그 샘플
                popularTags = listOf(
                    ExploreTagUi(id = "t1", label = "우롱차", isTrendingUp = true),
                    ExploreTagUi(id = "t2", label = "말차라떼", isTrendingUp = true),
                    ExploreTagUi(id = "t3", label = "다도", isTrendingUp = false)
                ),
                // 4. 저장된 노트 샘플 (Saved Section)
                mostSavedNotes = listOf(
                    ExploreNoteUi(
                        id = "s1",
                        title = "실패 없는 밀크티 레시피",
                        subtitle = "홍차 5g, 설탕 10g...",
                        savedCount = 1500,
                        rating = 5.0f
                    )
                ),
                // 5. 티 마스터 샘플
                teaMasters = listOf(
                    ExploreTeaMasterUi(
                        id = "m1",
                        name = "보이차 거사",
                        title = "보이차 20년 경력 마스터",
                        profileImageUrl = "https://picsum.photos/100/100?random=21",
                        isFollowing = false
                    ),
                    ExploreTeaMasterUi(
                        id = "m2",
                        name = "수진 소믈리에",
                        title = "런던 티 아카데미 수료",
                        profileImageUrl = "https://picsum.photos/100/100?random=22",
                        isFollowing = true
                    )
                ),
                // 6. 팔로잉 피드 샘플 (Following Tab)
                followingFeed = listOf(
                    ExploreNoteUi(
                        id = "1",
                        title = "동정오룡차 (Dong Ding Oolong)",
                        subtitle = "대만 · 중배화 · 반구형",
                        authorName = "민지",
                        authorProfileUrl = null,
                        timeAgo = "2시간 전",
                        imageUrl = null,
                        description = "은은한 꽃향과 부드러운 과일향이 조화롭게 어우러진 오룽차, 목넘김이 매끄럽고 여운이 깁니다.",
                        rating = 4.5f,
                        brewingChips = listOf("95℃", "3m", "5g", "1st Infusion"),
                        reviewLabel = "Rebrew 가능",
                        comment = "오늘 아침에 마신 차 중 최고였어요. 3회까지 우려봤는데 2번째 우림이 가장 좋았답니다. 😊",
                        likeCount = 23,
                        isLiked = true,
                        likerProfileUrls = listOf("", "", "")
                    )
                )
            )
        }
    }
    

    private suspend fun fetchPopularNotes() {
        communityUseCases.getPopularNotes().collectLatest { result ->
            handleDataResult(result) { data ->
                _uiState.update { it.copy(popularNotes = data.toNoteUi()) }
            }
        }
    }

    private suspend fun fetchRisingNotes() {
        communityUseCases.getRisingNotes().collectLatest { result ->
            handleDataResult(result) { data ->
                _uiState.update { it.copy(risingNotes = data.toNoteUi()) }
            }
        }
    }

    private suspend fun fetchPopularTags() {
        communityUseCases.getPopularTags().collectLatest { result ->
            handleDataResult(result) { data ->
                _uiState.update { it.copy(popularTags = data.toTagUi()) }
            }
        }
    }

    private suspend fun fetchMostSavedNotes() {
        communityUseCases.getMostSavedNotes().collectLatest { result ->
            handleDataResult(result) { data ->
                _uiState.update { it.copy(mostSavedNotes = data.toNoteUi()) }
            }
        }
    }

    private suspend fun fetchMasters() {
        communityUseCases.getRecommendedMasters().collectLatest { result ->
            handleDataResult(result) { data ->
                _uiState.update { it.copy(teaMasters = data.toMasterUi()) }
            }
        }
    }

    private suspend fun fetchFollowingFeed() {
        communityUseCases.getFollowingFeed().collectLatest { result ->
            handleDataResult(result) { data ->
                _uiState.update { it.copy(followingFeed = data.toNoteUi()) }
            }
        }
    }

    private fun <T> handleDataResult(
        result: DataResourceResult<T>,
        onSuccess: (T) -> Unit
    ) {
        when (result) {
            is DataResourceResult.Loading -> {
                _uiState.update { it.copy(isLoading = true) }
            }
            is DataResourceResult.Success -> {
                onSuccess(result.data)
                _uiState.update { it.copy(isLoading = false, errorMessage = null) }
            }
            is DataResourceResult.Failure -> {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                }
            }
            else -> Unit
        }
    }

    fun onTabSelected(tab: ExploreTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            val result = communityUseCases.toggleLike(postId)
            if (result is DataResourceResult.Success) {
                // 🔹 Toast 대신 Snackbar Effect 발생
                _effect.emit(CommunityUiEffect.ShowSnackbar("좋아요가 반영되었습니다."))
            } else if (result is DataResourceResult.Failure) {
                _effect.emit(CommunityUiEffect.ShowSnackbar("오류 발생: ${result.exception.message}"))
            }
        }
    }

    fun toggleFollow(masterId: String) {
        _uiState.update { currentState ->
            val updatedMasters = currentState.teaMasters.map { master ->
                if (master.id == masterId) {
                    master.copy(isFollowing = !master.isFollowing)
                } else master
            }
            currentState.copy(teaMasters = updatedMasters)
        }

        viewModelScope.launch {
            val master = _uiState.value.teaMasters.find { it.id == masterId }
            val message = if (master?.isFollowing == true) "팔로우를 시작했습니다." else "팔로우를 취소했습니다."
            _effect.emit(CommunityUiEffect.ShowSnackbar(message))
        }
    }
}