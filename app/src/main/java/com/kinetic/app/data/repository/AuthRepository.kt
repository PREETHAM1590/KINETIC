package com.kinetic.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kinetic.app.data.models.AuthResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface AuthRepository {
    val currentUser: Flow<FirebaseUser?>
    suspend fun signIn(email: String, password: String): AuthResult
    suspend fun signUp(email: String, password: String): AuthResult
    suspend fun signOut()
    suspend fun deleteAccount(): AuthResult
    fun isSignedIn(): Boolean
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val currentUser: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid
            if (userId.isNullOrBlank()) {
                AuthResult.Error("Sign in failed: missing user")
            } else {
                AuthResult.Success(userId)
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign in failed")
        }
    }

    override suspend fun signUp(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid
            if (userId.isNullOrBlank()) {
                AuthResult.Error("Sign up failed: missing user")
            } else {
                AuthResult.Success(userId)
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign up failed")
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun deleteAccount(): AuthResult {
        return try {
            val user = firebaseAuth.currentUser ?: return AuthResult.Error("No user signed in")
            user.delete().await()
            AuthResult.Success(user.uid)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Account deletion failed")
        }
    }

    override fun isSignedIn(): Boolean = firebaseAuth.currentUser != null
}
