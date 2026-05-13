package com.heft.ui.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heft.data.model.Exercise
import com.heft.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ExerciseViewModel – manages the state for the Add Exercise screen.
 * Handles input validation and saving to Firebase Firestore.
 */
class ExerciseViewModel : ViewModel() {

    // Repository for Firestore operations
    private val repository = ExerciseRepository()

    // UI state
    private val _exerciseState = MutableStateFlow<ExerciseState>(ExerciseState.Idle)
    val exerciseState: StateFlow<ExerciseState> = _exerciseState

    /**
     * Save a new exercise to Firestore.
     * Validates inputs first then calculates calories automatically.
     *
     * @param exerciseType – type of exercise e.g. Push-ups
     * @param sets         – number of sets performed
     * @param reps         – number of reps per set
     * @param notes        – optional notes
     */
    fun saveExercise(
        exerciseType: String,
        sets: String,
        reps: String,
        notes: String
    ) {
        // ── Validation ────────────────────────────────────────────────────

        // Check exercise type is selected
        if (exerciseType.isBlank()) {
            _exerciseState.value = ExerciseState.Error("Please select an exercise type")
            return
        }

        // Check sets is a valid number
        val setsInt = sets.toIntOrNull()
        if (setsInt == null || setsInt <= 0) {
            _exerciseState.value = ExerciseState.Error("Please enter a valid number of sets")
            return
        }

        // Check reps is a valid number
        val repsInt = reps.toIntOrNull()
        if (repsInt == null || repsInt <= 0) {
            _exerciseState.value = ExerciseState.Error("Please enter a valid number of reps")
            return
        }

        // ── Calculate Calories ────────────────────────────────────────────
        val calories = Exercise.calculateCalories(exerciseType, setsInt, repsInt)

        // ── Save to Firestore ─────────────────────────────────────────────
        viewModelScope.launch {
            _exerciseState.value = ExerciseState.Loading

            val exercise = Exercise(
                exerciseType   = exerciseType,
                sets           = setsInt,
                reps           = repsInt,
                caloriesBurned = calories,
                notes          = notes,
                timestamp      = com.google.firebase.Timestamp.now()
            )

            val result = repository.saveExercise(exercise)

            result.onSuccess {
                _exerciseState.value = ExerciseState.Success(calories)
            }
            result.onFailure { e ->
                _exerciseState.value = ExerciseState.Error(
                    e.message ?: "Failed to save exercise"
                )
            }
        }
    }

    // Reset state back to idle
    fun resetState() {
        _exerciseState.value = ExerciseState.Idle
    }
}

/**
 * ExerciseState – all possible UI states for the Add Exercise screen.
 */
sealed class ExerciseState {
    object Idle    : ExerciseState()  // Default state
    object Loading : ExerciseState()  // Saving to Firestore
    data class Success(val calories: Double) : ExerciseState() // Saved successfully
    data class Error(val message: String)    : ExerciseState() // Error message
}