package com.heft.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heft.data.model.Analytics
import com.heft.data.model.Exercise
import com.heft.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * AnalyticsViewModel – manages the state for the Analytics screen.
 * Loads analytics and exercise data from Firestore.
 */
class AnalyticsViewModel : ViewModel() {

    // Repository for Firestore operations
    private val repository = ExerciseRepository()

    // UI state
    private val _analyticsState = MutableStateFlow<AnalyticsState>(AnalyticsState.Loading)
    val analyticsState: StateFlow<AnalyticsState> = _analyticsState

    init {
        // Load analytics when ViewModel is created
        loadAnalytics()
    }

    /**
     * Load analytics and exercises from Firestore.
     */
    fun loadAnalytics() {
        viewModelScope.launch {
            _analyticsState.value = AnalyticsState.Loading

            val analyticsResult  = repository.getAnalytics()
            val exercisesResult  = repository.getExercises()
            val practicesResult  = repository.getPractices()

            if (analyticsResult.isSuccess && exercisesResult.isSuccess) {
                val exercises = exercisesResult.getOrNull() ?: emptyList()
                val practices = practicesResult.getOrNull() ?: emptyList()

                android.util.Log.d("AnalyticsViewModel",
                    "Loaded ${exercises.size} exercises and ${practices.size} practices")

                // Update analytics with practice sessions count
                val analytics = analyticsResult.getOrNull()!!.copy(
                    totalPracticeSessions = practices.size
                )

                // Convert practices to exercises for breakdown chart
                val practiceAsExercises = practices.map { practice ->
                    com.heft.data.model.Exercise(
                        id             = practice.id,
                        userId         = practice.userId,
                        exerciseType   = "${practice.exerciseType} (Practice)",
                        sets           = 1,
                        reps           = practice.durationMinutes,
                        caloriesBurned = practice.caloriesBurned,
                        timestamp      = practice.timestamp
                    )
                }

                // Combine exercises and practices
                val combined = exercises + practiceAsExercises

                _analyticsState.value = AnalyticsState.Success(
                    analytics = analytics,
                    exercises = combined
                )
            } else {
                _analyticsState.value = AnalyticsState.Error(
                    "Failed to load analytics"
                )
            }
        }
    }
}

/**
 * AnalyticsState – all possible UI states for the Analytics screen.
 */
sealed class AnalyticsState {
    object Loading : AnalyticsState()
    data class Success(
        val analytics : Analytics,
        val exercises : List<Exercise>
    ) : AnalyticsState()
    data class Error(val message: String) : AnalyticsState()
}