package com.subin.leafy.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.AuthUser
import com.subin.leafy.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    override suspend fun signUp(email: String, password: String, username: String): DataResourceResult<AuthUser> {
        return try {
            // 1. Firebase Auth 계정 생성
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                val authUser = AuthUser(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: email,
                    username = username
                )
                DataResourceResult.Success(authUser)
            } else {
                DataResourceResult.Failure(Exception("계정 생성 후 사용자 정보를 가져올 수 없습니다."))
            }
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
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