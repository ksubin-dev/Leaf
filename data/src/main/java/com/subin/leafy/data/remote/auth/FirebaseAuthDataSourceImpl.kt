package com.subin.leafy.data.remote.auth

import com.google.firebase.auth.FirebaseAuth
import com.subin.leafy.data.datasource.remote.AuthDataSource
import com.subin.leafy.domain.common.DataResourceResult
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSourceImpl(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthDataSource {

    override fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

    override fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    override suspend fun login(email: String, password: String): DataResourceResult<Unit> = try {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
        DataResourceResult.Success(Unit)
    } catch (e: Exception) {
        DataResourceResult.Failure(e)
    }

    override suspend fun signUp(email: String, password: String): DataResourceResult<Unit> = try {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        DataResourceResult.Success(Unit)
    } catch (e: Exception) {
        DataResourceResult.Failure(e)
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun deleteAuthAccount(): DataResourceResult<Unit> = try {
        firebaseAuth.currentUser?.delete()?.await()
        DataResourceResult.Success(Unit)
    } catch (e: Exception) {
        DataResourceResult.Failure(e)
    }

    override suspend fun sendPasswordResetEmail(email: String): DataResourceResult<Unit> = try {
        firebaseAuth.sendPasswordResetEmail(email).await()
        DataResourceResult.Success(Unit)
    } catch (e: Exception) {
        DataResourceResult.Failure(e)
    }
}