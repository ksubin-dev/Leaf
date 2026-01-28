package com.subin.leafy.domain.usecase.home

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.HomeContent
import com.subin.leafy.domain.repository.HomeRepository
import javax.inject.Inject

class GetHomeContentUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(): DataResourceResult<HomeContent> {
        return homeRepository.getHomeContent()
    }
}