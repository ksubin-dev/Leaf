package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.home.GetHomeContentUseCase
import javax.inject.Inject

data class HomeUseCases @Inject constructor(
    val getHomeContent: GetHomeContentUseCase
)