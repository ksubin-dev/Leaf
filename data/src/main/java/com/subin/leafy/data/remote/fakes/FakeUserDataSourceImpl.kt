package com.subin.leafy.data.remote.fakes

import com.subin.leafy.data.datasource.UserDataSource
import com.subin.leafy.data.mapper.toDomain
import com.subin.leafy.data.model.dto.UserDTO
import com.subin.leafy.data.model.dto.UserStatsDTO
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserStats
import com.subin.leafy.domain.model.id.UserId

class FakeUserDataSourceImpl : UserDataSource {

    // 가짜 ID 값을 여기서 정의합니다.
    //FirestoreSource에서는 실제 Firebase UID를 반환
    private val fakeCurrentUserId = UserId("mock_user_leafy")

    private val fakeUserDto = UserDTO(
        uid = "mock_user_leafy",
        displayName = "Leafy User",
        photoUrl = null
    )

    private val fakeStatsDto = UserStatsDTO(
        weeklyCount = 3,
        monthlyCount = 12,
        avgRating = 4.5,
        favoriteTea = "Oolong",
        avgBrewingTime = "3:00"
    )

    override suspend fun getCurrentUserId(): UserId {
        return fakeCurrentUserId
    }

    override suspend fun getUser(userId: UserId): DataResourceResult<User> = runCatching {
        DataResourceResult.Success(fakeUserDto.toDomain())
    }.getOrElse {
        DataResourceResult.Failure(it)
    }

    override suspend fun getUserStats(userId: UserId): DataResourceResult<UserStats> = runCatching {
        DataResourceResult.Success(fakeStatsDto.toDomain())
    }.getOrElse {
        DataResourceResult.Failure(it)
    }
}