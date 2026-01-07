package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.UserDto
import com.subin.leafy.data.model.dto.UserStatsDto
import com.subin.leafy.domain.model.*


// 1. UserDto -> User
fun UserDto.toUserDomain() = User(
    id = uid,
    nickname = displayName,
    profileImageUrl = photoUrl,
    bio = bio ?: "",
    socialStats = UserSocialStatistics(
        followerCount = followerCount,
        followingCount = followingCount
    ),
    relationState = UserRelationState(isFollowing = false),
    followingIds = followingIds,
    likedPostIds = likedPostIds,
    savedPostIds = savedPostIds,
    createdAt = createdAt
)

// 2. UserDto -> AuthUser (로그인한 본인 정보)
fun UserDto.toAuthDomain(
    email: String,
    isNewUser: Boolean = false,
    providerId: String? = null
) = AuthUser(
    id = uid,
    email = email,
    nickname = displayName,
    profileUrl = photoUrl,
    followingIds = followingIds,
    likedPostIds = likedPostIds,
    savedPostIds = savedPostIds,
    fcmToken = fcmToken,
    isNewUser = isNewUser,
    providerId = providerId
)

// 3. UserDto -> PostAuthor (티 마스터 추천 리스트용)
fun UserDto.toTeaMaster(isFollowing: Boolean = false) = PostAuthor(
    id = this.uid,
    nickname = this.displayName,
    profileImageUrl = this.photoUrl,
    isFollowing = isFollowing
)

// 4. UserStatsDto -> UserStats (통계 데이터 가공)
fun UserStatsDto.toDomain() = UserStats(
    totalBrewingCount = totalBrewingCount,
    currentStreakDays = currentStreakDays,
    monthlyBrewingCount = monthlyBrewingCount,
    preferredTimeSlot = mostFrequentTimeSlot,
    averageBrewingTime = formatBrewTime(avgBrewTimeSeconds),
    preferredTemperature = favoriteTemperature,
    preferredBrewingSeconds = favoriteBrewTimeSeconds,
    weeklyCount = weeklyBrewingCount,
    dailyCaffeineAvg = dailyCaffeineAvgMg,
    weeklyCaffeineTrend = weeklyCaffeineTrendMg,
    averageRating = avgRating,
    favoriteTeaType = favoriteTeaName,
    teaTypeDistribution = teaTypeDistribution,
    myTeaChestCount = teaInventoryCount,
    wishlistCount = wishlistCount
)

// 5. AuthUser -> UserDto (로그인 정보 업데이트/저장용)
// 사용자가 프로필을 수정하거나 FCM 토큰이 바뀔 때 서버로 보낼 데이터를 만듭니다.
fun AuthUser.toDto(currentDto: UserDto) = currentDto.copy(
    uid = this.id,
    displayName = this.nickname ?: currentDto.displayName,
    photoUrl = this.profileUrl ?: currentDto.photoUrl,
    fcmToken = this.fcmToken,
    followingIds = this.followingIds,
    likedPostIds = this.likedPostIds,
    savedPostIds = this.savedPostIds
)

// --- 보조 함수 ---
private fun formatBrewTime(seconds: Int): String {
    val min = seconds / 60
    val sec = seconds % 60
    return if (min > 0) "${min}분 ${sec}초" else "${sec}초"
}

/**
 * 리스트 변환 헬퍼
 */
fun List<UserDto>.toTeaMasterList() = this.map { it.toTeaMaster() }