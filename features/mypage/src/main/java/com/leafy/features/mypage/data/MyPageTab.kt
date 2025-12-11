package com.leafy.features.mypage.data

import androidx.annotation.DrawableRes
import com.leafy.shared.R as SharedR

/**
 * 마이페이지에서 사용되는 탭의 종류를 정의하는 상수 enum 클래스.
 */
enum class MyPageTab(@DrawableRes val iconRes: Int? = null) {
    CALENDAR(SharedR.drawable.ic_calendar),
    ANALYTICS(SharedR.drawable.ic_analytics),
    COLLECTION(SharedR.drawable.ic_leaf),
    WISHLIST(SharedR.drawable.ic_like),
    BADGES(SharedR.drawable.ic_badge)
}