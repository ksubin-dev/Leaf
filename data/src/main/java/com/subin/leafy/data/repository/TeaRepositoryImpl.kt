package com.subin.leafy.data.repository
import com.subin.leafy.data.datasource.local.LocalTeaDataSource
import com.subin.leafy.data.datasource.remote.AuthDataSource
import com.subin.leafy.data.datasource.remote.RemoteTeaDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TeaItem
import com.subin.leafy.domain.repository.TeaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TeaRepositoryImpl @Inject constructor(
    private val localTeaDataSource: LocalTeaDataSource,
    private val remoteTeaDataSource: RemoteTeaDataSource,
    private val authDataSource: AuthDataSource
) : TeaRepository {

    override fun getTeasFlow(): Flow<List<TeaItem>> {
        return localTeaDataSource.getTeasFlow()
    }

    override fun searchTeas(query: String): Flow<List<TeaItem>> {
        return localTeaDataSource.searchTeas(query)
    }

    override fun getTeaCountFlow(): Flow<Int> {
        return localTeaDataSource.getTeaCountFlow()
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
            // (2) 리모트 DB 백업 (실패해도 로컬엔 저장됐으니 성공으로 간주하거나,
            // 추후 WorkManager로 재시도 로직을 붙일 수 있음)
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

            if (remoteTeas.isNotEmpty()) {
                remoteTeas.forEach { tea ->
                    localTeaDataSource.insertTea(tea)
                }
            }
            DataResourceResult.Success(Unit)
        } else {
            DataResourceResult.Failure((result as DataResourceResult.Failure).exception)
        }
    }
}