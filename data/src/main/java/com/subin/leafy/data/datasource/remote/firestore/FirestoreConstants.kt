package com.subin.leafy.data.datasource.remote.firestore

object FirestoreConstants {
    const val COLLECTION_USERS = "users"
    const val COLLECTION_BADGES = "badges"
    const val FIELD_POST_COUNT = "postCount"
    const val FIELD_NICKNAME = "nickname"
    const val FIELD_PROFILE_IMAGE = "profileImageUrl"
    const val FIELD_BIO = "bio"
    const val FIELD_EXPERT_TYPES = "expertTypes"

    const val FIELD_FOLLOWING_IDS = "followingIds"

    const val FIELD_LIKED_POST_IDS = "likedPostIds"
    const val FIELD_BOOKMARKED_POST_IDS = "bookmarkedPostIds"

    const val KEY_SOCIAL_STATS = "socialStats"
    const val KEY_FOLLOWER_COUNT = "followerCount"
    const val KEY_FOLLOWING_COUNT = "followingCount"
    const val FIELD_FOLLOWER_COUNT = "$KEY_SOCIAL_STATS.$KEY_FOLLOWER_COUNT"
    const val FIELD_FOLLOWING_COUNT = "$KEY_SOCIAL_STATS.$KEY_FOLLOWING_COUNT"

    const val FIELD_FCM_TOKEN = "fcmToken"

    const val COLLECTION_NOTES = "notes"
    const val FIELD_OWNER_ID = "ownerId"
    const val FIELD_IS_PUBLIC = "public"
    const val FIELD_CREATED_AT = "createdAt"

    const val FIELD_DATE = "date"

    const val COLLECTION_POSTS = "posts"
    const val COLLECTION_COMMENTS = "comments"

    const val FIELD_TITLE = "title"
    const val FIELD_AUTHOR_ID = "author.id"
    const val FIELD_TAGS = "tags"

    const val FIELD_TEA_TYPE = "teaType"

    const val KEY_STATS = "stats"
    const val KEY_VIEW_COUNT = "viewCount"
    const val KEY_LIKE_COUNT = "likeCount"
    const val KEY_BOOKMARK_COUNT = "bookmarkCount"
    const val KEY_COMMENT_COUNT = "commentCount"

    const val FIELD_VIEW_COUNT = "$KEY_STATS.$KEY_VIEW_COUNT"
    const val FIELD_LIKE_COUNT = "$KEY_STATS.$KEY_LIKE_COUNT"
    const val FIELD_BOOKMARK_COUNT = "$KEY_STATS.$KEY_BOOKMARK_COUNT"
    const val FIELD_COMMENT_COUNT = "$KEY_STATS.$KEY_COMMENT_COUNT"

    const val COLLECTION_NOTIFICATIONS = "notifications"
    const val FIELD_IS_READ = "isRead"

    const val FIELD_IS_NOTI_AGREED = "isNotificationAgreed"

    const val COLLECTION_HOME_CONTENTS = "home_contents"
    const val DOCUMENT_DAILY = "daily"

    const val COLLECTION_TEAS = "teas"
}