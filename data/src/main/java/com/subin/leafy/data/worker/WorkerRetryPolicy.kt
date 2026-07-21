package com.subin.leafy.data.worker

import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import com.subin.leafy.domain.common.DataResourceResult

internal const val MAX_RETRY_COUNT = 3

internal fun CoroutineWorker.retryOrFailure(): ListenableWorker.Result {
    return if (runAttemptCount >= MAX_RETRY_COUNT) {
        ListenableWorker.Result.failure()
    } else {
        ListenableWorker.Result.retry()
    }
}

internal fun CoroutineWorker.resultFor(resourceResult: DataResourceResult<*>): ListenableWorker.Result {
    return when (resourceResult) {
        is DataResourceResult.Success -> ListenableWorker.Result.success()
        is DataResourceResult.Failure -> {
            if (resourceResult.exception.isRetryableWorkerFailure()) {
                retryOrFailure()
            } else {
                ListenableWorker.Result.failure()
            }
        }
        DataResourceResult.Loading -> retryOrFailure()
    }
}

internal fun CoroutineWorker.resultForException(exception: Throwable): ListenableWorker.Result {
    return if (exception.isRetryableWorkerFailure()) {
        retryOrFailure()
    } else {
        ListenableWorker.Result.failure()
    }
}

private fun Throwable.isRetryableWorkerFailure(): Boolean {
    val text = listOfNotNull(message, cause?.message)
        .joinToString(separator = " ")
        .lowercase()

    val nonRetryableSignals = listOf(
        "로그인",
        "login",
        "unauthenticated",
        "permission",
        "권한",
        "invalid",
        "유효하지",
        "검증",
        "validation",
        "not found",
        "찾을 수"
    )

    return nonRetryableSignals.none { signal -> text.contains(signal) }
}
