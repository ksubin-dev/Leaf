package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.user.*

data class UserUseCases(
    val getCurrentUserId: GetCurrentUserIdUseCase,
    val getMyProfile: GetMyProfileUseCase,
    val getUserProfile: GetUserProfileUseCase,
    val updateProfile: UpdateProfileUseCase,
    val followUser: FollowUserUseCase,
    val getFollowList: GetFollowListUseCase,
    val getUserBadges: GetUserBadgesUseCase,
    val searchUsers: SearchUsersUseCase,
    val checkNickname: CheckProfileNicknameUseCase
)