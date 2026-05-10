package com.heft.data.model

import com.google.firebase.Timestamp

// User data class — maps to Firestore 'users' collection
data class User(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val fitnessGoal: String = "",
    val weeklyTarget: Int = 5,
    val createdAt: Timestamp? = null
)