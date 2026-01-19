package com.leafy.features.timer.ui


import com.leafy.shared.model.InfusionRecordDto
import com.subin.leafy.domain.model.InfusionRecord

fun InfusionRecord.toInfusionRecordDto(): InfusionRecordDto {
    return InfusionRecordDto(
        count = this.count,
        timeSeconds = this.timeSeconds,
        waterTemp = this.waterTemp
    )
}