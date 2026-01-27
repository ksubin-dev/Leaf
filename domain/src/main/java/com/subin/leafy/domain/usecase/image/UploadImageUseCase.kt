package com.subin.leafy.domain.usecase.image

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.ImageRepository
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(uri: String, folder: String): DataResourceResult<String> {
        if (uri.isBlank()) {
            return DataResourceResult.Failure(Exception("이미지 경로가 비어있습니다."))
        }
        return imageRepository.uploadImage(uri, folder)
    }
}