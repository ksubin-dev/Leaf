package com.subin.leafy.data.util

enum class BadgeLibrary(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String // 로컬 리소스 ID나 고정된 URL
) {
    // 1. 시작이 반 (첫 기록)
    FIRST_BREW(
        id = "badge_start_01",
        title = "설레는 첫 잔",
        description = "첫 번째 시음 기록을 남기셨군요! 차의 세계에 오신 걸 환영합니다.",
        imageUrl = "https://firebasestorage.../badge_first.png"
    ),

    // 2. 꾸준함의 미학 (10회 기록)
    BREWING_ENTHUSIAST(
        id = "badge_log_10",
        title = "차 애호가",
        description = "벌써 10번이나 차를 즐기셨네요. 습관이 되어가고 있어요!",
        imageUrl = "https://firebasestorage.../badge_10.png"
    ),

    // 3. 차 마스터 (100회)
    TEA_MASTER(
        id = "badge_log_100",
        title = "진정한 티 마스터",
        description = "100번의 우림. 이제 눈 감고도 차를 우리시겠군요.",
        imageUrl = "https://firebasestorage.../badge_100.png"
    ),

    // 4. 취향 확고 (한 종류만 5번 연속)
    ONE_LOVE(
        id = "badge_style_one",
        title = "한 우물 파기",
        description = "한 가지 차 종류를 진득하게 즐길 줄 아는 당신!",
        imageUrl = "https://firebasestorage.../badge_one_love.png"
    );

    // 뱃지 ID로 Enum 찾기 (Helper)
    companion object {
        fun findById(id: String): BadgeLibrary? {
            return entries.find { it.id == id }
        }
    }
}