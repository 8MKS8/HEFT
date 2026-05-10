package com.heft.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.heft.data.model.Exercise
import com.heft.data.model.History
import com.heft.data.model.Practice
import com.heft.data.model.Analytics
import com.heft.data.model.PersonalBest
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ExerciseRepository — handles all Firestore operations
// for exercises, practice, history and analytics
class ExerciseRepository {

    // Firestore instance
    private val firestore = FirebaseFirestore.getInstance()

    // Current user ID
    private val userId get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Date formatter
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // ── EXERCISES ─────────────────────────────────────────────────────────

    // Save a new exercise to Firestore
    suspend fun saveExercise(exercise: Exercise): Result<String> {
        return try {
            val doc = firestore.collection("exercises")
                .add(exercise.copy(userId = userId))
                .await()

            // Update history and analytics
            updateHistory(exercise)
            updateAnalytics(exercise)

            Result.success(doc.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all exercises for current user
    suspend fun getExercises(): Result<List<Exercise>> {
        return try {
            val snapshot = firestore.collection("exercises")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            val exercises = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Exercise::class.java)?.copy(id = doc.id)
            }
            Result.success(exercises)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete an exercise
    suspend fun deleteExercise(id: String): Result<Unit> {
        return try {
            firestore.collection("exercises")
                .document(id)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── PRACTICE ──────────────────────────────────────────────────────────

    // Save a practice session
    suspend fun savePractice(practice: Practice): Result<String> {
        return try {
            val doc = firestore.collection("practice")
                .add(practice.copy(userId = userId))
                .await()
            Result.success(doc.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all practice sessions
    suspend fun getPractices(): Result<List<Practice>> {
        return try {
            val snapshot = firestore.collection("practice")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            val practices = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Practice::class.java)?.copy(id = doc.id)
            }
            Result.success(practices)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── HISTORY ───────────────────────────────────────────────────────────

    // Get history for current user
    suspend fun getHistory(): Result<List<History>> {
        return try {
            val snapshot = firestore.collection("history")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            val history = snapshot.documents.mapNotNull { doc ->
                doc.toObject(History::class.java)?.copy(id = doc.id)
            }
            Result.success(history)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update history after saving exercise
    private suspend fun updateHistory(exercise: Exercise) {
        val today = dateFormat.format(Date())
        val historyRef = firestore.collection("history")
            .whereEqualTo("userId", userId)
            .whereEqualTo("date", today)
            .get()
            .await()

        if (historyRef.isEmpty) {
            // Create new history entry for today
            val history = History(
                userId         = userId,
                date           = today,
                type           = "exercise",
                totalExercises = 1,
                totalSets      = exercise.sets,
                totalReps      = exercise.reps,
                totalCalories  = exercise.caloriesBurned
            )
            firestore.collection("history").add(history).await()
        } else {
            // Update existing history entry
            val doc = historyRef.documents.first()
            val existing = doc.toObject(History::class.java)!!
            firestore.collection("history")
                .document(doc.id)
                .update(
                    mapOf(
                        "totalExercises" to existing.totalExercises + 1,
                        "totalSets"      to existing.totalSets + exercise.sets,
                        "totalReps"      to existing.totalReps + exercise.reps,
                        "totalCalories"  to existing.totalCalories + exercise.caloriesBurned
                    )
                ).await()
        }
    }

    // ── ANALYTICS ─────────────────────────────────────────────────────────

    // Get analytics for current user
    suspend fun getAnalytics(): Result<Analytics> {
        return try {
            val doc = firestore.collection("analytics")
                .document(userId)
                .get()
                .await()
            val analytics = doc.toObject(Analytics::class.java)
                ?: Analytics(userId = userId)
            Result.success(analytics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update analytics after saving exercise
    private suspend fun updateAnalytics(exercise: Exercise) {
        val analyticsRef = firestore.collection("analytics").document(userId)
        val doc = analyticsRef.get().await()
        val existing = doc.toObject(Analytics::class.java)
            ?: Analytics(userId = userId)

        // Check personal best
        val currentBest = existing.personalBests[exercise.exerciseType]
        val newTotalReps = exercise.sets * exercise.reps
        val bestTotalReps = (currentBest?.bestSets ?: 0) * (currentBest?.bestReps ?: 0)

        val updatedBests = existing.personalBests.toMutableMap()
        if (newTotalReps > bestTotalReps) {
            updatedBests[exercise.exerciseType] = PersonalBest(
                exerciseType = exercise.exerciseType,
                bestSets     = exercise.sets,
                bestReps     = exercise.reps,
                calories     = exercise.caloriesBurned
            )
        }

        // Update analytics document
        analyticsRef.set(
            existing.copy(
                totalSessions       = existing.totalSessions + 1,
                totalReps           = existing.totalReps + (exercise.sets * exercise.reps),
                totalCaloriesBurned = existing.totalCaloriesBurned + exercise.caloriesBurned,
                personalBests       = updatedBests
            )
        ).await()
    }
}