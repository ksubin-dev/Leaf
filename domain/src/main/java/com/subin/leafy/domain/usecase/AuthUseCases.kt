package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.auth.*
import javax.inject.Inject

data class AuthUseCases @Inject constructor(
    val signUp: SignUpUseCase,
    val login: LoginUseCase,
    val logout: LogoutUseCase,
    val checkLoginStatus: CheckLoginStatusUseCase,
    val checkNickname: CheckNicknameUseCase,
    val deleteAccount: DeleteAccountUseCase
)