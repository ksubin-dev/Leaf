package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.tea.*
import javax.inject.Inject

data class TeaUseCases @Inject constructor(
    val getTeas: GetTeasUseCase,
    val getTeaCount: GetTeaCountUseCase,
    val searchTeas: SearchTeasUseCase,
    val getTeaDetail: GetTeaDetailUseCase,
    val saveTea: SaveTeaUseCase,
    val deleteTea: DeleteTeaUseCase,
    val toggleFavorite: ToggleFavoriteTeaUseCase,
    val syncTeas: SyncTeasUseCase,
    val scheduleTeaUpload: ScheduleTeaUploadUseCase
)