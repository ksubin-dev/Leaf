package com.subin.leafy.domain.usecase.tea

import com.subin.leafy.domain.model.TeaItem
import com.subin.leafy.domain.repository.TeaRepository
import javax.inject.Inject

class ScheduleTeaUploadUseCase @Inject constructor(
    private val repository: TeaRepository
) {
    suspend operator fun invoke(tea: TeaItem, imageUriString: String?) {
        repository.scheduleTeaUpload(tea, imageUriString)
    }
}