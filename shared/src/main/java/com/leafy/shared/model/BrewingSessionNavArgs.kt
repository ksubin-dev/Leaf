package com.leafy.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class BrewingSessionNavArgs(
    val teaName: String,
    val teaType: String,
    val waterTemp: Int,
    val leafAmount: Float,
    val waterAmount: Int,
    val teaware: String,
    val records: List<InfusionRecordDto>
)

@Serializable
data class InfusionRecordDto(
    val count: Int,
    val timeSeconds: Int,
    val waterTemp: Int
)