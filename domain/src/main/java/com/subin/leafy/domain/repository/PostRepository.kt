package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.Comment
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.RankingItem
import com.subin.leafy.domain.model.TeaMaster
import com.subin.leafy.domain.model.TeaType
import kotlinx.coroutines.flow.Flow

interface PostRepository {

    // =================================================================
    // 1. 피드 조회 (Read)
    // - 모든 반환된 게시글에는 '내 좋아요/북마크 여부(myState)'가 채워져 있어야 함
    // =================================================================

    // 이번 주 랭킹 (홈 화면용)
    // - teaType: null이면 '전체', 값이 있으면(GREEN 등) 해당 차 종류 필터링
    // - 반환값: CommunityPost가 아니라, 가공된 RankingItem 리스트를 반환합니다.
    fun getWeeklyRanking(teaType: TeaType?): Flow<DataResourceResult<List<RankingItem>>>

    // 이번 주 인기 노트 (최근 7일 + 조회수 기준)
    fun getPopularPosts(): Flow<DataResourceResult<List<CommunityPost>>>

    // 명예의 전당 (누적 북마크 기준)
    fun getMostBookmarkedPosts(): Flow<DataResourceResult<List<CommunityPost>>>

    // 팔로잉 피드 (내가 팔로우한 유저들의 글)
    // * 파라미터 없음: 내부에서 UserDataSource를 통해 내 팔로잉 목록을 가져와서 조회함
    fun getFollowingFeed(followingIds: List<String>): Flow<DataResourceResult<List<CommunityPost>>>

    // 특정 유저의 글 모아보기 (프로필 화면용)
    fun getUserPosts(userId: String): Flow<DataResourceResult<List<CommunityPost>>>

    // 글 상세 조회
    suspend fun getPostDetail(postId: String): DataResourceResult<CommunityPost>

    // 검색 (제목 또는 태그) - 홈 화면 & 커뮤니티 공통
    suspend fun searchPosts(query: String): DataResourceResult<List<CommunityPost>>


    // =================================================================
    // 2. 티 마스터 (Community 상단 추천)
    // =================================================================

    // 추천 티마스터 목록 (내 팔로우 여부 포함)
    fun getRecommendedMasters(): Flow<DataResourceResult<List<TeaMaster>>>


    // =================================================================
    // 3. 글 작성/수정/삭제 (CRUD)
    // =================================================================

    // 글 작성
    // ViewModel은 유저가 입력한 내용만 넘기면 됨. (작성자 정보, 날짜 등은 Repo가 처리)
    suspend fun createPost(
        postId: String,
        title: String,
        content: String,
        imageUrls: List<String>,
        teaType: String?,   // 선택 안 할 수도 있음
        rating: Int?,       // 선택 안 할 수도 있음
        tags: List<String>,
        brewingSummary: String?, // 예: "95℃ · 3분"
        originNoteId: String? = null
    ): DataResourceResult<Unit>

    // 글 수정
    suspend fun updatePost(post: CommunityPost): DataResourceResult<Unit>

    // 글 삭제
    suspend fun deletePost(postId: String): DataResourceResult<Unit>


    // =================================================================
    // 4. 댓글 (Comment)
    // =================================================================

    // 댓글 목록 조회 (실시간)
    fun getComments(postId: String): Flow<DataResourceResult<List<Comment>>>

    // 댓글 작성
    suspend fun addComment(postId: String, content: String): DataResourceResult<Unit>

    // 댓글 삭제
    suspend fun deleteComment(postId: String, commentId: String): DataResourceResult<Unit>


    // =================================================================
    // 5. 소셜 액션 (Action)
    // =================================================================

    // 좋아요 토글 (내부에서 현재 상태 확인 후 반대로 변경)
    suspend fun toggleLike(postId: String): DataResourceResult<Unit>

    // 북마크 토글
    suspend fun toggleBookmark(postId: String): DataResourceResult<Unit>

    // 조회수 증가 (상세 진입 시 호출)
    suspend fun incrementViewCount(postId: String): DataResourceResult<Unit>

    // =================================================================
    // 6. 마이페이지용 목록 조회 (좋아요 / 북마크)
    // =================================================================

    // 내가 좋아요한 글 목록
    fun getMyLikedPosts(): Flow<DataResourceResult<List<CommunityPost>>>

    // 내가 북마크한 글 목록
    fun getMyBookmarkedPosts(): Flow<DataResourceResult<List<CommunityPost>>>
}