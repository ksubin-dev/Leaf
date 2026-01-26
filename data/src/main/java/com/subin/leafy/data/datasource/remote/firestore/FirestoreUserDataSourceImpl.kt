package com.subin.leafy.data.datasource.remote.firestore

import android.util.Log
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
import com.subin.leafy.data.model.dto.NotificationDto
import com.subin.leafy.data.model.dto.UserBadgeDto
import com.subin.leafy.data.model.dto.UserDto
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.NotificationType
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserBadge
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreUserDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserDataSource {

    private val usersCollection = firestore.collection(FirestoreConstants.COLLECTION_USERS)

    override suspend fun getUser(userId: String): DataResourceResult<User> {
        return try {
            val snapshot = firestore.collection("users").document(userId).get().await()
            val userDto = snapshot.toObject<UserDto>()?.copy(uid = snapshot.id)

            if (userDto != null) {
                DataResourceResult.Success(userDto.toUserDomain())
            } else {
                DataResourceResult.Failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override fun getUserFlow(userId: String): Flow<DataResourceResult<User>> = callbackFlow {
        val listener = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResourceResult.Failure(error))
                    return@addSnapshotListener
                }

                val userDto = snapshot?.toObject<UserDto>()?.copy(uid = snapshot.id)
                if (userDto != null) {
                    trySend(DataResourceResult.Success(userDto.toUserDomain()))
                } else {
                    trySend(DataResourceResult.Failure(Exception("User not found")))
                }
            }
        awaitClose { listener.remove() }
    }

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
                .set(updates, SetOptions.merge())
                .await()

            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun isNicknameDuplicate(nickname: String): Boolean {
        return try {
            val snapshot = usersCollection
                .whereEqualTo(FirestoreConstants.FIELD_NICKNAME, nickname)
                .get()
                .await()
            !snapshot.isEmpty
        } catch (e: Exception) {
            true
        }
    }

    override suspend fun searchUsers(
        query: String,
        lastUserId: String?,
        limit: Int
    ): DataResourceResult<List<User>> {
        if (query.isBlank()) return DataResourceResult.Success(emptyList())

        return try {
            val lastDocument = lastUserId?.let {
                usersCollection.document(it).get().await()
            }

            var firestoreQuery = usersCollection
                .whereGreaterThanOrEqualTo(FirestoreConstants.FIELD_NICKNAME, query)
                .whereLessThanOrEqualTo(FirestoreConstants.FIELD_NICKNAME, query + "\uf8ff")
                .orderBy(FirestoreConstants.FIELD_NICKNAME)

            if (lastDocument != null && lastDocument.exists()) {
                firestoreQuery = firestoreQuery.startAfter(lastDocument)
            }

            val snapshot = firestoreQuery.limit(limit.toLong()).get().await()

            val users = snapshot.documents.mapNotNull { doc ->
                doc.toObject<UserDto>()?.copy(uid = doc.id)?.toUserDomain()
            }
            DataResourceResult.Success(users)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun followUser(myId: String, targetUserId: String): DataResourceResult<Unit> {
        return try {
            val mySnapshot = usersCollection.document(myId).get().await()
            val myName = mySnapshot.getString(FirestoreConstants.FIELD_NICKNAME) ?: "알 수 없음"
            val myProfile = mySnapshot.getString(FirestoreConstants.FIELD_PROFILE_IMAGE)

            firestore.runTransaction { transaction ->
                val myRef = usersCollection.document(myId)
                val targetRef = usersCollection.document(targetUserId)

                transaction.update(myRef, FirestoreConstants.FIELD_FOLLOWING_IDS, FieldValue.arrayUnion(targetUserId))
                transaction.update(myRef, FirestoreConstants.FIELD_FOLLOWING_COUNT, FieldValue.increment(1))

                transaction.update(targetRef, FirestoreConstants.FIELD_FOLLOWER_COUNT, FieldValue.increment(1))
            }.await()

            if (myId != targetUserId) {
                sendNotification(
                    targetUserId = targetUserId,
                    type = NotificationType.FOLLOW,
                    senderId = myId,
                    senderName = myName,
                    senderProfileUrl = myProfile,
                    message = "${myName}님이 회원님을 팔로우하기 시작했습니다."
                )
            }

            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

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

    override fun isFollowingFlow(myId: String, targetUserId: String): Flow<Boolean> = callbackFlow {
        val listener = usersCollection.document(myId)
            .addSnapshotListener { snapshot, _ ->
                val userDto = snapshot?.toObject<UserDto>()
                val isFollowing = userDto?.followingIds?.contains(targetUserId) ?: false
                trySend(isFollowing)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getUsersByIds(userIds: List<String>): DataResourceResult<List<User>> {
        return try {
            if (userIds.isEmpty()) return DataResourceResult.Success(emptyList())

            val userList = mutableListOf<User>()
            userIds.chunked(10).forEach { chunk ->
                val snapshot = usersCollection
                    .whereIn(com.google.firebase.firestore.FieldPath.documentId(), chunk)
                    .get()
                    .await()

                val chunkedUsers = snapshot.documents.mapNotNull { doc ->
                    doc.toObject<UserDto>()?.copy(uid = doc.id)?.toUserDomain()
                }
                userList.addAll(chunkedUsers)
            }

            DataResourceResult.Success(userList)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun getFollowers(myUserId: String): DataResourceResult<List<User>> {
        return try {
            val snapshot = usersCollection
                .whereArrayContains(FirestoreConstants.FIELD_FOLLOWING_IDS, myUserId)
                .get()
                .await()

            val followers = snapshot.documents.mapNotNull { doc ->
                doc.toObject<UserDto>()?.copy(uid = doc.id)?.toUserDomain()
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

    override suspend fun updateNotificationSetting(userId: String, isAgreed: Boolean): DataResourceResult<Unit> {
        return try {
            usersCollection.document(userId)
                .update(FIELD_IS_NOTI_AGREED, isAgreed)
                .await()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun deleteUser(userId: String): DataResourceResult<Unit> {
        return try {
            usersCollection.document(userId).delete().await()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun updateFcmToken(userId: String, token: String?) {
        val updates = if (token == null) {
            mapOf(FirestoreConstants.FIELD_FCM_TOKEN to FieldValue.delete())
        } else {
            mapOf(FirestoreConstants.FIELD_FCM_TOKEN to token)
        }

        usersCollection.document(userId).update(updates).await()
    }

    private fun sendNotification(
        targetUserId: String,
        type: NotificationType,
        senderId: String,
        senderName: String,
        senderProfileUrl: String?,
        message: String,
        targetPostId: String? = null
    ) {
        val notiRef = usersCollection.document(targetUserId)
            .collection(FirestoreConstants.COLLECTION_NOTIFICATIONS)
            .document()

        val notification = NotificationDto(
            id = notiRef.id,
            type = type.name,
            senderId = senderId,
            senderName = senderName,
            senderProfileUrl = senderProfileUrl,
            targetPostId = targetPostId,
            message = message,
            isRead = false,
            createdAt = System.currentTimeMillis()
        )

        notiRef.set(notification)
            .addOnFailureListener { e ->
                Log.e("FirestoreUserDataSource", "Failed to send notification: ${e.message}", e)
            }
    }
}