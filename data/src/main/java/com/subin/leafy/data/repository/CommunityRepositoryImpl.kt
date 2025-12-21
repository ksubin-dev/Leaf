package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.CommunityDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.CommunityTag
import com.subin.leafy.domain.model.TeaMaster
import com.subin.leafy.domain.repository.CommunityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class CommunityRepositoryImpl(
    private val targetDataSource: CommunityDataSource
) : CommunityRepository {

    override fun getPopularNotes(): Flow<DataResourceResult<List<CommunityPost>>> = flow {
        emit(DataResourceResult.Loading)
        emit(targetDataSource.getPopularNotes())
    }.catch {
        emit(DataResourceResult.Failure(it))
    }.flowOn(Dispatchers.IO)

    override fun getRisingNotes(): Flow<DataResourceResult<List<CommunityPost>>> = flow {
        emit(DataResourceResult.Loading)
        emit(targetDataSource.getRisingNotes())
    }.catch {
        emit(DataResourceResult.Failure(it))
    }.flowOn(Dispatchers.IO)

    override fun getMostSavedNotes(): Flow<DataResourceResult<List<CommunityPost>>> = flow {
        emit(DataResourceResult.Loading)
        emit(targetDataSource.getMostSavedNotes())
    }.catch {
        emit(DataResourceResult.Failure(it))
    }.flowOn(Dispatchers.IO)

    override fun getRecommendedMasters(): Flow<DataResourceResult<List<TeaMaster>>> = flow {
        emit(DataResourceResult.Loading)
        emit(targetDataSource.getRecommendedMasters())
    }.catch {
        emit(DataResourceResult.Failure(it))
    }.flowOn(Dispatchers.IO)

    override fun getPopularTags(): Flow<DataResourceResult<List<CommunityTag>>> = flow {
        emit(DataResourceResult.Loading)
        emit(targetDataSource.getPopularTags())
    }.catch {
        emit(DataResourceResult.Failure(it))
    }.flowOn(Dispatchers.IO)

    override fun getFollowingFeed(): Flow<DataResourceResult<List<CommunityPost>>> = flow {
        emit(DataResourceResult.Loading)
        emit(targetDataSource.getFollowingFeed())
    }.catch {
        emit(DataResourceResult.Failure(it))
    }.flowOn(Dispatchers.IO)

    // 액션(CUD) 계열은 Flow 대신 직접 DataResourceResult 반환하거나
    // 필요에 따라 강사님처럼 Flow로 감쌀 수 있습니다.
    override suspend fun toggleLike(postId: String): DataResourceResult<Boolean> {
        return targetDataSource.toggleLike(postId)
    }

    override suspend fun toggleFollow(masterId: String): DataResourceResult<Boolean> {
        return targetDataSource.toggleFollow(masterId)
    }
}