package com.heft.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.heft.data.model.Analytics
import com.heft.data.model.Exercise
import com.heft.ui.auth.CardBackground
import com.heft.ui.auth.DarkBackground
import com.heft.ui.auth.NeonGreen
import com.heft.ui.auth.TextPrimary
import com.heft.ui.auth.TextSecondary
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * AnalyticsScreen – displays user fitness analytics.
 *
 * Features:
 *  • Total sessions, reps, calories
 *  • Personal bests per exercise
 *  • Weekly progress
 *  • Exercise breakdown
 *
 * @param onBack – navigates back to Home screen
 */
@Composable
fun AnalyticsScreen(
    onBack: () -> Unit,
    viewModel: AnalyticsViewModel = viewModel()
) {
    // Observe analytics state
    val analyticsState by viewModel.analyticsState.collectAsState()

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
                text = "Analytics",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width(80.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Content ───────────────────────────────────────────────────────
        when (val state = analyticsState) {

            // Loading state
            is AnalyticsState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NeonGreen)
                }
            }

            // Error state
            is AnalyticsState.Error -> {
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

            // Success state
            is AnalyticsState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Overall stats
                    item {
                        OverallStatsCard(analytics = state.analytics)
                    }

                    // Personal bests
                    item {
                        PersonalBestsCard(analytics = state.analytics)
                    }

                    // Exercise breakdown
                    item {
                        ExerciseBreakdownCard(exercises = state.exercises)
                    }

                    // Bottom padding
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

/**
 * OverallStatsCard – shows total fitness statistics.
 */
@Composable
fun OverallStatsCard(analytics: Analytics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card title
            Text(
                text = "📊 Overall Stats",
                color = NeonGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // Stats grid Row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AnalyticsStatItem(
                    value = analytics.totalSessions.toString(),
                    label = "Sessions",
                    emoji = "🏋️"
                )
                AnalyticsStatItem(
                    value = analytics.totalReps.toString(),
                    label = "Total Reps",
                    emoji = "🔄"
                )
            }

            // Stats grid Row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AnalyticsStatItem(
                    value = String.format(Locale.getDefault(), "%.0f", analytics.totalCaloriesBurned),
                    label = "Calories",
                    emoji = "🔥"
                )
                AnalyticsStatItem(
                    value = analytics.totalPracticeSessions.toString(),
                    label = "Practice",
                    emoji = "🎯"
                )
            }

            // ── Facebook Share ────────────────────────────────────────
            HorizontalDivider(color = NeonGreen.copy(alpha = 0.3f))

            val context = LocalContext.current

            Button(
                onClick = {
                    val shareText = buildString {
                        appendLine("🏋️ My HEFT Fitness Stats!")
                        appendLine()
                        appendLine("💪 Total Sessions: ${analytics.totalSessions}")
                        appendLine("🔄 Total Reps: ${analytics.totalReps}")
                        appendLine("🔥 Calories Burned: ${String.format(Locale.getDefault(), "%.0f", analytics.totalCaloriesBurned)} kcal")
                        appendLine("🎯 Practice Sessions: ${analytics.totalPracticeSessions}")
                        appendLine()
                        appendLine("Tracking my home workouts with HEFT! 💪")
                        appendLine("#HomeWorkout #HEFT #Fitness #Health")
                    }
                    try {
                        // Try Facebook app first
                        val facebookIntent = android.content.Intent(
                            android.content.Intent.ACTION_SEND
                        ).apply {
                            type = "text/plain"
                            setPackage("com.facebook.katana")
                            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(facebookIntent)
                    } catch (_: Exception) {
                        // Fallback to share sheet
                        val shareIntent = android.content.Intent(
                            android.content.Intent.ACTION_SEND
                        ).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(
                            android.content.Intent.createChooser(
                                shareIntent, "Share your results"
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1877F2),
                    contentColor   = Color.White
                )
            ) {
                Text(
                    text       = "📘 Share on Facebook",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp
                )
            }
        }
    }
}

/**
 * AnalyticsStatItem – single stat display.
 */
@Composable
fun AnalyticsStatItem(
    value: String,
    label: String,
    emoji: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = emoji,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = NeonGreen,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

/**
 * PersonalBestsCard – shows personal best for each exercise.
 */
@Composable
fun PersonalBestsCard(analytics: Analytics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card title
            Text(
                text = "🏆 Personal Bests",
                color = NeonGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            if (analytics.personalBests.isEmpty()) {
                // Empty state
                Text(
                    text = "No personal bests yet!\nStart exercising to set records.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Personal bests list
                analytics.personalBests.forEach { (exerciseType, best) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Exercise type
                        Text(
                            text = "⭐ $exerciseType",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )

                        // Best sets x reps
                        Text(
                            text = "${best.bestSets}×${best.bestReps}",
                            color = NeonGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Calories
                        Text(
                            text = "🔥${String.format(Locale.getDefault(), "%.1f", best.calories)}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    HorizontalDivider(
                        color = NeonGreen.copy(alpha = 0.1f),
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}

/**
 * ExerciseBreakdownCard – shows breakdown of exercises by type.
 */
@Composable
fun ExerciseBreakdownCard(exercises: List<Exercise>) {
    // Group exercises by type
    val breakdown = exercises
        .groupBy { it.exerciseType }
        .map { (type, list) ->
            Triple(
                type,
                list.size,
                list.sumOf { it.caloriesBurned }
            )
        }
        .sortedByDescending { it.second }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card title
            Text(
                text = "📈 Exercise Breakdown",
                color = NeonGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            if (breakdown.isEmpty()) {
                Text(
                    text = "No exercises logged yet!",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                breakdown.forEach { (type, count, calories) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Exercise type
                        Text(
                            text = type,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )

                        // Count
                        Text(
                            text = "$count sessions",
                            color = NeonGreen,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Calories
                        Text(
                            text = "🔥${String.format(Locale.getDefault(), "%.0f", calories)}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    // Progress bar
                    val maxCount = breakdown.maxOf { it.second }
                    LinearProgressIndicator(
                        progress = { count.toFloat() / maxCount.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        color = NeonGreen,
                        trackColor = NeonGreen.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}