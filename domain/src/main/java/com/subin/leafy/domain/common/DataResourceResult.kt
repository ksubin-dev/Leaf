package com.subin.leafy.domain.common

sealed class DataResourceResult<out T> {
    data object DummyConstructor : DataResourceResult<Nothing>()
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
        is DataResourceResult.DummyConstructor -> DataResourceResult.DummyConstructor
    }
}

inline fun <T> Result<T>.toResourceResult(): DataResourceResult<T> {
    return fold(
        onSuccess = { DataResourceResult.Success(it) },
        onFailure = { DataResourceResult.Failure(it) }
    )
}

