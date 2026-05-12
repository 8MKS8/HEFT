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
            val result = repository.getExercises()
            result.onSuccess { exercises ->
                _historyState.value = HistoryState.Success(exercises)
            }
            result.onFailure { e ->
                _historyState.value = HistoryState.Error(
                    e.message ?: "Failed to load exercises"
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