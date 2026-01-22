package com.subin.leafy.data.remote.fakes

import com.subin.leafy.data.datasource.CommunityDataSource
import com.subin.leafy.data.model.dto.CommunityPostDTO
import com.subin.leafy.data.mapper.toDomainList
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.CommunityTag
import com.subin.leafy.domain.model.TeaMaster
import com.leafy.shared.R as SharedR

class FakeCommunityDataSourceImpl : CommunityDataSource {

    override suspend fun getPopularNotes(): DataResourceResult<List<CommunityPost>> = runCatching {
        // 먼저 DTO 리스트를 정의 (나중엔 Firebase에서 가져올 객체들)
        val dtoList = listOf(
            createPostDTO("1", "프리미엄 제주 녹차", "Felix", SharedR.drawable.ic_sample_tea_1, 4.8f, 234),
            createPostDTO("2", "다즐링 퍼스트 플러시", "Sarah", SharedR.drawable.ic_sample_tea_2, 4.6f, 189)
        )
        // 2. Mapper 사용: DTO -> Domain Model 변환 후 Success 반환
        DataResourceResult.Success(dtoList.toDomainList())
    }.getOrElse {
        DataResourceResult.Failure(it)
    }

    override suspend fun getRisingNotes(): DataResourceResult<List<CommunityPost>> = runCatching {
        val dtoList = listOf(
            createPostDTO("3", "자스민 그린티", "TeaLover", SharedR.drawable.ic_sample_tea_2, 4.7f, 120, isLiked = true, likeCount = 35),
            createPostDTO("4", "카모마일 허브티", "ZenMaster", SharedR.drawable.ic_sample_tea_3, 4.6f, 98, likeCount = 18)
        )
        DataResourceResult.Success(dtoList.toDomainList())
    }.getOrElse {
        DataResourceResult.Failure(it)
    }

    override suspend fun getMostSavedNotes(): DataResourceResult<List<CommunityPost>> = runCatching {
        val dtoList = listOf(
            createPostDTO("5", "백모단 화이트티", "", SharedR.drawable.ic_sample_tea_7, 4.7f, 987),
            createPostDTO("6", "루이보스 바닐라", "", SharedR.drawable.ic_sample_tea_6, 4.2f, 854)
        )
        DataResourceResult.Success(dtoList.toDomainList())
    }.getOrElse {
        DataResourceResult.Failure(it)
    }

    override suspend fun getRecommendedMasters(): DataResourceResult<List<TeaMaster>> = runCatching {
        // TeaMaster도 DTO와 Mapper가 있다면 같은 방식으로 작성 (현재는 직접 생성)
        val masters = listOf(
            TeaMaster("m1", "그린티 마니아", "녹차 & 말차 전문가", SharedR.drawable.ic_profile_4, false),
            TeaMaster("m2", "허브티 큐레이터", "허브티 & 웰니스 컨설턴트", SharedR.drawable.ic_profile_5, false)
        )
        DataResourceResult.Success(masters)
    }.getOrElse {
        DataResourceResult.Failure(it)
    }

    override suspend fun getPopularTags(): DataResourceResult<List<CommunityTag>> = runCatching {
        val tags = listOf(
            CommunityTag("t1", "녹차"),
            CommunityTag("t2", "우롱차"),
            CommunityTag("t3", "가향홍차")
        )
        DataResourceResult.Success(tags)
    }.getOrElse {
        DataResourceResult.Failure(it)
    }

    override suspend fun getFollowingFeed(): DataResourceResult<List<CommunityPost>> = runCatching {
        val dtoList = listOf(
            CommunityPostDTO(
                _id = "f1",
                authorName = "민지",
                authorProfileUrl = SharedR.drawable.ic_profile_1,
                title = "동정오룡차",
                subtitle = "대만 · 중배화 · 반구형",
                content = "은은한 꽃향과 부드러운 과일향이 조화롭게 어우러진 오룽차...",
                teaTag = "Oolong",
                imageRes = SharedR.drawable.ic_sample_tea_7,
                rating = 4.5f,
                metaInfo = "95℃ · 3m · 5g",
                likeCount = 23,
                createdAt = "2시간 전"
            )
        )
        DataResourceResult.Success(dtoList.toDomainList())
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun toggleLike(postId: String): DataResourceResult<Boolean> = runCatching {
        DataResourceResult.Success(true)
    }.getOrElse {
        DataResourceResult.Failure(it)
    }

    override suspend fun toggleFollow(masterId: String): DataResourceResult<Boolean> = runCatching {
        DataResourceResult.Success(true)
    }.getOrElse {
        DataResourceResult.Failure(it)
    }

    // ───── Helper: 반복되는 DTO 생성을 위한 함수 ─────
    private fun createPostDTO(
        id: String, title: String, author: String, img: Int,
        rating: Float, saved: Int, isLiked: Boolean = false, likeCount: Int = 0
    ) = CommunityPostDTO(
        _id = id,
        authorName = author,
        title = title,
        imageRes = img,
        rating = rating,
        savedCount = saved,
        isLiked = isLiked,
        likeCount = likeCount,
        createdAt = "1일 전"
    )
}