package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.remote.AuthDataSource
import com.subin.leafy.data.datasource.remote.PostDataSource
import com.subin.leafy.data.datasource.remote.TeaMasterDataSource
import com.subin.leafy.data.datasource.remote.UserDataSource
import com.subin.leafy.data.mapper.toRankingItem
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.*
import com.subin.leafy.domain.repository.PostRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class PostRepositoryImpl(
    private val authDataSource: AuthDataSource,
    private val postDataSource: PostDataSource,
    private val userDataSource: UserDataSource,
    private val teaMasterDataSource: TeaMasterDataSource
) : PostRepository {

    // =================================================================
    // 1. 피드 조회 (Read)
    // 모든 조회 로직의 끝에는 'mapPostsWithMyState'가 있어 내 상태를 주입합니다.
    // =================================================================


    //이번주 랭킹(home)
    override fun getWeeklyRanking(teaType: TeaType?): Flow<DataResourceResult<List<RankingItem>>> {
        return postDataSource.getWeeklyRanking(teaType).map { result ->
            when (result) {
                is DataResourceResult.Success -> {
                    val filteredPosts = result.data.filter { it.teaType != TeaType.ETC }
                    val top3Posts = filteredPosts.take(3)
                    val rankingItems = top3Posts.mapIndexed { index, post ->
                        post.toRankingItem(rank = index + 1)
                    }

                    DataResourceResult.Success(rankingItems)
                }
                is DataResourceResult.Failure -> {
                    DataResourceResult.Failure(result.exception)
                }
                else -> {
                    DataResourceResult.Failure(Exception("Unknown State"))
                }
            }
        }
    }

    // 이번 주 인기 글 (최근 7일 조회수 기준)
    override fun getPopularPosts(): Flow<DataResourceResult<List<CommunityPost>>> {
        return postDataSource.getPopularPosts().map { result ->
            mapPostsWithMyState(result)
        }
    }

    // 명예의 전당 (누적 북마크 기준)
    override fun getMostBookmarkedPosts(): Flow<DataResourceResult<List<CommunityPost>>> {
        return postDataSource.getMostBookmarkedPosts().map { result ->
            mapPostsWithMyState(result)
        }
    }

    // 팔로잉 피드
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFollowingFeed(): Flow<DataResourceResult<List<CommunityPost>>> {
        val myUid = authDataSource.getCurrentUserId() ?: return flowOf(
            DataResourceResult.Failure(Exception("로그인이 필요합니다."))
        )

        return userDataSource.getFollowingIdsFlow(myUid).flatMapLatest { idsResult ->
            when (idsResult) {
                is DataResourceResult.Success -> {
                    val followingIds = idsResult.data

                    if (followingIds.isEmpty()) {
                        flowOf(DataResourceResult.Success(emptyList()))
                    } else {
                        postDataSource.getFollowingFeed(followingIds).map { postResult ->
                            mapPostsWithMyState(postResult)
                        }
                    }
                }
                is DataResourceResult.Failure -> {
                    flowOf(DataResourceResult.Failure(idsResult.exception))
                }
                else -> {
                    flowOf(DataResourceResult.Failure(Exception("Unknown Error fetching following list")))
                }
            }
        }
    }

    // 특정 유저의 글 모아보기 (프로필 화면용)
    override fun getUserPosts(userId: String): Flow<DataResourceResult<List<CommunityPost>>> {
        return postDataSource.getUserPosts(userId).map { result ->
            mapPostsWithMyState(result)
        }
    }

    // 글 상세 조회
    override suspend fun getPostDetail(postId: String): DataResourceResult<CommunityPost> {
        val result = postDataSource.getPostDetail(postId)
        return if (result is DataResourceResult.Success) {
            val mappedPost = mapSinglePostWithMyState(result.data)
            DataResourceResult.Success(mappedPost)
        } else {
            result
        }
    }

    // 검색 (홈/커뮤니티 공통)
    override suspend fun searchPosts(query: String): DataResourceResult<List<CommunityPost>> {
        val result = postDataSource.searchPosts(query)
        return mapPostsWithMyState(result)
    }



    // =================================================================
    // 2. 티 마스터 (Community 상단 추천)
    // =================================================================

    override fun getRecommendedMasters(): Flow<DataResourceResult<List<TeaMaster>>> {
        val myUid =
            authDataSource.getCurrentUserId() ?: return teaMasterDataSource.getRecommendedMasters()

        return combine(
            teaMasterDataSource.getRecommendedMasters(),
            userDataSource.getFollowingIdsFlow(myUid)
        ) { masterResult, followingResult ->

            if (masterResult !is DataResourceResult.Success) {
                return@combine masterResult
            }

            val masters = masterResult.data

            val followingIds = if (followingResult is DataResourceResult.Success) {
                followingResult.data
            } else {
                emptyList()
            }

            val mappedMasters = masters.map { master ->
                master.copy(isFollowing = followingIds.contains(master.id))
            }

            DataResourceResult.Success(mappedMasters)
        }
    }


    // =================================================================
    // 3. 글 작성/수정/삭제 (CRUD)
    // =================================================================

    override suspend fun createPost(
        postId: String,
        title: String,
        content: String,
        imageUrls: List<String>,
        teaType: String?,
        rating: Int?,
        tags: List<String>,
        brewingSummary: String?,
        originNoteId: String?
    ): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        // 1. 작성자 프로필 정보 가져오기
        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) {
            return DataResourceResult.Failure(Exception("유저 정보를 불러올 수 없습니다."))
        }
        val me = userResult.data

        // 2. 객체 생성
        val newPost = CommunityPost(
            id = postId,
            author = PostAuthor(me.id, me.nickname, me.profileImageUrl, isFollowing = false),
            imageUrls = imageUrls,
            title = title,
            content = content,
            originNoteId = originNoteId,
            teaType = teaType?.let { runCatching { TeaType.valueOf(it) }.getOrNull() },
            rating = rating,
            tags = tags,
            brewingSummary = brewingSummary,
            stats = PostStatistics(0, 0, 0, 0),
            myState = PostSocialState(false, false),
            createdAt = System.currentTimeMillis()
        )

        return postDataSource.createPost(newPost)
    }

    override suspend fun updatePost(post: CommunityPost): DataResourceResult<Unit> {
        return postDataSource.updatePost(post)
    }

    override suspend fun deletePost(postId: String): DataResourceResult<Unit> {
        return postDataSource.deletePost(postId)
    }


    // =================================================================
    // 4. 댓글 (Comment)
    // =================================================================

    override fun getComments(postId: String): Flow<DataResourceResult<List<Comment>>> {
        return postDataSource.getComments(postId).map { result ->
            if (result is DataResourceResult.Success) {
                val myUid = authDataSource.getCurrentUserId()
                val mappedComments = result.data.map { comment ->
                    comment.copy(isMine = comment.author.id == myUid)
                }
                DataResourceResult.Success(mappedComments)
            } else {
                result
            }
        }
    }

    override suspend fun addComment(postId: String, content: String): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) {
            return DataResourceResult.Failure(Exception("유저 정보를 불러올 수 없습니다."))
        }
        val me = userResult.data

        val newComment = Comment(
            id = "",
            postId = postId,
            author = CommentAuthor(
                id = me.id,
                nickname = me.nickname,
                profileImageUrl = me.profileImageUrl
            ),
            content = content,
            createdAt = System.currentTimeMillis(),
            isMine = true
        )
        return postDataSource.addComment(postId, newComment)
    }

    override suspend fun deleteComment(postId: String, commentId: String): DataResourceResult<Unit> {
        return postDataSource.deleteComment(postId, commentId)
    }


    // =================================================================
    // 5. 소셜 액션 (좋아요/북마크)
    // =================================================================

    override suspend fun toggleLike(postId: String): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        // 1. 현재 상태 확인
        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) return DataResourceResult.Failure(Exception("User error"))

        val isCurrentlyLiked = userResult.data.likedPostIds.contains(postId)
        val newIsLiked = !isCurrentlyLiked

        // 2. Post 업데이트 (조회수/좋아요 수 등 숫자 변경)
        val postUpdate = postDataSource.toggleLike(postId, newIsLiked)
        if (postUpdate is DataResourceResult.Failure) return postUpdate

        // 3. User 업데이트 (리스트에 ID 추가/제거 - Atomic Operation)
        return userDataSource.toggleLikePost(myUid, postId, newIsLiked)
    }

    override suspend fun toggleBookmark(postId: String): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) return DataResourceResult.Failure(Exception("User error"))

        val isCurrentlyBookmarked = userResult.data.bookmarkedPostIds.contains(postId)
        val newIsBookmarked = !isCurrentlyBookmarked

        // Post 업데이트
        val postUpdate = postDataSource.toggleBookmark(postId, newIsBookmarked)
        if (postUpdate is DataResourceResult.Failure) return postUpdate

        // User 업데이트 (최적화 함수 사용)
        return userDataSource.toggleBookmarkPost(myUid, postId, newIsBookmarked)
    }

    override suspend fun incrementViewCount(postId: String): DataResourceResult<Unit> {
        return postDataSource.incrementViewCount(postId)
    }

    // 내가 좋아요한 글 목록
    override fun getMyLikedPosts(): Flow<DataResourceResult<List<CommunityPost>>> = flow {
        val myUid = authDataSource.getCurrentUserId()
        if (myUid == null) {
            emit(DataResourceResult.Failure(Exception("로그인이 필요합니다.")))
            return@flow
        }

        // 1. 내 정보 가져오기 (좋아요한 ID 목록 확인)
        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) {
            emit(DataResourceResult.Failure(Exception("유저 정보를 불러올 수 없습니다.")))
            return@flow
        }

        val likedIds = userResult.data.likedPostIds

        if (likedIds.isEmpty()) {
            emit(DataResourceResult.Success(emptyList()))
        } else {
            val postsResult = postDataSource.getPostsByIds(likedIds)
            emit(mapPostsWithMyState(postsResult))
        }
    }

    // 내가 북마크한 글 목록
    override fun getMyBookmarkedPosts(): Flow<DataResourceResult<List<CommunityPost>>> = flow {
        val myUid = authDataSource.getCurrentUserId()
        if (myUid == null) {
            emit(DataResourceResult.Failure(Exception("로그인이 필요합니다.")))
            return@flow
        }

        // 1. 내 정보 가져오기 (북마크한 ID 목록 확인)
        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) {
            emit(DataResourceResult.Failure(Exception("유저 정보를 불러올 수 없습니다.")))
            return@flow
        }

        val bookmarkedIds = userResult.data.bookmarkedPostIds

        if (bookmarkedIds.isEmpty()) {
            emit(DataResourceResult.Success(emptyList()))
        } else {
            val postsResult = postDataSource.getPostsByIds(bookmarkedIds)
            emit(mapPostsWithMyState(postsResult))
        }
    }

    // =================================================================
    // Helper Functions (중복 제거)
    // =================================================================

    private suspend fun mapPostsWithMyState(
        result: DataResourceResult<List<CommunityPost>>
    ): DataResourceResult<List<CommunityPost>> {
        if (result !is DataResourceResult.Success) return result

        val posts = result.data
        val myUid = authDataSource.getCurrentUserId() ?: return result // 비로그인 시 그대로 반환
        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) return result

        val me = userResult.data

        val mappedPosts = posts.map { post ->
            post.copy(
                myState = PostSocialState(
                    isLiked = me.likedPostIds.contains(post.id),
                    isBookmarked = me.bookmarkedPostIds.contains(post.id)
                )
            )
        }
        return DataResourceResult.Success(mappedPosts)
    }

    private suspend fun mapSinglePostWithMyState(post: CommunityPost): CommunityPost {
        val myUid = authDataSource.getCurrentUserId() ?: return post
        val userResult = userDataSource.getUser(myUid)

        return if (userResult is DataResourceResult.Success) {
            val me = userResult.data
            post.copy(
                myState = PostSocialState(
                    isLiked = me.likedPostIds.contains(post.id),
                    isBookmarked = me.bookmarkedPostIds.contains(post.id)
                )
            )
        } else {
            post
        }
    }
}