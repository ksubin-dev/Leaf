package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.TeaMasterDto
import com.subin.leafy.domain.model.TeaMaster
import com.subin.leafy.domain.model.TeaType

// DTO -> Domain
fun TeaMasterDto.toDomain(isFollowing: Boolean = false) = TeaMaster(
    id = this.id,
    nickname = this.nickname,
    title = this.title,
    profileImageUrl = this.profileImageUrl,
    isFollowing = isFollowing,
    followerCount = this.followerCount,

    expertTypes = this.expertTypes.mapNotNull { typeName ->
        runCatching { TeaType.valueOf(typeName) }.getOrNull()
    }
)

fun List<TeaMasterDto>.toDomainList() = this.map { it.toDomain() }