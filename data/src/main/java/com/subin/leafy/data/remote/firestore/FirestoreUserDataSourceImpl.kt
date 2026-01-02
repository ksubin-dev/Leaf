package com.subin.leafy.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.subin.leafy.data.datasource.UserDataSource
import com.subin.leafy.data.mapper.toDomain
import com.subin.leafy.data.model.dto.UserDTO
import com.subin.leafy.data.model.dto.UserStatsDTO
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserStats
import kotlinx.coroutines.tasks.await

class FirestoreUserDataSourceImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UserDataSource {

    override suspend fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    //common 만들어서 users나 커뮤니티쪽 빼기
    //select snapshot 써야함 이걸로 바꾸기 서치는 스냅샷!!
    //DataResourceResult  cud

    //바꾸기
    override suspend fun getUser(userId: String): DataResourceResult<User> = runCatching {
        val snapshot = firestore.collection("users").document(userId).get().await()
        val userDto = snapshot.toObject(UserDTO::class.java)
            ?: throw Exception("유저 정보가 없습니다.") //커스텀 Exception(예: UserNotFoundException)을 정의하여 도메인 레이어에서 어떤 에러인지 명확히 알 수 있게 하세요.
        DataResourceResult.Success(userDto.toDomain())
    }.getOrElse {
        DataResourceResult.Failure(it)
    }

    override suspend fun getUserStats(userId: String): DataResourceResult<UserStats> = runCatching {
        val snapshot = firestore.collection("user_stats").document(userId).get().await()
        val statsDto = snapshot.toObject(UserStatsDTO::class.java)
            ?: UserStatsDTO()
        DataResourceResult.Success(statsDto.toDomain())
    }.getOrElse {
        DataResourceResult.Failure(it)
    }
}