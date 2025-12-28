package com.subin.leafy.domain.model

data class BrewingRecord(
    val id: String,
    val dateString: String,
    val teaName: String,
    val metaInfo: String,
    val rating: Int
)