package com.subin.leafy.domain.usecase.home

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.HomeContent
import com.subin.leafy.domain.repository.HomeRepository

class GetHomeContentUseCase(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(): DataResourceResult<HomeContent> {
        return homeRepository.getHomeContent()
    }
}