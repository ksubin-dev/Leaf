package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult

interface ImageRepository {
    // 1. 단일 이미지 업로드
    suspend fun uploadImage(uri: String, folder: String): DataResourceResult<String>

    // 2. 다중 이미지 업로드 (게시글 작성 시 필요)
    suspend fun uploadImages(uris: List<String>, folder: String): DataResourceResult<List<String>>

    // 3. 이미지 삭제
    suspend fun deleteImage(imageUrl: String): DataResourceResult<Unit>
}