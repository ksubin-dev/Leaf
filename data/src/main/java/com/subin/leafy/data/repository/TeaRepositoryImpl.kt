package com.subin.leafy.data.repository
import com.subin.leafy.data.datasource.local.LocalTeaDataSource
import com.subin.leafy.data.datasource.remote.AuthDataSource
import com.subin.leafy.data.datasource.remote.RemoteTeaDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TeaItem
import com.subin.leafy.domain.repository.TeaRepository
import kotlinx.coroutines.flow.Flow

class TeaRepositoryImpl(
    private val localTeaDataSource: LocalTeaDataSource,
    private val remoteTeaDataSource: RemoteTeaDataSource,
    private val authDataSource: AuthDataSource
) : TeaRepository {

    // =================================================================
    // 1. 조회 (Local First Strategy)
    // - UI는 오직 로컬 DB만 바라봅니다.
    // =================================================================

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


    // =================================================================
    // 2. 저장 및 수정 (Local + Remote Backup)
    // =================================================================

    override suspend fun saveTea(tea: TeaItem): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))
        val teaToSave = tea.copy(ownerId = myUid)

        return try {
            // (1) 로컬 DB 저장 (UI 즉시 반영)
            // Room은 OnConflictStrategy.REPLACE라 수정 시에도 이 함수를 씁니다.
            localTeaDataSource.insertTea(teaToSave)

            // (2) 리모트 DB 백업 (실패해도 로컬엔 저장됐으니 성공으로 간주하거나,
            // 추후 WorkManager로 재시도 로직을 붙일 수 있음)
            remoteTeaDataSource.saveTea(teaToSave)

            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            // 로컬 저장 실패 시에만 에러 반환
            DataResourceResult.Failure(e)
        }
    }


    // =================================================================
    // 3. 삭제
    // =================================================================

    override suspend fun deleteTea(id: String): DataResourceResult<Unit> {
        return try {
            localTeaDataSource.deleteTea(id)
            remoteTeaDataSource.deleteTea(id)
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }


    // =================================================================
    // 4. 편의 기능 (즐겨찾기)
    // =================================================================

    override suspend fun toggleFavorite(teaId: String): DataResourceResult<Unit> {
        // 1. 현재 상태 가져오기
        val tea = localTeaDataSource.getTea(teaId)
            ?: return DataResourceResult.Failure(Exception("해당 차를 찾을 수 없습니다."))

        // 2. 상태 반전 (True <-> False)
        val updatedTea = tea.copy(isFavorite = !tea.isFavorite)

        // 3. 저장 로직 재사용 (Local + Remote 모두 업데이트됨)
        return saveTea(updatedTea)
    }


    // =================================================================
    // 5. 동기화 (Sync)
    // - 새 기기 로그인 시 서버 백업본을 로컬로 가져오는 역할
    // =================================================================

    override suspend fun syncTeas(): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        // (1) 서버에서 내 백업 데이터 가져오기
        val result = remoteTeaDataSource.getMyBackupTeas(myUid)

        return if (result is DataResourceResult.Success) {
            val remoteTeas = result.data

            // (2) 로컬 DB에 일괄 삽입 (이미 있으면 덮어쓰기)
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