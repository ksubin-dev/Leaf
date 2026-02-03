package com.subin.leafy.data.repository
import androidx.work.WorkManager
import com.google.gson.Gson
import com.subin.leafy.data.datasource.local.LocalTeaDataSource
import com.subin.leafy.data.datasource.remote.AuthDataSource
import com.subin.leafy.data.datasource.remote.RemoteTeaDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TeaItem
import com.subin.leafy.domain.repository.TeaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkRequest
import com.subin.leafy.data.worker.TeaUploadWorker
import java.util.concurrent.TimeUnit

class TeaRepositoryImpl @Inject constructor(
    private val localTeaDataSource: LocalTeaDataSource,
    private val remoteTeaDataSource: RemoteTeaDataSource,
    private val authDataSource: AuthDataSource,
    private val workManager: WorkManager
) : TeaRepository {

    override fun getTeasFlow(): Flow<List<TeaItem>> {
        val myUid = authDataSource.getCurrentUserId() ?: return flowOf(emptyList())
        return localTeaDataSource.getTeasFlow(myUid)
    }

    override fun searchTeas(query: String): Flow<List<TeaItem>> {
        val myUid = authDataSource.getCurrentUserId() ?: return flowOf(emptyList())
        return localTeaDataSource.searchTeas(myUid, query)
    }

    override fun getTeaCountFlow(): Flow<Int> {
        val myUid = authDataSource.getCurrentUserId() ?: return flowOf(0)
        return localTeaDataSource.getTeaCountFlow(myUid)
    }

    override suspend fun getTeaDetail(id: String): TeaItem? {
        return localTeaDataSource.getTea(id)
    }

    override suspend fun saveTea(tea: TeaItem): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))
        val teaToSave = tea.copy(ownerId = myUid)

        return try {
            localTeaDataSource.insertTea(teaToSave)
            remoteTeaDataSource.saveTea(teaToSave)

            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun deleteTea(id: String): DataResourceResult<Unit> {
        return try {
            localTeaDataSource.deleteTea(id)
            remoteTeaDataSource.deleteTea(id)
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun toggleFavorite(teaId: String): DataResourceResult<Unit> {
        val tea = localTeaDataSource.getTea(teaId)
            ?: return DataResourceResult.Failure(Exception("해당 차를 찾을 수 없습니다."))
        val updatedTea = tea.copy(isFavorite = !tea.isFavorite)
        return saveTea(updatedTea)
    }

    override suspend fun syncTeas(): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        val result = remoteTeaDataSource.getMyBackupTeas(myUid)

        return if (result is DataResourceResult.Success) {
            val remoteTeas = result.data

            try {
                localTeaDataSource.deleteMyAllTeas(myUid)

                if (remoteTeas.isNotEmpty()) {
                    localTeaDataSource.insertTeas(remoteTeas)
                }
                DataResourceResult.Success(Unit)
            } catch (e: Exception) {
                DataResourceResult.Failure(e)
            }
        } else {
            DataResourceResult.Failure((result as DataResourceResult.Failure).exception)
        }
    }

    override suspend fun scheduleTeaUpload(tea: TeaItem, imageUriString: String?) {

        val myUid = authDataSource.getCurrentUserId() ?: return
        val teaWithOwner = tea.copy(ownerId = myUid)

        val gson = Gson()
        val teaJson = gson.toJson(teaWithOwner)

        val inputData = Data.Builder()
            .putString(TeaUploadWorker.KEY_TEA_DATA, teaJson)
            .putString(TeaUploadWorker.KEY_IMAGE_URI, imageUriString)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadWorkRequest = OneTimeWorkRequestBuilder<TeaUploadWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag("upload_tea_${tea.id}")
            .build()

        workManager.enqueue(uploadWorkRequest)
    }
}