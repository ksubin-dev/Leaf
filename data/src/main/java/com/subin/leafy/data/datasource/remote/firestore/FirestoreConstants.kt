package com.subin.leafy.data.datasource.remote.firestore

object FirestoreConstants {
    // ==========================================
    // 1. 유저 (User)
    // ==========================================
    const val COLLECTION_USERS = "users"
    const val COLLECTION_BADGES = "badges"

    const val FIELD_UID = "uid"
    const val FIELD_NICKNAME = "nickname"
    const val FIELD_PROFILE_IMAGE = "profileImageUrl"
    const val FIELD_BIO = "bio"
    const val FIELD_FOLLOWING_IDS = "followingIds"

    const val FIELD_LIKED_POST_IDS = "likedPostIds"
    const val FIELD_BOOKMARKED_POST_IDS = "bookmarkedPostIds"

    // 통계 필드
    const val FIELD_FOLLOWING_COUNT = "socialStats.followingCount"
    const val FIELD_FOLLOWER_COUNT = "socialStats.followerCount"

    // ==========================================
    // 2. 노트 (Note)
    // ==========================================
    const val COLLECTION_NOTES = "notes"
    const val FIELD_OWNER_ID = "ownerId"
    const val FIELD_IS_PUBLIC = "isPublic"
    const val FIELD_CREATED_AT = "createdAt"

    // ==========================================
    // 3. 커뮤니티 (Post & Comment & Notification)
    // ==========================================
    const val COLLECTION_POSTS = "posts"
    const val COLLECTION_COMMENTS = "comments"

    const val FIELD_TITLE = "title"
    const val FIELD_AUTHOR_ID = "author.id"
    const val FIELD_TAGS = "tags"

    //차 종류 필터링용
    const val FIELD_TEA_TYPE = "teaType"

    const val FIELD_VIEW_COUNT = "stats.viewCount"
    const val FIELD_LIKE_COUNT = "stats.likeCount"
    const val FIELD_BOOKMARK_COUNT = "stats.bookmarkCount"
    const val FIELD_COMMENT_COUNT = "stats.commentCount"

    const val COLLECTION_NOTIFICATIONS = "notifications"
    const val FIELD_IS_READ = "isRead"

    const val FIELD_IS_NOTI_AGREED = "isNotificationAgreed"

    // ==========================================
    // 4. 홈 (Home Content)
    // ==========================================
    const val COLLECTION_HOME_CONTENTS = "home_contents"
    const val DOCUMENT_DAILY = "daily"

    // 5. 티마스터 (Tea Master)
    const val COLLECTION_TEA_MASTERS = "tea_masters"
    const val FIELD_FOLLOWER_COUNT_SIMPLE = "followerCount"

    // 6. tea Item
    const val COLLECTION_TEAS = "teas"
}