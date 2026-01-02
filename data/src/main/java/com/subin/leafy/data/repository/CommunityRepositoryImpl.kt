package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.CommunityDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.CommunityTag
import com.subin.leafy.domain.model.TeaMaster
import com.subin.leafy.domain.repository.CommunityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class CommunityRepositoryImpl(
    private val targetDataSource: CommunityDataSource
) : CommunityRepository {

    private fun <T> wrapFlow(fetcher: () -> Flow<DataResourceResult<T>>): Flow<DataResourceResult<T>> =
        fetcher()
            .onStart { emit(DataResourceResult.Loading) }
            .catch { emit(DataResourceResult.Failure(it)) }
            .flowOn(Dispatchers.IO) // 이것도 힐트한테 관리 맡기기!! 강사님 코드에 있으니까 하나 만들어서 사용하기

    override fun getPopularNotes() = wrapFlow { targetDataSource.getPopularNotes() }

    override fun getRisingNotes() = wrapFlow { targetDataSource.getRisingNotes() }

    override fun getMostSavedNotes() = wrapFlow { targetDataSource.getMostSavedNotes() }

    override fun getFollowingFeed() = wrapFlow { targetDataSource.getFollowingFeed() }

    override fun getRecommendedMasters() = wrapFlow { targetDataSource.getRecommendedMasters() }

    override fun getPopularTags() = wrapFlow { targetDataSource.getPopularTags() }

    override suspend fun toggleLike(postId: String): DataResourceResult<Boolean> {
        return try {
            targetDataSource.toggleLike(postId)
        } catch (e: Exception) {
            // 여기가 예외처리하고 뷰모델은 받게끔 전체적으로 확인하고 수정
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun toggleFollow(masterId: String): DataResourceResult<Boolean> {
        return try {
            targetDataSource.toggleFollow(masterId)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }
}
//utils 패키지 만들어서 빼기 반복되는 코드들