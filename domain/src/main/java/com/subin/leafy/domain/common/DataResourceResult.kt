package com.subin.leafy.domain.common

sealed class DataResourceResult<out T> {
    data object Loading : DataResourceResult<Nothing>()
    data class Success<out T>(
        val data: T
    ) : DataResourceResult<T>()
    data class Failure(
        val exception: Throwable
    ) : DataResourceResult<Nothing>()
}

inline fun <T, R> DataResourceResult<T>.mapData(transform: (T) -> R): DataResourceResult<R> {
    return when (this) {
        is DataResourceResult.Success -> DataResourceResult.Success(transform(data))
        is DataResourceResult.Failure -> DataResourceResult.Failure(exception)
        is DataResourceResult.Loading -> DataResourceResult.Loading
    }
}

inline fun <T> runCatchingToResource(block: () -> T): DataResourceResult<T> {
    return try {
        DataResourceResult.Success(block())
    } catch (e: Exception) {
        DataResourceResult.Failure(e)
    }
}

