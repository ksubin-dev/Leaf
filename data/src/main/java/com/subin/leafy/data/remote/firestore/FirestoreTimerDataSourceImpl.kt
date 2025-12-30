package com.subin.leafy.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.subin.leafy.data.datasource.TimerDataSource
import com.subin.leafy.data.mapper.toDomainList
import com.subin.leafy.data.model.dto.TimerPresetDTO
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TimerPreset
import kotlinx.coroutines.tasks.await

class FirestoreTimerDataSourceImpl(
    private val firestore: FirebaseFirestore
) : TimerDataSource {

    private val timerCollection = firestore.collection("timer_presets")

    override suspend fun getPresets(): DataResourceResult<List<TimerPreset>> = runCatching {
        val snapshot = timerCollection.get().await()
        val presets = snapshot.toObjects(TimerPresetDTO::class.java).toDomainList()
        DataResourceResult.Success(presets)
    }.getOrElse {
        DataResourceResult.Failure(it)
    }
}