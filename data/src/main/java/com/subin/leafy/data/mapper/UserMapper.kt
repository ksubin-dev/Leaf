package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.UserDto
import com.subin.leafy.data.model.dto.UserAnalysisDto
import com.subin.leafy.domain.model.*

// 1. UserDto(Remote) -> User(Domain)
fun UserDto.toUserDomain() = User(
    id = uid,
    nickname = nickname,
    profileImageUrl = profileImageUrl,
    bio = bio ?: "",
    socialStats = UserSocialStatistics(
        followerCount = followerCount,
        followingCount = followingCount
    ),
    // 팔로우 여부는 Repository에서 계산해서 넣어주므로 초기값은 false
    relationState = UserRelationState(isFollowing = false),
    followingIds = followingIds,
    likedPostIds = likedPostIds,
    savedPostIds = savedPostIds,
    createdAt = createdAt
)

// 2. UserDto -> AuthUser (로그인한 내 정보)
fun UserDto.toAuthDomain(
    email: String,
    isNewUser: Boolean = false,
    providerId: String? = null
) = AuthUser(
    id = uid,
    email = email,
    nickname = nickname,
    profileUrl = profileImageUrl,
    followingIds = followingIds,
    likedPostIds = likedPostIds,
    savedPostIds = savedPostIds,
    fcmToken = fcmToken,
    isNewUser = isNewUser,
    providerId = providerId
)

// 3. UserDto -> PostAuthor (게시글 작성자 정보 등)
fun UserDto.toTeaMaster(isFollowing: Boolean = false) = PostAuthor(
    id = this.uid,
    nickname = this.nickname,
    profileImageUrl = this.profileImageUrl,
    isFollowing = isFollowing
)

// 4. UserAnalysisDto(Local) -> UserAnalysis(Domain)
fun UserAnalysisDto.toDomain() = UserAnalysis(
    totalBrewingCount = totalBrewingCount,
    currentStreakDays = currentStreakDays,
    monthlyBrewingCount = monthlyBrewingCount,

    // 분석 데이터 매핑
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

// 5. AuthUser -> UserDto (업데이트용)
fun AuthUser.toDto(currentDto: UserDto) = currentDto.copy(
    uid = this.id,
    nickname = this.nickname ?: currentDto.nickname,
    profileImageUrl = this.profileUrl ?: currentDto.profileImageUrl,
    fcmToken = this.fcmToken,
    followingIds = this.followingIds,
    likedPostIds = this.likedPostIds,
    savedPostIds = this.savedPostIds
)

// --- 보조 함수 ---
private fun formatBrewTime(seconds: Int): String {
    if (seconds == 0) return "-"
    val min = seconds / 60
    val sec = seconds % 60
    return if (min > 0) "${min}분 ${sec}초" else "${sec}초"
}


fun List<UserDto>.toTeaMasterList() = this.map { it.toTeaMaster() }