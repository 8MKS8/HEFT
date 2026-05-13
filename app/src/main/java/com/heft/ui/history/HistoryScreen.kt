package com.heft.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heft.data.model.Exercise
import com.heft.ui.auth.CardBackground
import com.heft.ui.auth.DarkBackground
import com.heft.ui.auth.NeonGreen
import com.heft.ui.auth.TextPrimary
import com.heft.ui.auth.TextSecondary
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * HistoryScreen – displays all exercises logged by the user.
 *
 * Features:
 *  • List of all exercises from Firestore
 *  • Exercise type, sets, reps, calories
 *  • Date and time of each exercise
 *  • Delete exercise option
 *  • Empty state when no exercises
 *
 * @param onBack – navigates back to Home screen
 */
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    // Observe history state
    val historyState by viewModel.historyState.collectAsState()

    // Load exercises when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadExercises()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // ── Top Bar ───────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            TextButton(onClick = onBack) {
                Text(
                    text = "← Back",
                    color = NeonGreen,
                    fontSize = 16.sp
                )
            }

            // Screen title
            Text(
                text = "Exercise History",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            // Spacer to balance back button
            Spacer(modifier = Modifier.width(80.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Content ───────────────────────────────────────────────────────
        when (val state = historyState) {

            // Loading state — show spinner
            is HistoryState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NeonGreen)
                }
            }

            // Error state — show error message
            is HistoryState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Success state — show list of exercises
            is HistoryState.Success -> {
                if (state.exercises.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🏋️",
                                fontSize = 64.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No exercises yet!",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Start adding exercises\nto see your history here.",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Show total stats at top
                    HistoryStatsCard(exercises = state.exercises)

                    Spacer(modifier = Modifier.height(16.dp))

                    // List of exercises
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.exercises) { exercise ->
                            ExerciseHistoryCard(
                                exercise  = exercise,
                                onDelete  = { viewModel.deleteExercise(exercise.id) }
                            )
                        }
                    }
                }
            }

            else -> {}
        }
    }
}

/**
 * HistoryStatsCard – shows total stats at the top of history screen.
 */
@Composable
fun HistoryStatsCard(exercises: List<Exercise>) {
    // Calculate totals — exclude practice sessions from sets/reps
    val exercisesOnly = exercises.filter { !it.exerciseType.contains("Practice") }
    val practiceOnly  = exercises.filter { it.exerciseType.contains("Practice") }
    val totalSets     = exercisesOnly.sumOf { it.sets }
    val totalReps     = exercisesOnly.sumOf { it.reps }
    val totalCalories = exercises.sumOf { it.caloriesBurned }
    val totalPractice = practiceOnly.size

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // First row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Total sessions
                StatItem(
                    value = exercisesOnly.size.toString(),
                    label = "Exercises"
                )

                // Total practice
                StatItem(
                    value = totalPractice.toString(),
                    label = "Practice"
                )

                // Total calories
                StatItem(
                    value = String.format("%.0f", totalCalories),
                    label = "Calories"
                )
            }

            HorizontalDivider(color = NeonGreen.copy(alpha = 0.2f))

            // Second row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Total sets
                StatItem(
                    value = totalSets.toString(),
                    label = "Total Sets"
                )

                // Total reps
                StatItem(
                    value = totalReps.toString(),
                    label = "Total Reps"
                )
            }
        }
    }
}

/**
 * StatItem – single stat display in the stats card.
 */
@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = NeonGreen,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp
        )
    }
}

/**
 * ExerciseHistoryCard – single exercise card in the history list.
 */
@Composable
fun ExerciseHistoryCard(
    exercise: Exercise,
    onDelete: () -> Unit
) {
    // Format the timestamp
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val dateStr = try {
        exercise.timestamp?.toDate()?.let {
            dateFormat.format(it)
        } ?: "Syncing..."
    } catch (e: Exception) {
        "Syncing..."
    }

    // Show delete confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = CardBackground,
            title = {
                Text(
                    text = "Delete Exercise?",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete this exercise?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = NeonGreen)
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Exercise info
            Column(modifier = Modifier.weight(1f)) {
                // Exercise type with badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Exercise name
                    Text(
                        text = exercise.exerciseType
                            .replace("🎯 ", "")
                            .replace(" (Practice)", ""),
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Badge — Exercise or Practice
                    Card(
                        shape = RoundedCornerShape(4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (exercise.exerciseType.contains("Practice"))
                                NeonGreen.copy(alpha = 0.2f)
                            else
                                CardBackground
                        )
                    ) {
                        Text(
                            text = if (exercise.exerciseType.contains("Practice"))
                                "🎯 Practice"
                            else
                                "💪 Exercise",
                            color = if (exercise.exerciseType.contains("Practice"))
                                NeonGreen
                            else
                                TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                horizontal = 6.dp,
                                vertical   = 2.dp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Sets x Reps OR Duration for practice
                Text(
                    text = if (exercise.exerciseType.contains("Practice"))
                        "⏱️ Duration: ${exercise.reps} minutes"
                    else
                        "${exercise.sets} sets × ${exercise.reps} reps",
                    color = NeonGreen,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Calories
                Text(
                    text = "🔥 ${String.format("%.1f", exercise.caloriesBurned)} kcal",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Date
                Text(
                    text = dateStr,
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                // Notes if available
                if (exercise.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "📝 ${exercise.notes}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            // Delete button
            TextButton(
                onClick = { showDeleteDialog = true }
            ) {
                Text(
                    text = "🗑️",
                    fontSize = 20.sp
                )
            }
        }
    }
}