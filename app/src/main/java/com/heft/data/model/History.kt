package com.heft.data.model

import com.google.firebase.Timestamp

// History data class — maps to Firestore 'history' collection
// Records each workout session
data class History(
    val id: String = "",
    val userId: String = "",
    val date: String = "",
    val type: String = "",        // "exercise" or "practice"
    val totalExercises: Int = 0,
    val totalSets: Int = 0,
    val totalReps: Int = 0,
    val totalCalories: Double = 0.0,
    val exercises: List<String> = emptyList(), // list of exercise IDs
    val timestamp: Timestamp? = null
)