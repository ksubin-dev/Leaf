package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.auth.GetAuthUserUseCase
import com.subin.leafy.domain.usecase.auth.LoginUseCase
import com.subin.leafy.domain.usecase.auth.LogoutUseCase
import com.subin.leafy.domain.usecase.auth.SignUpUseCase

data class AuthUseCases(
    val signUp: SignUpUseCase,
    val login: LoginUseCase,
    val logout: LogoutUseCase,
    val getAuthUser: GetAuthUserUseCase
)