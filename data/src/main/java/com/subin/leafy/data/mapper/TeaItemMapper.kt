package com.subin.leafy.data.mapper

import com.subin.leafy.data.datasource.local.room.entity.TeaEntity
import com.subin.leafy.data.model.dto.TeaItemDto
import com.subin.leafy.domain.model.TeaItem
import com.subin.leafy.domain.model.TeaType

// =================================================================
// 1. DTO (Server/DataStore) <-> Domain
// =================================================================

fun TeaItemDto.toDomain() = TeaItem(
    id = this.id,
    ownerId = this.ownerId,
    name = this.name,
    brand = this.brand,
    type = runCatching { TeaType.valueOf(this.type) }.getOrDefault(TeaType.ETC),
    origin = this.origin,
    leafStyle = this.leafStyle,
    grade = this.grade,
    imageUrl = this.imageUrl,
    isFavorite = this.isFavorite,
    stockQuantity = this.stockQuantity,
    memo = this.memo,
    createdAt = this.createdAt
)

fun TeaItem.toDto() = TeaItemDto(
    id = this.id,
    ownerId = this.ownerId,
    name = this.name,
    brand = this.brand,
    type = this.type.name,
    origin = this.origin,
    leafStyle = this.leafStyle,
    grade = this.grade,
    imageUrl = this.imageUrl,
    isFavorite = this.isFavorite,
    stockQuantity = this.stockQuantity,
    memo = this.memo,
    createdAt = this.createdAt
)

// =================================================================
// 2. Entity (Room DB) <-> Domain
// =================================================================

fun TeaEntity.toDomain() = TeaItem(
    id = this.id,
    ownerId = this.ownerId,
    name = this.name,
    brand = this.brand,
    type = runCatching { TeaType.valueOf(this.type) }.getOrDefault(TeaType.ETC),
    origin = this.origin,
    leafStyle = this.leafStyle,
    grade = this.grade,
    imageUrl = this.imageUrl,
    isFavorite = this.isFavorite,
    stockQuantity = this.stockQuantity,
    memo = this.memo,
    createdAt = this.createdAt
)

fun TeaItem.toEntity() = TeaEntity(
    id = this.id,
    ownerId = this.ownerId,
    name = this.name,
    brand = this.brand,
    type = this.type.name,
    origin = this.origin,
    leafStyle = this.leafStyle,
    grade = this.grade,
    imageUrl = this.imageUrl,
    isFavorite = this.isFavorite,
    stockQuantity = this.stockQuantity,
    memo = this.memo,
    createdAt = this.createdAt
)

// =================================================================
// 3. 리스트 변환 헬퍼
// =================================================================

fun List<TeaItemDto>.toDomainList() = this.map { it.toDomain() }
fun List<TeaEntity>.toTeaDomainList() = this.map { it.toDomain() }