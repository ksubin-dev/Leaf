package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.image.DeleteImageUseCase
import com.subin.leafy.domain.usecase.image.UploadImageUseCase
import com.subin.leafy.domain.usecase.image.UploadImagesUseCase

data class ImageUseCases(
    val uploadImage: UploadImageUseCase,
    val uploadImages: UploadImagesUseCase,
    val deleteImage: DeleteImageUseCase
)