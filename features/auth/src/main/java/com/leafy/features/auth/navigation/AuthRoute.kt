package com.leafy.features.auth.navigation

import kotlinx.serialization.Serializable

// 화면별 경로 정의
sealed interface AuthRoute {
    @Serializable
    data object Login : AuthRoute

    @Serializable
    data object SignUp : AuthRoute
}