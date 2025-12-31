package com.subin.leafy.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.CollectionReference
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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreCommunityDataSourceImpl(
    private val firestore: FirebaseFirestore
) : CommunityDataSource {

    //utils 패키지 만들어서 빼기 반복되는 코드들
    companion object {
        const val COL_POSTS = "community_posts"
        const val COL_MASTERS = "tea_masters"
        const val COL_TAGS = "popular_tags"
    }

    private val postsCol = firestore.collection(COL_POSTS)
    private val mastersCol = firestore.collection(COL_MASTERS)
    private val tagsCol = firestore.collection(COL_TAGS)

    private fun <T, R> CollectionReference.asSnapshotFlow(
        queryCustomizer: (Query) -> Query = { it },
        dtoClass: Class<T>,
        mapper: (List<T>) -> List<R>
    ): Flow<DataResourceResult<List<R>>> = callbackFlow {
        val subscription = queryCustomizer(this@asSnapshotFlow)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResourceResult.Failure(error))
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val dtos = it.toObjects(dtoClass)
                    trySend(DataResourceResult.Success(mapper(dtos)))
                }
            }
        awaitClose { subscription.remove() }
    }

    override fun getPopularNotes() = postsCol.asSnapshotFlow(
        queryCustomizer = { it.orderBy("rating", Query.Direction.DESCENDING).limit(10) },
        dtoClass = CommunityPostDTO::class.java,
        mapper = { it.toDomainList() }
    )

    override fun getRisingNotes() = postsCol.asSnapshotFlow(
        queryCustomizer = { it.orderBy("createdAt", Query.Direction.DESCENDING).limit(10) },
        dtoClass = CommunityPostDTO::class.java,
        mapper = { it.toDomainList() }
    )

    override fun getMostSavedNotes() = postsCol.asSnapshotFlow(
        queryCustomizer = { it.orderBy("savedCount", Query.Direction.DESCENDING).limit(10) },
        dtoClass = CommunityPostDTO::class.java,
        mapper = { it.toDomainList() }
    )

    override fun getFollowingFeed() = postsCol.asSnapshotFlow(
        queryCustomizer = { it.limit(20) },
        dtoClass = CommunityPostDTO::class.java,
        mapper = { it.toDomainList() }
    )

    override fun getRecommendedMasters() = mastersCol.asSnapshotFlow(
        dtoClass = TeaMasterDTO::class.java,
        mapper = { dtos -> dtos.map { it.toDomain() } }
    )

    override fun getPopularTags() = tagsCol.asSnapshotFlow(
        queryCustomizer = { it.orderBy("isTrendingUp", Query.Direction.DESCENDING) },
        dtoClass = CommunityTagDTO::class.java,
        mapper = { dtos -> dtos.map { it.toDomain() } }
    )

    override suspend fun toggleLike(postId: String): DataResourceResult<Boolean> = runCatching {
        val postRef = postsCol.document(postId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val currentLikes = snapshot.getLong("likeCount") ?: 0
            transaction.update(postRef, "likeCount", currentLikes + 1)
        }.await()
        DataResourceResult.Success(true)
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun toggleFollow(masterId: String): DataResourceResult<Boolean> = runCatching {
        // 실제 팔로우 로직 구현부
        DataResourceResult.Success(true)
    }.getOrElse { DataResourceResult.Failure(it) }
}