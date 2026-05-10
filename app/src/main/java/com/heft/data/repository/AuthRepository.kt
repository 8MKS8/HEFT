package com.heft.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.heft.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// AuthRepository — handles all Firebase Authentication operations
// Login, Register, Logout
class AuthRepository {

    // Firebase Auth instance
    private val auth = FirebaseAuth.getInstance()

    // Firestore instance
    private val firestore = FirebaseFirestore.getInstance()

    // Get current logged in user
    val currentUser: FirebaseUser? get() = auth.currentUser

    // Check if user is logged in
    val isLoggedIn: Boolean get() = auth.currentUser != null

    // ── REGISTER ─────────────────────────────────────────────────────────

    // Register a new user with email and password
    suspend fun register(
        email: String,
        password: String,
        displayName: String
    ): Result<FirebaseUser> {
        return try {
            // Create user in Firebase Auth
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user!!

            // Save user profile to Firestore
            // Save user profile to Firestore
            try {
                val user = User(
                    uid         = firebaseUser.uid,
                    displayName = displayName,
                    email       = email
                )
                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .set(user)
                    .await()
            } catch (e: Exception) {
                // Continue even if Firestore save fails
                android.util.Log.e("AuthRepository", "Failed to save user profile", e)
            }

            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── LOGIN ─────────────────────────────────────────────────────────────

    // Login with email and password
    suspend fun login(
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── LOGOUT ────────────────────────────────────────────────────────────

    // Sign out current user
    fun logout() {
        auth.signOut()
    }

    // ── PASSWORD RESET ────────────────────────────────────────────────────

    // Send password reset email
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}