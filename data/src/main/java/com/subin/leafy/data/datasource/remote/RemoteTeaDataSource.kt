package com.subin.leafy.data.datasource.remote

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TeaItem

interface RemoteTeaDataSource {

    // 1. 내 차 보관함 백업 가져오기 (동기화용)
    suspend fun getMyBackupTeas(userId: String): DataResourceResult<List<TeaItem>>

    // 2. 차 저장 (백업용 - 생성 및 수정 겸용)
    // - 로컬 DB에 저장된 후, 서버에도 동일하게 저장합니다.
    suspend fun saveTea(tea: TeaItem): DataResourceResult<Unit>

    // 3. 차 삭제 (동기화용)
    // - 로컬에서 삭제하면 서버에서도 삭제합니다.
    suspend fun deleteTea(teaId: String): DataResourceResult<Unit>
}