package com.subin.leafy.ui.component

import com.leafy.shared.navigation.LeafyNavigation
import com.leafy.shared.navigation.MainNavigationRoute
import com.leafy.shared.R as SharedR

data class LeafyBottomAppBarItem(
    val tabName: String,
    val iconRes: Int,                 // 🔹 shared drawable resource id
    val destination: LeafyNavigation  // 🔹 이동할 타입 기반 Route
) {
    companion object {

        /**
         * Leafy 앱의 Bottom Navigation 탭 구성
         */
        fun fetchBottomAppBarItems(): List<LeafyBottomAppBarItem> = listOf(
            LeafyBottomAppBarItem(
                tabName = "Home",
                iconRes = SharedR.drawable.ic_nav_home,
                destination = MainNavigationRoute.HomeTab
            ),
            LeafyBottomAppBarItem(
                tabName = "Community",
                iconRes = SharedR.drawable.ic_nav_explore,
                destination = MainNavigationRoute.CommunityTab
            ),
            LeafyBottomAppBarItem(
                tabName = "Timer",
                iconRes = SharedR.drawable.ic_nav_timer,
                destination = MainNavigationRoute.TimerTab
            ),
            LeafyBottomAppBarItem(
                tabName = "Note",
                iconRes = SharedR.drawable.ic_nav_note,
                destination = MainNavigationRoute.NoteTab
            ),
            LeafyBottomAppBarItem(
                tabName = "My",
                iconRes = SharedR.drawable.ic_nav_mypage,
                destination = MainNavigationRoute.MyPageTab
            )
        )
    }
}