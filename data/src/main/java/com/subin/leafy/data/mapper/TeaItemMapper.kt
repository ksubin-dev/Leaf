package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.TeaItemDto
import com.subin.leafy.domain.model.TeaItem
import com.subin.leafy.domain.model.TeaType

// 1. DTO -> Domain
fun TeaItemDto.toTeaItemDomain() = TeaItem(
    id = this.id,
    name = this.name,
    brand = this.brand,
    type = runCatching { TeaType.valueOf(this.type) }.getOrDefault(TeaType.ETC),
    origin = this.origin,
    leafStyle = this.leafStyle,
    grade = this.grade,
    imageUrl = this.imageUrl,
    isFavorite = this.isFavorite,
    stockQuantity = this.stockQuantity,
    memo = this.memo
)

// 2. Domain -> DTO
fun TeaItem.toDto() = TeaItemDto(
    id = this.id,
    name = this.name,
    brand = this.brand,
    type = this.type.name,
    origin = this.origin,
    leafStyle = this.leafStyle,
    grade = this.grade,
    imageUrl = this.imageUrl,
    isFavorite = this.isFavorite,
    stockQuantity = this.stockQuantity,
    memo = this.memo
)

// 3. 리스트 변환 헬퍼
fun List<TeaItemDto>.toTeaItemDomainList() = this.map { it.toTeaItemDomain() }