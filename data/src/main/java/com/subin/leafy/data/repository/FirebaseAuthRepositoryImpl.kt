package com.subin.leafy.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.subin.leafy.data.model.dto.UserDTO
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.common.toResourceResult
import com.subin.leafy.domain.model.AuthUser
import com.subin.leafy.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) : AuthRepository {

    private var _cachedUser: AuthUser? = null

    override suspend fun signUp(
        email: String,
        password: String,
        username: String,
        profileImageUri: String?
    ): DataResourceResult<AuthUser> = runCatching {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("계정 생성 실패")

        var uploadedImageUrl: String? = null
        profileImageUri?.let { uriString ->
            val ref = firebaseStorage.reference.child("profiles/${firebaseUser.uid}.jpg")
            ref.putFile(Uri.parse(uriString)).await()
            uploadedImageUrl = ref.downloadUrl.await().toString()
        }

        val userDto = UserDTO(
            uid = firebaseUser.uid,
            displayName = username,
            photoUrl = uploadedImageUrl,
            likedPostIds = emptyList(),
            savedPostIds = emptyList(),
            followingIds = emptyList()
        )

        firestore.collection("users").document(firebaseUser.uid).set(userDto).await()

        val newUser = AuthUser(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            username = username,
            profileUrl = uploadedImageUrl,
            likedPostIds = emptyList(),
            savedPostIds = emptyList(),
            followingIds = emptyList()
        )
        _cachedUser = newUser
        newUser
    }.toResourceResult()

    override suspend fun login(email: String, password: String): DataResourceResult<AuthUser> =
        runCatching {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("로그인 정보를 가져올 수 없습니다.")

            val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()
            val userDto = userDoc.toObject(UserDTO::class.java)

            val authUser = AuthUser(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: email,
                username = userDto?.displayName ?: firebaseUser.displayName,
                profileUrl = userDto?.photoUrl ?: firebaseUser.photoUrl?.toString(),
                likedPostIds = userDto?.likedPostIds ?: emptyList(),
                savedPostIds = userDto?.savedPostIds ?: emptyList(),
                followingIds = userDto?.followingIds ?: emptyList()
            )

            _cachedUser = authUser
            authUser
        }.toResourceResult()

    override suspend fun logout(): DataResourceResult<Unit> = runCatching {
        firebaseAuth.signOut()
        _cachedUser = null
    }.toResourceResult()

    override fun getCurrentUser(): AuthUser? {
        if (_cachedUser == null) {
            val firebaseUser = firebaseAuth.currentUser ?: return null
            _cachedUser = AuthUser(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                username = firebaseUser.displayName,
                profileUrl = firebaseUser.photoUrl?.toString()
            )
        }
        return _cachedUser
    }

    override fun updateCurrentUserState(
        likedPostIds: List<String>?,
        savedPostIds: List<String>?,
        followingIds: List<String>?
    ) {
        _cachedUser = _cachedUser?.copy(
            likedPostIds = likedPostIds ?: _cachedUser!!.likedPostIds,
            savedPostIds = savedPostIds ?: _cachedUser!!.savedPostIds,
            followingIds = followingIds ?: _cachedUser!!.followingIds
        )
    }
}