package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.UserDTO
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.id.UserId

fun UserDTO.toDomain() = User(
    id = UserId(this.uid),
    username = this.displayName,
    profileImageUrl = this.photoUrl
)