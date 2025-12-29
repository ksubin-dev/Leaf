package com.leafy.features.auth.navigation

import kotlinx.serialization.Serializable

sealed interface AuthRoute {
    @Serializable
    data object Login : AuthRoute

    @Serializable
    data object SignUp : AuthRoute
}