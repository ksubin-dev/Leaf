package com.subin.leafy.data.remote.fakes

import com.subin.leafy.data.datasource.UserDataSource
import com.subin.leafy.data.mapper.toDomain
import com.subin.leafy.data.model.dto.UserDTO
import com.subin.leafy.data.model.dto.UserStatsDTO
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserStats

class FakeUserDataSourceImpl : UserDataSource {

    // 가짜 ID 값을 여기서 정의합니다.
    //FirestoreSource에서는 실제 Firebase UID를 반환
    private val fakeCurrentUserId = "mock_user_leafy"

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

    override suspend fun getCurrentUserId(): String? {
        return fakeCurrentUserId
    }

    override suspend fun getUser(userId: String): DataResourceResult<User> = runCatching {
        DataResourceResult.Success(fakeUserDto.toDomain())
    }.getOrElse {
        DataResourceResult.Failure(it)
    }

    override suspend fun getUserStats(userId: String): DataResourceResult<UserStats> = runCatching {
        DataResourceResult.Success(fakeStatsDto.toDomain())
    }.getOrElse {
        DataResourceResult.Failure(it)
    }
}