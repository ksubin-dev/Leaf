package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.UserDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class UserRepositoryImpl(
    private val dataSource: UserDataSource
) : UserRepository {

    override suspend fun getCurrentUserId(): String? {
        return dataSource.getCurrentUserId()
    }

    override fun getUser(userId: String): Flow<DataResourceResult<User>> = flow {
        emit(dataSource.getUser(userId))
    }.onStart {
        emit(DataResourceResult.Loading)
    }.catch { e ->
        emit(DataResourceResult.Failure(e))
    }.flowOn(Dispatchers.IO)


    override fun updateProfile(user: User): Flow<DataResourceResult<Unit>> = flow<DataResourceResult<Unit>> {
        //val result = dataSource.updateUser(user)
        //    emit(result) => 나중에 user에 추가해줘야함
        emit(DataResourceResult.Success(Unit))
    }.onStart {
        emit(DataResourceResult.Loading)
    }.catch { e ->
        emit(DataResourceResult.Failure(e))
    }.flowOn(Dispatchers.IO)
}