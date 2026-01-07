package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.remote.UserDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.common.toResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class UserRepositoryImpl(
    private val dataSource: UserDataSource
) : UserRepository {

    override suspend fun getCurrentUserId(): String? = dataSource.getCurrentUserId()

    override fun getUser(userId: String): Flow<DataResourceResult<User>> =
        dataSource.getUserFlow(userId)
            .onStart { emit(DataResourceResult.Loading) }
            .catch { e -> emit(DataResourceResult.Failure(e)) }
            .flowOn(Dispatchers.IO)

    override fun updateProfile(user: User): Flow<DataResourceResult<Unit>> = flow {
        val result = dataSource.updateUser(user)
        emit(result)
    }.onStart {
        emit(DataResourceResult.Loading)
    }.catch { e ->
        // 예상치 못한 런타임 에러 발생 시 처리
        emit(DataResourceResult.Failure(e))
    }.flowOn(Dispatchers.IO)

    override fun toggleFollow(myId: String, targetUserId: String, isFollowing: Boolean): Flow<DataResourceResult<Unit>> = flow {
        emit(DataResourceResult.Loading)
        val result = if (isFollowing) dataSource.unfollowUser(myId, targetUserId)
        else dataSource.followUser(myId, targetUserId)
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun isFollowing(myId: String, targetUserId: String): Flow<DataResourceResult<Boolean>> = flow {
        emit(DataResourceResult.Success(dataSource.checkFollowStatus(myId, targetUserId)))
    }.flowOn(Dispatchers.IO)

    override fun getTopUsers(limit: Int): Flow<DataResourceResult<List<User>>> = flow {
        emit(DataResourceResult.Loading)
        emit(dataSource.fetchTopUsers(limit))
    }.flowOn(Dispatchers.IO)
}