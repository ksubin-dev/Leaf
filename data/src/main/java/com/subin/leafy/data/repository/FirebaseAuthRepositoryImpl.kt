package com.subin.leafy.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.subin.leafy.data.model.dto.UserDTO
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.AuthUser
import com.subin.leafy.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun signUp(email: String, password: String, username: String): DataResourceResult<AuthUser> = runCatching {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("계정 생성에 실패했습니다.")

        val userDto = UserDTO(
            uid = firebaseUser.uid,
            displayName = username,
            photoUrl = null
        )

        firestore.collection("users").document(firebaseUser.uid).set(userDto).await()

        DataResourceResult.Success(
            AuthUser(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                username = username
            )
        )
    }.getOrElse {
        DataResourceResult.Failure(it)
    }

    override suspend fun login(email: String, password: String): DataResourceResult<AuthUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                DataResourceResult.Success(
                    AuthUser(id = firebaseUser.uid, email = firebaseUser.email ?: email)
                )
            } else {
                DataResourceResult.Failure(Exception("로그인 정보를 가져올 수 없습니다."))
            }
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun logout(): DataResourceResult<Unit> {
        return try {
            firebaseAuth.signOut()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override fun getCurrentUser(): AuthUser? {
        return firebaseAuth.currentUser?.let {
            AuthUser(id = it.uid, email = it.email ?: "")
        }
    }
}