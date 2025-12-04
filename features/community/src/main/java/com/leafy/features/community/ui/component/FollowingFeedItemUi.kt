package com.leafy.features.community.ui.component

import com.leafy.shared.R as SharedR

import androidx.annotation.DrawableRes

/**
 * Explore - Following 탭의 한 개 노트 카드에 필요한 데이터 모델
 */
data class ExploreFollowingNoteUi(
    val authorName: String,
    @DrawableRes val authorAvatarRes: Int,
    val timeAgo: String,

    // 상단 배지 (예: "Oolong", "Green Tea")
    val tagLabel: String,

    @DrawableRes val imageRes: Int,

    // 제목 (차 이름)
    val title: String,

    // 제목 아래 메타 정보 (예: "대만 · 중배화 · 반구형")
    val meta: String,

    // 짧은 설명 문장
    val description: String,

    // 온도/시간/그램/우림차수 칩
    val brewingChips: List<String>,

    // 별점
    val rating: Float,

    // 별점 오른쪽의 칩 (예: "Rebrew 가능")
    val reviewLabel: String,

    // 말풍선 안에 들어갈 코멘트
    val comment: String,

    // 하단에 겹쳐 보일 좋아요한 사람들의 아바타
    val likerAvatarResList: List<Int>,

    // "23명이 좋아합니다" 문구
    val likeCountText: String
) {
    companion object {

        fun sample1() = ExploreFollowingNoteUi(
            authorName = "민지",
            authorAvatarRes = SharedR.drawable.ic_profile_1,
            timeAgo = "2시간 전",
            tagLabel = "Oolong",
            imageRes = SharedR.drawable.ic_sample_tea_7,
            title = "동정오룡차",
            meta = "대만 · 중배화 · 반구형",
            description = "은은한 꽃향과 부드러운 과일향이 조화롭게 어우러진 오룽차, 목넘김이 매끄럽고 여운이 깁니다.",
            brewingChips = listOf("95℃", "3m", "5g", "1st Infusion"),
            rating = 4.5f,
            reviewLabel = "Rebrew 가능",
            comment = "오늘 아침에 마신 차 중 최고였어요. 3회까지 우려봤는데 2번째 우림이 가장 좋았답니다. 은은한 난향이 정말 매력적이에요. 😊",
            likerAvatarResList = listOf(
                SharedR.drawable.ic_profile_2,
                SharedR.drawable.ic_profile_3,
                SharedR.drawable.ic_profile_4
            ),
            likeCountText = "23명이 좋아합니다"
        )

        fun sample2() = ExploreFollowingNoteUi(
            authorName = "준호",
            authorAvatarRes = SharedR.drawable.ic_profile_2,
            timeAgo = "5시간 전",
            tagLabel = "Green Tea",
            imageRes = SharedR.drawable.ic_sample_tea_5,
            title = "일본 센차",
            meta = "일본 · 시즈오카 · 잔잔",
            description = "신선한 풀향과 깔끔한 감칠맛이 특징인 일본식 녹차입니다.",
            brewingChips = listOf("70℃", "1m 30s", "4g", "1st Infusion"),
            rating = 4.0f,
            reviewLabel = "Daily Drink",
            comment = "가볍게 매일 마시기 좋은 맛이에요. 텁텁하지 않고 깔끔해서 식사 후에도 딱 좋습니다.",
            likerAvatarResList = listOf(
                SharedR.drawable.ic_profile_1,
                SharedR.drawable.ic_profile_3
            ),
            likeCountText = "15명이 좋아합니다"
        )

        fun sample3() = ExploreFollowingNoteUi(
            authorName = "수진",
            authorAvatarRes = SharedR.drawable.ic_profile_3,
            timeAgo = "1일 전",
            tagLabel = "Black Tea",
            imageRes = SharedR.drawable.ic_sample_tea_4,
            title = "아삼 홍차",
            meta = "인도 · 아삼 · CTC",
            description = "진하고 묵직한 맛이 특징인 홍차로, 우유를 넣어 밀크티로 즐기기 좋습니다.",
            brewingChips = listOf("100℃", "4m", "3g", "1st Infusion"),
            rating = 5.0f,
            reviewLabel = "Milk Tea 추천",
            comment = "비 오는 날 아침에 마시기 딱 좋은 차예요. 유우 넣어서 밀크티로 만들었는데 정말 완벽했어요!",
            likerAvatarResList = listOf(
                SharedR.drawable.ic_profile_2,
                SharedR.drawable.ic_profile_4
            ),
            likeCountText = "31명이 좋아합니다"
        )
    }
}