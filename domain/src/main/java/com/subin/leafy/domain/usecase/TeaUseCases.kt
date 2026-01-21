package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.tea.*

data class TeaUseCases(
    val getTeas: GetTeasUseCase,
    val getTeaCount: GetTeaCountUseCase,
    val searchTeas: SearchTeasUseCase,
    val getTeaDetail: GetTeaDetailUseCase,
    val saveTea: SaveTeaUseCase,
    val deleteTea: DeleteTeaUseCase,
    val toggleFavorite: ToggleFavoriteTeaUseCase,
    val syncTeas: SyncTeasUseCase
)