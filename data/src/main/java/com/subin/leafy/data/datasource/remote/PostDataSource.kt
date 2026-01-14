package com.subin.leafy.data.datasource.remote

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.Comment
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.TeaMaster
import com.subin.leafy.domain.model.TeaType
import kotlinx.coroutines.flow.Flow

interface PostDataSource {

    // --- 1. 피드 조회 (Read) ---

    // 이번 주 주간 랭킹 (홈 화면용)
    fun getWeeklyRanking(teaType: TeaType?): Flow<DataResourceResult<List<CommunityPost>>>

    // 이번 주 인기 노트 (조회수 + 좋아요 기준)
    fun getPopularPosts(): Flow<DataResourceResult<List<CommunityPost>>>

    // 가장 많이 북마크된 노트 (명예의 전당)
    fun getMostBookmarkedPosts(): Flow<DataResourceResult<List<CommunityPost>>>

    // 팔로잉 피드 (내가 팔로우한 사람들의 글)
    fun getFollowingFeed(followingIds: List<String>): Flow<DataResourceResult<List<CommunityPost>>>

    // ID 리스트로 글 목록 가져오기 (좋아요/북마크 목록용)
    // 리스트가 비어있으면 빈 리스트 반환하도록 구현부에서 처리 필요
    suspend fun getPostsByIds(postIds: List<String>): DataResourceResult<List<CommunityPost>>

    // 특정 유저가 쓴 글 모아보기 (프로필 화면용)
    // 내 글 보기, 다른 사람 글 보기 등에서 무조건 쓰입니다!
    fun getUserPosts(userId: String): Flow<DataResourceResult<List<CommunityPost>>>

    // 글 상세 조회
    suspend fun getPostDetail(postId: String): DataResourceResult<CommunityPost>

    // 제목 또는 태그로 검색
    suspend fun searchPosts(query: String): DataResourceResult<List<CommunityPost>>


    // --- 2. 글 작성/수정/삭제 (CRUD) ---

    suspend fun createPost(post: CommunityPost): DataResourceResult<Unit>
    suspend fun updatePost(post: CommunityPost): DataResourceResult<Unit>
    suspend fun deletePost(postId: String): DataResourceResult<Unit>


    // --- 3. 댓글 (Comment) ---

    fun getComments(postId: String): Flow<DataResourceResult<List<Comment>>>
    suspend fun addComment(postId: String, comment: Comment): DataResourceResult<Unit>
    suspend fun deleteComment(postId: String, commentId: String): DataResourceResult<Unit>


    // --- 4. 소셜 액션 (Action) ---

    suspend fun toggleLike(postId: String, isLiked: Boolean): DataResourceResult<Unit>
    suspend fun toggleBookmark(postId: String, isBookmarked: Boolean): DataResourceResult<Unit>
    suspend fun incrementViewCount(postId: String): DataResourceResult<Unit>
}