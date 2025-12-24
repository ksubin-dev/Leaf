package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.UserDTO
import com.subin.leafy.domain.model.User

fun UserDTO.toDomain() = User(
    id = this.uid,
    username = this.displayName,
    profileImageUrl = this.photoUrl
)