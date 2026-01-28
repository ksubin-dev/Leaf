package com.subin.leafy.data.repository

import com.google.firebase.messaging.FirebaseMessaging
import com.subin.leafy.data.datasource.remote.AuthDataSource
import com.subin.leafy.data.datasource.remote.UserDataSource
import com.subin.leafy.data.util.BadgeLibrary
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserBadge
import com.subin.leafy.domain.model.UserRelationState
import com.subin.leafy.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource
) : UserRepository {

    override fun getMyProfileFlow(): Flow<DataResourceResult<User>> {
        val myUid = authDataSource.getCurrentUserId()

        return if (myUid != null) {
            userDataSource.getUserFlow(myUid)
        } else {
            flow { emit(DataResourceResult.Failure(Exception("Not logged in"))) }
        }
    }

    override suspend fun getUserProfile(targetUserId: String): DataResourceResult<User> {
        val targetUserResult = userDataSource.getUser(targetUserId)
        if (targetUserResult is DataResourceResult.Failure) return targetUserResult

        val targetUser = (targetUserResult as DataResourceResult.Success).data
        val myUid = authDataSource.getCurrentUserId()

        var isFollowing = false

        if (myUid != null && myUid != targetUserId) {
            val myProfileResult = userDataSource.getUser(myUid)
            if (myProfileResult is DataResourceResult.Success) {
                isFollowing = myProfileResult.data.followingIds.contains(targetUserId)
            }
        }

        return DataResourceResult.Success(
            targetUser.copy(
                relationState = UserRelationState(isFollowing = isFollowing)
            )
        )
    }

    override suspend fun updateProfile(
        nickname: String?,
        bio: String?,
        profileUrl: String?
    ): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("Not logged in"))

        val myProfileResult = userDataSource.getUser(myUid)
        if (myProfileResult !is DataResourceResult.Success) {
            return DataResourceResult.Failure(Exception("Failed to load profile"))
        }

        val currentUser = myProfileResult.data

        val updatedUser = currentUser.copy(
            nickname = nickname ?: currentUser.nickname,
            bio = bio ?: currentUser.bio,
            profileImageUrl = profileUrl ?: currentUser.profileImageUrl
        )

        return userDataSource.updateUser(updatedUser)
    }

    override suspend fun followUser(targetUserId: String): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("Not logged in"))

        return userDataSource.followUser(myUid, targetUserId)
    }

    override suspend fun unfollowUser(targetUserId: String): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("Not logged in"))

        return userDataSource.unfollowUser(myUid, targetUserId)
    }

    override suspend fun getFollowers(userId: String): DataResourceResult<List<User>> {
        val followersResult = userDataSource.getFollowers(userId)

        return if (followersResult is DataResourceResult.Success) {
            val followers = followersResult.data
            val myUid = authDataSource.getCurrentUserId()

            if (myUid != null) {
                val myProfile = userDataSource.getUser(myUid)
                val myFollowingIds = if (myProfile is DataResourceResult.Success) {
                    myProfile.data.followingIds.toSet()
                } else emptySet()

                val resultUsers = followers.map { user ->
                    user.copy(
                        relationState = user.relationState.copy(
                            isFollowing = myFollowingIds.contains(user.id)
                        )
                    )
                }
                DataResourceResult.Success(resultUsers)
            } else {
                DataResourceResult.Success(followers)
            }
        } else {
            followersResult
        }
    }

    override suspend fun getFollowings(userId: String): DataResourceResult<List<User>> {
        val userResult = userDataSource.getUser(userId)

        return if (userResult is DataResourceResult.Success) {
            val followingIds = userResult.data.followingIds

            if (followingIds.isNotEmpty()) {
                val usersResult = userDataSource.getUsersByIds(followingIds)

                if (usersResult is DataResourceResult.Success) {
                    val users = usersResult.data
                    val myUid = authDataSource.getCurrentUserId()

                    val resultUsers = if (userId == myUid) {
                        users.map { user ->
                            user.copy(relationState = user.relationState.copy(isFollowing = true))
                        }
                    } else {
                        val myProfile = if (myUid != null) userDataSource.getUser(myUid) else null
                        val myFollowings = if (myProfile is DataResourceResult.Success) {
                            myProfile.data.followingIds.toSet()
                        } else emptySet()

                        users.map { user ->
                            user.copy(
                                relationState = user.relationState.copy(
                                    isFollowing = myFollowings.contains(user.id)
                                )
                            )
                        }
                    }

                    DataResourceResult.Success(resultUsers)
                } else {
                    usersResult
                }
            } else {
                DataResourceResult.Success(emptyList())
            }
        } else {
            DataResourceResult.Failure(Exception("Failed to fetch user"))
        }
    }

    override fun getFollowingIdsFlow(userId: String): Flow<DataResourceResult<List<String>>> {
        return userDataSource.getFollowingIdsFlow(userId)
    }

    override suspend fun getUserBadges(userId: String): DataResourceResult<List<UserBadge>> {
        val remoteBadgesResult = userDataSource.getUserBadges(userId)

        if (remoteBadgesResult is DataResourceResult.Success) {
            val remoteBadges = remoteBadgesResult.data

            val combinedBadges = remoteBadges.mapNotNull { remoteBadge ->
                val libraryInfo = BadgeLibrary.findById(remoteBadge.id)

                if (libraryInfo != null) {
                    remoteBadge.copy(
                        name = libraryInfo.title,
                        description = libraryInfo.description,
                        imageUrl = libraryInfo.imageUrl
                    )
                } else {
                    null
                }
            }
            return DataResourceResult.Success(combinedBadges)
        }

        return remoteBadgesResult
    }

    override suspend fun syncFcmToken(isEnabled: Boolean): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("Login required"))

        return try {
            if (isEnabled) {
                val token = FirebaseMessaging.getInstance().token.await()
                userDataSource.updateFcmToken(myUid, token)
            } else {
                userDataSource.updateFcmToken(myUid, null)
            }
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun searchUsers(
        query: String,
        lastUserId: String?,
        limit: Int
    ): DataResourceResult<List<User>> {
        return userDataSource.searchUsers(query, lastUserId, limit)
    }

    override suspend fun checkNicknameAvailability(nickname: String): DataResourceResult<Boolean> {
        return try {
            val isDuplicate = userDataSource.isNicknameDuplicate(nickname)
            DataResourceResult.Success(!isDuplicate)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }
}