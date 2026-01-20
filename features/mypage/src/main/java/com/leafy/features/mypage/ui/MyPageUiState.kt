package com.leafy.features.mypage.ui

import com.leafy.shared.ui.model.UserUiModel
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserAnalysis // [필수] 도메인 모델 import
import java.time.LocalDate

data class MyPageUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    // 1. 프로필 영역
    val myProfile: User? = null,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val followerList: List<UserUiModel> = emptyList(),
    val followingList: List<UserUiModel> = emptyList(),

    // 2. 분석 데이터 (UserAnalysis 하나로 통계 끝!)
    // 여기에 총 횟수, 스트릭, 이번달 횟수, 선호 시간대, 카페인 차트 데이터가 다 들어있음
    val userAnalysis: UserAnalysis? = null,

    // 3. 캘린더 & 기록 영역 (월별 조회용)
    // * UserAnalysis의 monthlyCount는 '이번 달' 고정이지만,
    // * 캘린더는 사용자가 '지난 달'로 이동할 수 있으므로 별도 리스트 유지 필요
    val currentYear: Int = LocalDate.now().year,
    val currentMonth: Int = LocalDate.now().monthValue,
    val calendarNotes: List<BrewingNote> = emptyList(), // 달력에 점 찍을 데이터

    val selectedDate: LocalDate = LocalDate.now(),
    val selectedDateNotes: List<BrewingNote> = emptyList(),

    // 4. 하단 섹션
    val bookmarkedPosts: List<CommunityPost> = emptyList(),
    val likedPosts: List<CommunityPost> = emptyList(),
    val myTeaCabinetCount: Int = 0
)