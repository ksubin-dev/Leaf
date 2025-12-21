package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.UserDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.id.UserId
import com.subin.leafy.domain.repository.UserRepository

class UserRepositoryImpl(
    private val dataSource: UserDataSource
) : UserRepository {

    override suspend fun getCurrentUserId(): UserId {
        return dataSource.getCurrentUserId()
    }

    override suspend fun getUser(userId: UserId): User {
        val result = dataSource.getUser(userId)
        return if (result is DataResourceResult.Success) result.data
        else throw Exception("User not found")
    }

    override suspend fun updateProfile(user: User) {
        // TODO: 나중에 DataSource에 update 기능을 추가하면 여기서 호출합니다.
    }
}