package com.subin.leafy.data.repository

import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.id.UserId
import com.subin.leafy.domain.repository.UserRepository

class FakeUserRepository : UserRepository {

    private val mockUser = User(
        id = UserId("mock_user_leafy"),
        username = "Leafy User",
        profileImageUrl = null
    )

    override suspend fun getCurrentUserId(): UserId {
        return mockUser.id
    }

    override suspend fun getUser(userId: UserId): User {
        return mockUser
    }

    override suspend fun updateProfile(user: User) {
        // mock이므로 no-op
    }
}