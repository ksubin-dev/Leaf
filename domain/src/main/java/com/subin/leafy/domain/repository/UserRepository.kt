package com.subin.leafy.domain.repository

import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.id.UserId

interface UserRepository {
    suspend fun getCurrentUserId(): UserId
    suspend fun getUser(userId: UserId): User
    suspend fun updateProfile(user: User)
}