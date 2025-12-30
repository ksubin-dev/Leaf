package com.subin.leafy.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.subin.leafy.data.datasource.CommunityDataSource
import com.subin.leafy.data.mapper.toDomain
import com.subin.leafy.data.mapper.toDomainList
import com.subin.leafy.data.model.dto.CommunityPostDTO
import com.subin.leafy.data.model.dto.CommunityTagDTO
import com.subin.leafy.data.model.dto.TeaMasterDTO
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.CommunityTag
import com.subin.leafy.domain.model.TeaMaster
import kotlinx.coroutines.tasks.await

class FirestoreCommunityDataSourceImpl(
    private val firestore: FirebaseFirestore
) : CommunityDataSource {

    companion object {
        const val COL_POSTS = "community_posts"
        const val COL_MASTERS = "tea_masters"
        const val COL_TAGS = "popular_tags"
    }

    private val postsCol = firestore.collection(COL_POSTS)
    private val mastersCol = firestore.collection(COL_MASTERS)
    private val tagsCol = firestore.collection(COL_TAGS)

    override suspend fun getPopularNotes(): DataResourceResult<List<CommunityPost>> = runCatching {
        val snapshot = postsCol
            .orderBy("rating", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .await()

        val posts = snapshot.toObjects(CommunityPostDTO::class.java).toDomainList()
        DataResourceResult.Success(posts)
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun getRisingNotes(): DataResourceResult<List<CommunityPost>> = runCatching {
        // '좋아요'가 급상승하는 기준이 필요하겠지만, 여기서는 최근 등록 순으로 예시를 듭니다.
        val snapshot = postsCol
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .await()

        val posts = snapshot.toObjects(CommunityPostDTO::class.java).toDomainList()
        DataResourceResult.Success(posts)
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun getMostSavedNotes(): DataResourceResult<List<CommunityPost>> = runCatching {
        val snapshot = postsCol
            .orderBy("savedCount", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .await()

        val posts = snapshot.toObjects(CommunityPostDTO::class.java).toDomainList()
        DataResourceResult.Success(posts)
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun getRecommendedMasters(): DataResourceResult<List<TeaMaster>> = runCatching {
        val snapshot = mastersCol.get().await()
        // TeaMasterDTO -> TeaMaster(Domain) 변환 과정 필요
        val masters = snapshot.toObjects(TeaMasterDTO::class.java).map { it.toDomain() }
        DataResourceResult.Success(masters)
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun getPopularTags(): DataResourceResult<List<CommunityTag>> = runCatching {
        val snapshot = tagsCol
            .orderBy("isTrendingUp", Query.Direction.DESCENDING)
            .get()
            .await()
        val tags = snapshot.toObjects(CommunityTagDTO::class.java).map { it.toDomain() }
        DataResourceResult.Success(tags)
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun getFollowingFeed(): DataResourceResult<List<CommunityPost>> = runCatching {
        // 실제로는 현재 유저가 팔로우하는 ID 목록을 가져와서 whereIn 쿼리를 써야 함
        // 여기서는 전체 피드 중 일부를 반환하는 구조로 작성
        val snapshot = postsCol.limit(20).get().await()
        DataResourceResult.Success(snapshot.toObjects(CommunityPostDTO::class.java).toDomainList())
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun toggleLike(postId: String): DataResourceResult<Boolean> = runCatching {
        // TODO: 실제 Firestore 문서의 likeCount 필드를 FieldValue.increment(1) 등으로 업데이트하는 로직 필요
        DataResourceResult.Success(true)
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun toggleFollow(masterId: String): DataResourceResult<Boolean> = runCatching {
        // TODO: 유저의 팔로우 목록에 추가/삭제 로직 필요
        DataResourceResult.Success(true)
    }.getOrElse { DataResourceResult.Failure(it) }
}