package com.subin.leafy.domain.usecase.image

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.ImageRepository
import javax.inject.Inject

class UploadImagesUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(uris: List<String>, folder: String): DataResourceResult<List<String>> {
        if (uris.isEmpty()) {
            return DataResourceResult.Failure(Exception("업로드할 이미지가 없습니다."))
        }
        return imageRepository.uploadImages(uris, folder)
    }
}