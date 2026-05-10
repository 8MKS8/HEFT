package com.heft.data.model

import com.google.firebase.Timestamp

// Practice data class — maps to Firestore 'practice' collection
// Free practice — no sets or reps required
data class Practice(
    val id: String = "",
    val userId: String = "",
    val exerciseType: String = "",
    val videoWatched: Boolean = false,
    val durationMinutes: Int = 0,
    val caloriesBurned: Double = 0.0,
    val notes: String = "",
    val timestamp: Timestamp? = null
) {
    companion object {

        // Calories burned per minute for each exercise type
        val caloriesPerMinute = mapOf(
            "Push-ups"          to 7.0,
            "Squats"            to 8.0,
            "Lunges"            to 7.0,
            "Burpees"           to 10.0,
            "Sit-ups"           to 6.0,
            "Jumping Jacks"     to 8.0,
            "Mountain Climbers" to 9.0,
            "Plank"             to 5.0
        )

        // Calculate calories for practice session
        fun calculateCalories(type: String, durationMinutes: Int): Double {
            val calPerMin = caloriesPerMinute[type] ?: 7.0
            return durationMinutes * calPerMin
        }
    }
}
