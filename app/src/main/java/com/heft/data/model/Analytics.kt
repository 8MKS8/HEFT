package com.heft.data.model

import com.google.firebase.Timestamp

// Analytics data class — maps to Firestore 'analytics' collection
// Tracks overall user fitness statistics
data class Analytics(
    val userId: String = "",
    val totalSessions: Int = 0,
    val totalReps: Int = 0,
    val totalCaloriesBurned: Double = 0.0,
    val totalPracticeSessions: Int = 0,
    val personalBests: Map<String, PersonalBest> = emptyMap(),
    val weeklyProgress: List<WeeklyProgress> = emptyList()
)

// Personal best for each exercise type
data class PersonalBest(
    val exerciseType: String = "",
    val bestSets: Int = 0,
    val bestReps: Int = 0,
    val calories: Double = 0.0,
    val achievedAt: Timestamp? = null
)

// Weekly progress data for the chart
data class WeeklyProgress(
    val week: String = "",        // e.g. "Week 1"
    val sessions: Int = 0,
    val totalReps: Int = 0,
    val totalCalories: Double = 0.0
)