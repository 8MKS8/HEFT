package com.heft.data.model

import com.google.firebase.Timestamp

/**
 * User data class — maps to Firestore 'users' collection.
 * Stores all user profile information.
 */
data class User(
    val uid          : String     = "",
    val displayName  : String     = "",
    val email        : String     = "",

    // ── Personal Info ─────────────────────────────────────────────────────
    val age          : Int        = 0,
    val sex          : String     = "",      // "Male" or "Female"
    val heightCm     : Double     = 0.0,     // height in cm
    val weightKg     : Double     = 0.0,     // current weight in kg
    val goalWeightKg : Double     = 0.0,     // target weight in kg

    // ── Fitness Goals ─────────────────────────────────────────────────────
    val fitnessGoal  : String     = "",
    val weeklyTarget : Int        = 5,

    val createdAt    : Timestamp? = null
) {
    companion object {

        /**
         * Calculate BMI from height and weight.
         * BMI = weight(kg) / height(m)²
         */
        fun calculateBMI(weightKg: Double, heightCm: Double): Double {
            if (heightCm <= 0 || weightKg <= 0) return 0.0
            val heightM = heightCm / 100.0
            return weightKg / (heightM * heightM)
        }

        /**
         * Get BMI category from BMI value.
         */
        fun getBMICategory(bmi: Double): String {
            return when {
                bmi <= 0    -> "N/A"
                bmi < 18.5  -> "Underweight"
                bmi < 25.0  -> "Normal weight"
                bmi < 30.0  -> "Overweight"
                else        -> "Obese"
            }
        }

        /**
         * Get BMI category color.
         */
        fun getBMICategoryColor(bmi: Double): String {
            return when {
                bmi <= 0    -> "#A0A0A0"
                bmi < 18.5  -> "#3498DB"   // Blue — underweight
                bmi < 25.0  -> "#2ECC71"   // Green — normal
                bmi < 30.0  -> "#F39C12"   // Orange — overweight
                else        -> "#E74C3C"   // Red — obese
            }
        }
    }
}