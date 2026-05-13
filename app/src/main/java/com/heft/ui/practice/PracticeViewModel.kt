package com.heft.ui.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heft.data.model.Exercise
import com.heft.data.model.Practice
import com.heft.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp

/**
 * PracticeViewModel – manages the state for the Practice screen.
 * Handles free practice sessions with YouTube video integration.
 */
class PracticeViewModel : ViewModel() {

    // Repository for Firestore operations
    private val repository = ExerciseRepository()

    // UI state
    private val _practiceState = MutableStateFlow<PracticeState>(PracticeState.Idle)
    val practiceState: StateFlow<PracticeState> = _practiceState

    // All available exercises with their YouTube video IDs
    val exercisesWithVideos = listOf(
        PracticeExercise("Push-ups",          "4dF1DOWy-L4"),
        PracticeExercise("Pull-ups",          "eGo4IYlbE5g"),
        PracticeExercise("Dips",              "2z8JmcrW-As"),
        PracticeExercise("Squats",            "YaXPRqUwItQ"),
        PracticeExercise("Lunges",            "QOVaHwm-Q6U"),
        PracticeExercise("Burpees",           "TU8QYVW0gDU"),
        PracticeExercise("Sit-ups",           "jDwoBqPH0jk"),
        PracticeExercise("Jumping Jacks",     "c4DAnQ6DtF8"),
        PracticeExercise("Rope Jumping",      "u3zgHI8QnqE"),
        PracticeExercise("Mountain Climbers", "De_Ulf9fclg"),
        PracticeExercise("Plank",             "ASdvN_XEl_c")
    )

    /**
     * Save a free practice session to Firestore.
     * No sets or reps required — just duration.
     *
     * @param exerciseType    – type of exercise practiced
     * @param durationMinutes – how long the session lasted
     * @param videoWatched    – whether the user watched the tutorial
     * @param notes           – optional notes
     */
    fun savePractice(
        exerciseType    : String,
        durationMinutes : String,
        videoWatched    : Boolean,
        notes           : String
    ) {
        // Validate inputs
        if (exerciseType.isBlank()) {
            _practiceState.value = PracticeState.Error("Please select an exercise")
            return
        }

        val duration = durationMinutes.toIntOrNull()
        if (duration == null || duration <= 0) {
            _practiceState.value = PracticeState.Error("Please enter a valid duration")
            return
        }

        // Calculate calories burned
        val calories = Practice.calculateCalories(exerciseType, duration)

        viewModelScope.launch {
            _practiceState.value = PracticeState.Loading

            val practice = Practice(
                exerciseType    = exerciseType,
                durationMinutes = duration,
                videoWatched    = videoWatched,
                caloriesBurned  = calories,
                notes           = notes,
                timestamp       = Timestamp.now()
            )

            val result = repository.savePractice(practice)

            result.onSuccess {
                _practiceState.value = PracticeState.Success(calories)
            }
            result.onFailure { e ->
                _practiceState.value = PracticeState.Error(
                    e.message ?: "Failed to save practice session"
                )
            }
        }
    }

    fun resetState() {
        _practiceState.value = PracticeState.Idle
    }
}

/**
 * PracticeExercise – holds exercise name and YouTube video ID.
 */
data class PracticeExercise(
    val name    : String,
    val videoId : String
)

/**
 * PracticeState – all possible UI states for the Practice screen.
 */
sealed class PracticeState {
    object Idle    : PracticeState()
    object Loading : PracticeState()
    data class Success(val calories: Double) : PracticeState()
    data class Error(val message: String)    : PracticeState()
}