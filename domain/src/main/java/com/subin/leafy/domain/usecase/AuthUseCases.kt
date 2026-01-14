package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.auth.*

data class AuthUseCases(
    val signUp: SignUpUseCase,
    val login: LoginUseCase,
    val logout: LogoutUseCase,
    val checkLoginStatus: CheckLoginStatusUseCase,
    val checkNickname: CheckNicknameUseCase,
    val deleteAccount: DeleteAccountUseCase
)