package com.heft.data.model

import com.google.firebase.Timestamp

// Exercise data class — maps to Firestore 'exercises' collection
data class Exercise(
    val id: String = "",
    val userId: String = "",
    val exerciseType: String = "",
    val sets: Int = 0,
    val reps: Int = 0,
    val caloriesBurned: Double = 0.0,
    val notes: String = "",
    val timestamp: Timestamp? = null
) {
    // Companion object holds static data
    companion object {

        // Calories burned per rep for each exercise type
        val caloriesPerRep = mapOf(
            "Push-ups"          to 0.5,
            "Squats"            to 0.8,
            "Lunges"            to 0.7,
            "Burpees"           to 1.5,
            "Sit-ups"           to 0.4,
            "Jumping Jacks"     to 0.4,
            "Mountain Climbers" to 0.5,
            "Plank"             to 0.0
        )

        // All available exercise types
        val exerciseTypes = caloriesPerRep.keys.toList()

        // Calculate calories burned for an exercise
        fun calculateCalories(type: String, sets: Int, reps: Int): Double {
            val calPerRep = caloriesPerRep[type] ?: 0.5
            return sets * reps * calPerRep
        }
    }
}