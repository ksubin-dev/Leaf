package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.user.*
import javax.inject.Inject

data class UserUseCases @Inject constructor(
    val getCurrentUserId: GetCurrentUserIdUseCase,
    val getMyProfile: GetMyProfileUseCase,
    val getUserProfile: GetUserProfileUseCase,
    val updateProfile: UpdateProfileUseCase,
    val followUser: FollowUserUseCase,
    val getFollowList: GetFollowListUseCase,
    val getFollowingIdsFlow: GetFollowingIdsFlowUseCase,
    val getUserBadges: GetUserBadgesUseCase,
    val searchUsers: SearchUsersUseCase,
    val checkNickname: CheckProfileNicknameUseCase,
    val updateFcmToken: UpdateFcmTokenUseCase,
    val scheduleProfileUpdate: ScheduleProfileUpdateUseCase

)