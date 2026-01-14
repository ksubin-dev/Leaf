package com.leafy.shared.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import androidx.core.net.toUri

class ImageCompressor(private val context: Context) {

    /**
     * 이미지를 압축하여 캐시 파일로 저장하고, 그 경로를 반환합니다.
     * 지원 버전: API 26 이상 (28 미만은 BitmapFactory, 28 이상은 ImageDecoder 사용)
     */
    suspend fun compressImage(imageUriString: String): String = withContext(Dispatchers.IO) {
        val uri = imageUriString.toUri()

        // 1. 비트맵 생성 (버전별 분기 처리)
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                val (width, height) = info.size.width to info.size.height
                val targetSize = 1080

                var sampleSize = 1
                if (width > targetSize || height > targetSize) {
                    val halfHeight = height / 2
                    val halfWidth = width / 2
                    while ((halfHeight / sampleSize) >= targetSize && (halfWidth / sampleSize) >= targetSize) {
                        sampleSize *= 2
                    }
                }
                decoder.setTargetSampleSize(sampleSize)
                decoder.isMutableRequired = true
            }
        } else {
            getBitmapLegacy(uri, 1080)
        }

        // 2. 압축 및 파일 저장 (공통 로직)
        // cacheDir에 저장해서 임시 파일로 관리
        val file = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        }
        // 비트맵 메모리 해제 (OOM 방지)
        bitmap.recycle()
        // 파일 경로를 Uri 문자열로 반환
        Uri.fromFile(file).toString()
    }

    // --- 구형 방식 (BitmapFactory) ---
    private fun getBitmapLegacy(uri: Uri, reqSize: Int): Bitmap {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }
        options.inSampleSize = calculateInSampleSize(options, reqSize, reqSize)
        options.inJustDecodeBounds = false

        return context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        } ?: throw Exception("이미지 로드 실패")
    }

    // 비율 계산 공식 (안드로이드 공식 문서)
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}