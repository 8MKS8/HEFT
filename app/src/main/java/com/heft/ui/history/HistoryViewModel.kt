package com.heft.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heft.data.model.Exercise
import com.heft.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * HistoryViewModel – manages the state for the History screen.
 * Loads exercises from Firestore and handles deletions.
 */
class HistoryViewModel : ViewModel() {

    // Repository for Firestore operations
    private val repository = ExerciseRepository()

    // UI state
    private val _historyState = MutableStateFlow<HistoryState>(HistoryState.Loading)
    val historyState: StateFlow<HistoryState> = _historyState

    /**
     * Load all exercises for the current user from Firestore.
     */
    fun loadExercises() {
        viewModelScope.launch {
            _historyState.value = HistoryState.Loading

            val exercisesResult = repository.getExercises()
            val practicesResult = repository.getPractices()

            if (exercisesResult.isSuccess) {
                val exercises = exercisesResult.getOrNull() ?: emptyList()
                val practices = practicesResult.getOrNull() ?: emptyList()

                android.util.Log.d("HistoryViewModel",
                    "Loaded ${exercises.size} exercises and ${practices.size} practices")

                val practiceAsExercises = practices.map { practice ->
                    com.heft.data.model.Exercise(
                        id             = practice.id,
                        userId         = practice.userId,
                        exerciseType   = "🎯 ${practice.exerciseType} (Practice)",
                        sets           = 1,
                        reps           = practice.durationMinutes,
                        caloriesBurned = practice.caloriesBurned,
                        notes          = practice.notes,
                        timestamp      = practice.timestamp
                    )
                }

                val combined = (exercises + practiceAsExercises)
                    .sortedByDescending { it.timestamp?.seconds ?: 0 }

                _historyState.value = HistoryState.Success(combined)
            } else {
                _historyState.value = HistoryState.Error(
                    exercisesResult.exceptionOrNull()?.message
                        ?: "Failed to load history"
                )
            }
        }
    }

    /**
     * Delete an exercise from Firestore.
     * Reloads the list after deletion.
     */
    fun deleteExercise(id: String) {
        viewModelScope.launch {
            repository.deleteExercise(id)
            // Reload exercises after deletion
            loadExercises()
        }
    }
}

/**
 * HistoryState – all possible UI states for the History screen.
 */
sealed class HistoryState {
    object Loading : HistoryState()
    data class Success(val exercises: List<Exercise>) : HistoryState()
    data class Error(val message: String) : HistoryState()
}