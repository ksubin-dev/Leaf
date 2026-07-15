package com.leafy.shared.utils

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
import java.util.UUID
import androidx.core.net.toUri

class ImageCompressor(private val context: Context) {

    /**
     * 이미지를 압축하여 캐시 파일로 저장하고, 그 경로를 반환합니다.
     * 지원 버전: API 26 이상 (28 미만은 BitmapFactory, 28 이상은 ImageDecoder 사용)
     */
    suspend fun compressImage(imageUriString: String): String = withContext(Dispatchers.IO) {
        val uri = imageUriString.toUri()
        val bitmap = decodeBitmap(uri, 1080)

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

    /**
     * 이미지를 앱 내부 저장소에 저장하고, 앱 재실행 후에도 접근 가능한 file Uri를 반환합니다.
     */
    suspend fun saveImageToInternalStorage(
        imageUriString: String,
        folderName: String,
        filePrefix: String = "image"
    ): String = withContext(Dispatchers.IO) {
        val uri = imageUriString.toUri()
        val imagesRoot = File(context.filesDir, INTERNAL_IMAGE_DIR)

        getExistingInternalImage(uri, imagesRoot)?.let { existingFile ->
            return@withContext Uri.fromFile(existingFile).toString()
        }

        val bitmap = decodeBitmap(uri, 1080)
        val targetDir = File(imagesRoot, folderName).apply { mkdirs() }
        val safePrefix = filePrefix.replace(Regex("[^A-Za-z0-9_-]"), "_").ifBlank { "image" }
        val file = File(targetDir, "${safePrefix}_${System.currentTimeMillis()}_${UUID.randomUUID()}.jpg")

        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        }
        bitmap.recycle()

        Uri.fromFile(file).toString()
    }

    private fun getExistingInternalImage(uri: Uri, imagesRoot: File): File? {
        if (uri.scheme != "file") return null

        val path = uri.path ?: return null
        val file = File(path)
        if (!file.exists()) return null

        return try {
            val rootPath = imagesRoot.canonicalPath
            val filePath = file.canonicalPath
            if (filePath == rootPath || filePath.startsWith("$rootPath${File.separator}")) file else null
        } catch (e: Exception) {
            null
        }
    }

    private fun decodeBitmap(uri: Uri, targetSize: Int): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                val (width, height) = info.size.width to info.size.height

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
            getBitmapLegacy(uri, targetSize)
        }
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

    companion object {
        private const val INTERNAL_IMAGE_DIR = "leafy_images"
    }
}
