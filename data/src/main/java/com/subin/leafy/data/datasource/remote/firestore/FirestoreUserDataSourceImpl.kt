package com.subin.leafy.data.datasource.remote.firestore

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.subin.leafy.data.datasource.remote.UserDataSource
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.FIELD_BOOKMARKED_POST_IDS
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.FIELD_IS_NOTI_AGREED
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.FIELD_LIKED_POST_IDS
import com.subin.leafy.data.mapper.toDomain
import com.subin.leafy.data.mapper.toDto
import com.subin.leafy.data.mapper.toUserDomain
import com.subin.leafy.data.model.dto.UserBadgeDto
import com.subin.leafy.data.model.dto.UserDto
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserBadge
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreUserDataSourceImpl(
    private val firestore: FirebaseFirestore
) : UserDataSource {

    private val usersCollection = firestore.collection(FirestoreConstants.COLLECTION_USERS)

    // --- 1. 유저 정보 조회 (단건) ---
    override suspend fun getUser(userId: String): DataResourceResult<User> {
        return try {
            val snapshot = usersCollection.document(userId).get().await()
            val userDto = snapshot.toObject<UserDto>()

            if (userDto != null) {
                DataResourceResult.Success(userDto.toUserDomain())
            } else {
                DataResourceResult.Failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    // --- 2. 유저 정보 실시간 감지 (Flow) ---
    override fun getUserFlow(userId: String): Flow<DataResourceResult<User>> = callbackFlow {
        val listener = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResourceResult.Failure(error))
                    return@addSnapshotListener
                }

                val userDto = snapshot?.toObject<UserDto>()
                if (userDto != null) {
                    trySend(DataResourceResult.Success(userDto.toUserDomain()))
                } else {
                    trySend(DataResourceResult.Failure(Exception("User not found")))
                }
            }
        awaitClose { listener.remove() }
    }

    // --- 3. 유저 정보 업데이트 ---
    override suspend fun updateUser(user: User): DataResourceResult<Unit> {
        return try {
            val updates = mapOf(

                FirestoreConstants.FIELD_NICKNAME to user.nickname,
                FirestoreConstants.FIELD_PROFILE_IMAGE to user.profileImageUrl,
                FirestoreConstants.FIELD_BIO to user.bio,
                FirestoreConstants.FIELD_EXPERT_TYPES to user.expertTypes.map { it.name },


                FirestoreConstants.KEY_SOCIAL_STATS to mapOf(
                    FirestoreConstants.KEY_FOLLOWER_COUNT to user.socialStats.followerCount,
                    FirestoreConstants.KEY_FOLLOWING_COUNT to user.socialStats.followingCount
                ),

                FirestoreConstants.FIELD_FOLLOWING_IDS to user.followingIds,
                FIELD_LIKED_POST_IDS to user.likedPostIds,
                FIELD_BOOKMARKED_POST_IDS to user.bookmarkedPostIds,

                FirestoreConstants.FIELD_CREATED_AT to user.createdAt
            )

            usersCollection.document(user.id)
                .set(updates, SetOptions.merge()) // 없으면 생성, 있으면 병합(업데이트)
                .await()

            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun isNicknameDuplicate(nickname: String): Boolean {
        return try {
            val snapshot = firestore.collection(FirestoreConstants.COLLECTION_USERS)
                .whereEqualTo(FirestoreConstants.FIELD_NICKNAME, nickname)
                .get()
                .await()
            !snapshot.isEmpty
        } catch (e: Exception) {
            // 에러나면 안전하게 중복된 걸로 처리하거나 에러 던짐 (여기선 true로 처리)
            true
        }
    }

    // --- 4. 유저 검색 ---
    override suspend fun searchUsers(query: String): DataResourceResult<List<User>> {
        return try {
            if (query.isBlank()) return DataResourceResult.Success(emptyList())

            val snapshot = usersCollection
                .whereGreaterThanOrEqualTo(FirestoreConstants.FIELD_NICKNAME, query)
                .whereLessThanOrEqualTo(FirestoreConstants.FIELD_NICKNAME, query + "\uf8ff")
                .get()
                .await()

            val users = snapshot.documents.mapNotNull { doc ->
                doc.toObject<UserDto>()?.toUserDomain()
            }
            DataResourceResult.Success(users)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    // --- 5. 팔로우 ---
    override suspend fun followUser(myId: String, targetUserId: String): DataResourceResult<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val myRef = usersCollection.document(myId)
                val targetRef = usersCollection.document(targetUserId)

                transaction.update(myRef, FirestoreConstants.FIELD_FOLLOWING_IDS, FieldValue.arrayUnion(targetUserId))
                transaction.update(myRef, FirestoreConstants.FIELD_FOLLOWING_COUNT, FieldValue.increment(1))

                transaction.update(targetRef, FirestoreConstants.FIELD_FOLLOWER_COUNT, FieldValue.increment(1))
            }.await()

            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    // --- 6. 언팔로우 ---
    override suspend fun unfollowUser(myId: String, targetUserId: String): DataResourceResult<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val myRef = usersCollection.document(myId)
                val targetRef = usersCollection.document(targetUserId)

                transaction.update(myRef, FirestoreConstants.FIELD_FOLLOWING_IDS, FieldValue.arrayRemove(targetUserId))
                transaction.update(myRef, FirestoreConstants.FIELD_FOLLOWING_COUNT, FieldValue.increment(-1))
                transaction.update(targetRef, FirestoreConstants.FIELD_FOLLOWER_COUNT, FieldValue.increment(-1))
            }.await()

            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    // --- 7. 팔로우 여부 확인 ---
    override fun isFollowingFlow(myId: String, targetUserId: String): Flow<Boolean> = callbackFlow {
        val listener = usersCollection.document(myId)
            .addSnapshotListener { snapshot, _ ->
                val userDto = snapshot?.toObject<UserDto>()
                val isFollowing = userDto?.followingIds?.contains(targetUserId) ?: false
                trySend(isFollowing)
            }
        awaitClose { listener.remove() }
    }

    // --- 8. ID 리스트로 유저 목록 가져오기 ---
    override suspend fun getUsersByIds(userIds: List<String>): DataResourceResult<List<User>> {
        return try {
            if (userIds.isEmpty()) return DataResourceResult.Success(emptyList())

            val userList = mutableListOf<User>()
            userIds.chunked(10).forEach { chunk ->
                val snapshot = usersCollection
                    .whereIn(FirestoreConstants.FIELD_UID, chunk)
                    .get()
                    .await()

                val chunkedUsers = snapshot.documents.mapNotNull {
                    it.toObject<UserDto>()?.toUserDomain()
                }
                userList.addAll(chunkedUsers)
            }

            DataResourceResult.Success(userList)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    // --- 9. 내 팔로워 목록 가져오기 ---
    override suspend fun getFollowers(myUserId: String): DataResourceResult<List<User>> {
        return try {
            val snapshot = usersCollection
                .whereArrayContains(FirestoreConstants.FIELD_FOLLOWING_IDS, myUserId)
                .get()
                .await()

            val followers = snapshot.documents.mapNotNull {
                it.toObject<UserDto>()?.toUserDomain()
            }
            DataResourceResult.Success(followers)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun getFollowingIds(userId: String): DataResourceResult<List<String>> {
        return try {
            val snapshot = firestore.collection(FirestoreConstants.COLLECTION_USERS)
                .document(userId)
                .get()
                .await()

            val ids = snapshot.get(FirestoreConstants.FIELD_FOLLOWING_IDS) as? List<String> ?: emptyList()
            DataResourceResult.Success(ids)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override fun getFollowingIdsFlow(userId: String): Flow<DataResourceResult<List<String>>> = callbackFlow {
        val listener = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResourceResult.Failure(error))
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val ids = snapshot.get(FirestoreConstants.FIELD_FOLLOWING_IDS) as? List<String> ?: emptyList()
                    trySend(DataResourceResult.Success(ids))
                } else {
                    trySend(DataResourceResult.Failure(Exception("User not found")))
                }
            }
        awaitClose { listener.remove() }
    }

    // --- 10. 뱃지 가져오기 ---
    override suspend fun getUserBadges(userId: String): DataResourceResult<List<UserBadge>> {
        return try {
            val snapshot = usersCollection.document(userId)
                .collection(FirestoreConstants.COLLECTION_BADGES)
                .get()
                .await()

            val badges = snapshot.documents.mapNotNull { doc ->
                doc.toObject<UserBadgeDto>()?.toDomain()
            }
            DataResourceResult.Success(badges)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun saveUserBadge(userId: String, badge: UserBadge): DataResourceResult<Unit> {
        return try {
            val badgeDto = badge.toDto()
            usersCollection.document(userId)
                .collection(FirestoreConstants.COLLECTION_BADGES)
                .document(badge.id)
                .set(badgeDto)
                .await()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun toggleLikePost(userId: String, postId: String, isAdding: Boolean): DataResourceResult<Unit> {
        return try {
            val userRef = usersCollection.document(userId)
            val updateAction = if (isAdding) {
                FieldValue.arrayUnion(postId)
            } else {
                FieldValue.arrayRemove(postId)
            }

            userRef.update(FIELD_LIKED_POST_IDS, updateAction).await()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    // 북마크 토글
    override suspend fun toggleBookmarkPost(userId: String, postId: String, isAdding: Boolean): DataResourceResult<Unit> {
        return try {
            val userRef = usersCollection.document(userId)
            val updateAction = if (isAdding) {
                FieldValue.arrayUnion(postId)
            } else {
                FieldValue.arrayRemove(postId)
            }
            userRef.update(FIELD_BOOKMARKED_POST_IDS, updateAction).await()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun updateNotificationSetting(userId: String, isAgreed: Boolean): DataResourceResult<Unit> {
        return try {
            firestore.collection(FirestoreConstants.COLLECTION_USERS)
                .document(userId)
                .update(FIELD_IS_NOTI_AGREED, isAgreed)
                .await()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }
}