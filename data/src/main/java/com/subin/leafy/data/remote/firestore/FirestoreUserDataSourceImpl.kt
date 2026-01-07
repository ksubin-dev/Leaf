package com.subin.leafy.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FieldValue
import com.subin.leafy.data.datasource.UserDataSource
import com.subin.leafy.data.mapper.toDomain
import com.subin.leafy.data.model.dto.UserDTO
import com.subin.leafy.data.model.dto.UserStatsDTO
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserStats
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreUserDataSourceImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UserDataSource {

    companion object {
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_STATS = "user_stats"
        private const val COLLECTION_FOLLOWS = "follows"
    }

    override suspend fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override fun getUserFlow(userId: String): Flow<DataResourceResult<User>> = callbackFlow {
        val subscription = firestore.collection(COLLECTION_USERS).document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResourceResult.Failure(error))
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val userDto = snapshot.toObject(UserDTO::class.java)
                    if (userDto != null) {
                        trySend(DataResourceResult.Success(userDto.toDomain()))
                    }
                } else {
                    trySend(DataResourceResult.Failure(Exception("User not found")))
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun getUser(userId: String): DataResourceResult<User> = runCatching {
        val document = firestore.collection(COLLECTION_USERS).document(userId).get().await()

        if (!document.exists()) {
            throw NoSuchElementException("유저(ID: $userId) 정보가 존재하지 않습니다.")
        }

        val userDto = document.toObject(UserDTO::class.java)
            ?: throw Exception("유저 데이터 변환 실패")

        DataResourceResult.Success(userDto.toDomain())
    }.getOrElse {
        DataResourceResult.Failure(it)
    }

    override suspend fun getUserStats(userId: String): DataResourceResult<UserStats> = runCatching {
        val document = firestore.collection(COLLECTION_STATS).document(userId).get().await()
        val statsDto = document.toObject(UserStatsDTO::class.java) ?: UserStatsDTO()

        DataResourceResult.Success(statsDto.toDomain())
    }.getOrElse {
        DataResourceResult.Failure(it)
    }

    override suspend fun updateUser(user: User): DataResourceResult<Unit> = runCatching {
        val updateData = mapOf(
            "displayName" to user.username,
            "photoUrl" to user.profileImageUrl,
            "bio" to user.bio,
            "followerCount" to user.followerCount,
            "followingCount" to user.followingCount
        )

        firestore.collection(COLLECTION_USERS)
            .document(user.id)
            .update(updateData)
            .await()

        DataResourceResult.Success(Unit)
    }.getOrElse {
        DataResourceResult.Failure(it)
    }

    /**
     * 🎯 팔로우 처리 (Atomic Batch 사용)
     * 내 팔로잉 증가와 상대 팔로워 증가를 한 번에 처리합니다.
     */
    override suspend fun followUser(myId: String, targetUserId: String): DataResourceResult<Unit> = runCatching {
        val batch = firestore.batch()

        // 1. 팔로우 관계 문서 생성 (중복 방지 ID 사용)
        val followRef = firestore.collection(COLLECTION_FOLLOWS).document("${myId}_$targetUserId")
        batch.set(followRef, mapOf(
            "fromId" to myId,
            "toId" to targetUserId,
            "createdAt" to System.currentTimeMillis()
        ))

        // 2. 내 팔로잉 수 증가
        val myRef = firestore.collection(COLLECTION_USERS).document(myId)
        batch.update(myRef, "followingCount", FieldValue.increment(1))

        // 3. 상대 팔로워 수 증가
        val targetRef = firestore.collection(COLLECTION_USERS).document(targetUserId)
        batch.update(targetRef, "followerCount", FieldValue.increment(1))

        batch.commit().await()
        DataResourceResult.Success(Unit)
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun unfollowUser(myId: String, targetUserId: String): DataResourceResult<Unit> = runCatching {
        val batch = firestore.batch()

        // 1. 팔로우 관계 문서 삭제
        val followRef = firestore.collection(COLLECTION_FOLLOWS).document("${myId}_$targetUserId")
        batch.delete(followRef)

        // 2. 카운트 감소
        batch.update(firestore.collection(COLLECTION_USERS).document(myId), "followingCount", FieldValue.increment(-1))
        batch.update(firestore.collection(COLLECTION_USERS).document(targetUserId), "followerCount", FieldValue.increment(-1))

        batch.commit().await()
        DataResourceResult.Success(Unit)
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun checkFollowStatus(myId: String, targetUserId: String): Boolean {
        val doc = firestore.collection(COLLECTION_FOLLOWS).document("${myId}_$targetUserId").get().await()
        return doc.exists()
    }

    override suspend fun fetchTopUsers(limit: Int): DataResourceResult<List<User>> = runCatching {
        val snapshot = firestore.collection(COLLECTION_USERS)
            .orderBy("followerCount", Query.Direction.DESCENDING) // 🎯 DESCENDING 에러 해결됨
            .limit(limit.toLong())
            .get().await()

        val users = snapshot.toObjects(UserDTO::class.java).map { it.toDomain() }
        DataResourceResult.Success(users)
    }.getOrElse { DataResourceResult.Failure(it) }
}