package com.subin.leafy.domain.usecase.image

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.ImageRepository
import javax.inject.Inject

class DeleteImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(imageUrl: String): DataResourceResult<Unit> {
        if (imageUrl.isBlank()) {
            return DataResourceResult.Failure(Exception("삭제할 이미지 URL이 유효하지 않습니다."))
        }
        return imageRepository.deleteImage(imageUrl)
    }
}