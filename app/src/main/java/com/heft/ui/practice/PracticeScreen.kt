package com.heft.ui.practice

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.heft.ui.auth.CardBackground
import com.heft.ui.auth.DarkBackground
import com.heft.ui.auth.NeonGreen
import com.heft.ui.auth.TextPrimary
import com.heft.ui.auth.TextSecondary
import com.heft.data.model.Exercise

/**
 * PracticeScreen – Supervised Practice screen.
 *
 * Features:
 *  • Exercise selector
 *  • YouTube tutorial video (internet connection required)
 *  • Duration input (no sets/reps — free practice)
 *  • Automatic calorie calculation
 *  • Save practice session to Firestore
 *
 * @param onBack – navigates back to Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    onBack: () -> Unit,
    viewModel: PracticeViewModel = viewModel()
) {
    // ── Input States ──────────────────────────────────────────────────────
    var selectedExercise  by remember { mutableStateOf(viewModel.exercisesWithVideos.first()) }
    var duration          by remember { mutableStateOf("") }
    var notes             by remember { mutableStateOf("") }
    var videoWatched      by remember { mutableStateOf(false) }
    var expanded          by remember { mutableStateOf(false) }

    // Observe practice state
    val practiceState by viewModel.practiceState.collectAsState()

    // Success dialog
    var showSuccessDialog by remember { mutableStateOf(false) }
    var savedCalories     by remember { mutableStateOf(0.0) }

    // Handle state changes
    LaunchedEffect(practiceState) {
        when (val state = practiceState) {
            is PracticeState.Success -> {
                savedCalories = state.calories
                showSuccessDialog = true
                videoWatched = false
            }
            else -> {}
        }
    }

    // ── Success Dialog ────────────────────────────────────────────────────
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                viewModel.resetState()
            },
            containerColor = CardBackground,
            title = {
                Text(
                    text = "Practice Saved! 🎯",
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Great practice session! You burned " +
                            "approximately ${String.format("%.1f", savedCalories)} calories!",
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.resetState()
                        duration = ""
                        notes    = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen,
                        contentColor   = DarkBackground
                    )
                ) {
                    Text("Practice Again")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    viewModel.resetState()
                    onBack()
                }) {
                    Text("Done", color = NeonGreen)
                }
            }
        )
    }

    // ── Main Screen ───────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // ── Top Bar ───────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text(
                    text = "← Back",
                    color = NeonGreen,
                    fontSize = 16.sp
                )
            }
            Text(
                text = "Practice",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(80.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Exercise Selector Card ────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Select Exercise",
                    color = NeonGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                // Exercise dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedExercise.name,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = NeonGreen,
                            unfocusedBorderColor = TextSecondary,
                            focusedTextColor     = TextPrimary,
                            unfocusedTextColor   = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(CardBackground)
                    ) {
                        viewModel.exercisesWithVideos.forEach { exercise ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = exercise.name,
                                        color = TextPrimary
                                    )
                                },
                                onClick = {
                                    selectedExercise = exercise
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── YouTube Player Card ───────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "📹 Tutorial Video",
                    color = NeonGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Watch the video to learn proper form",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                // YouTube Player — requires internet connection
                YouTubePlayer(
                    videoId      = selectedExercise.videoId,
                    onVideoReady = { videoWatched = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        // ── Training Plan Card — show for all exercises except Plank ─────
        if (selectedExercise.name != "Plank") {
            TrainingPlanCard(
                exerciseName = selectedExercise.name,
                onSave = { sets, reps, calories ->
                    // Estimate duration based on sets and reps
                    // Average 3 seconds per rep + rest time between sets
                    val estimatedDuration = ((sets * reps * 3) / 60).coerceAtLeast(1)

                    viewModel.savePractice(
                        exerciseType    = selectedExercise.name,
                        durationMinutes = estimatedDuration.toString(),
                        videoWatched    = videoWatched,
                        notes           = "$sets sets × $reps reps — ${String.format("%.1f", calories)} kcal"
                    )
                }
            )
        }

        // ── Practice Log Card — only show for Plank ──────────────────────
        if (selectedExercise.name == "Plank") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Log Plank Session",
                        color = NeonGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Duration input
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Duration (minutes)", color = TextSecondary) },
                        placeholder = { Text("e.g. 5", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = NeonGreen,
                            unfocusedBorderColor = TextSecondary,
                            focusedTextColor     = TextPrimary,
                            unfocusedTextColor   = TextPrimary,
                            cursorColor          = NeonGreen
                        ),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Calorie preview
                    if (duration.toIntOrNull() != null && duration.toInt() > 0) {
                        val estimatedCalories = com.heft.data.model.Practice.calculateCalories(
                            selectedExercise.name,
                            duration.toInt()
                        )
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = DarkBackground
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "🔥 Estimated calories: " +
                                        "${String.format("%.1f", estimatedCalories)} kcal",
                                color = NeonGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // Notes input
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (optional)", color = TextSecondary) },
                        placeholder = { Text("How long did you hold it?", color = TextSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = NeonGreen,
                            unfocusedBorderColor = TextSecondary,
                            focusedTextColor     = TextPrimary,
                            unfocusedTextColor   = TextPrimary,
                            cursorColor          = NeonGreen
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Error message
                    if (practiceState is PracticeState.Error) {
                        Text(
                            text = (practiceState as PracticeState.Error).message,
                            color = Color.Red,
                            fontSize = 13.sp
                        )
                    }

                    // Save button
                    Button(
                        onClick = {
                            viewModel.savePractice(
                                exerciseType    = selectedExercise.name,
                                durationMinutes = duration,
                                videoWatched    = videoWatched,
                                notes           = notes
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGreen,
                            contentColor   = DarkBackground
                        ),
                        enabled = practiceState !is PracticeState.Loading
                    ) {
                        if (practiceState is PracticeState.Loading) {
                            CircularProgressIndicator(
                                color    = DarkBackground,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text       = "SAVE PRACTICE",
                                fontWeight = FontWeight.Bold,
                                fontSize   = 16.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * YouTubePlayer – embeds a YouTube video player.
 * Requires internet connection to load videos.
 *
 * @param videoId    – YouTube video ID
 * @param onVideoReady – called when video is ready to play
 */
@Composable
fun YouTubePlayer(
    videoId: String,
    onVideoReady: () -> Unit = {}
) {
    key(videoId) {
        AndroidView(
            factory = { context ->
                YouTubePlayerView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            // Load the video when player is ready
                            youTubePlayer.cueVideo(videoId, 0f)
                            onVideoReady()
                        }
                    })
                }
            },
            update = { _ ->
                // Video updates handled by onReady callback
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )
    }
}

/**
 * TrainingPlanCard – shows beginner/pro training plan
 * based on user's maximum reps input.
 *
 * Beginner: 5 sets × (maxReps × 0.40-0.45)
 * Pro:      5 sets × (maxReps × 0.60-0.65)
 *
 * @param exerciseName – selected exercise name
 * @param onSave       – saves the training to history
 */
@Composable
fun TrainingPlanCard(
    exerciseName: String,
    onSave: (sets: Int, reps: Int, calories: Double) -> Unit
) {
    // ── States ────────────────────────────────────────────────────────────
    var maxReps      by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf("") } // "Beginner" or "Pro"
    var trainingPlan  by remember { mutableStateOf<TrainingPlan?>(null) }

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
                text = "🏋️ Training Plan",
                color = NeonGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // Max reps input
            Text(
                text = "How many $exerciseName can you do?",
                color = TextSecondary,
                fontSize = 13.sp
            )

            OutlinedTextField(
                value = maxReps,
                onValueChange = {
                    maxReps = it
                    // Reset plan when input changes
                    trainingPlan = null
                    selectedLevel = ""
                },
                label = { Text("Max reps", color = TextSecondary) },
                placeholder = { Text("e.g. 20", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = NeonGreen,
                    unfocusedBorderColor = TextSecondary,
                    focusedTextColor     = TextPrimary,
                    unfocusedTextColor   = TextPrimary,
                    cursorColor          = NeonGreen
                ),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                ),
                shape = RoundedCornerShape(8.dp)
            )

            // Show level selection only when max reps is entered
            if (maxReps.toIntOrNull() != null && maxReps.toInt() > 0) {

                Text(
                    text = "Select your level:",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                // Beginner / Pro buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Beginner button
                    Button(
                        onClick = {
                            selectedLevel = "Beginner"
                            val max = maxReps.toInt()
                            // Random multiplier between 0.40 and 0.45
                            val multiplier = (0.40 + Math.random() * 0.05)
                            val repsPerSet = (max * multiplier).toInt().coerceAtLeast(1)
                            val calories   = Exercise.calculateCalories(
                                exerciseName, 5, repsPerSet
                            )
                            trainingPlan = TrainingPlan(
                                level      = "Beginner",
                                sets       = 5,
                                repsPerSet = repsPerSet,
                                calories   = calories
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedLevel == "Beginner")
                                NeonGreen else CardBackground,
                            contentColor = if (selectedLevel == "Beginner")
                                DarkBackground else TextPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, NeonGreen
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🌱",
                                fontSize = 20.sp
                            )
                            Text(
                                text = "Beginner",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "40-45%",
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Pro button
                    Button(
                        onClick = {
                            selectedLevel = "Pro"
                            val max = maxReps.toInt()
                            // Random multiplier between 0.60 and 0.65
                            val multiplier = (0.60 + Math.random() * 0.05)
                            val repsPerSet = (max * multiplier).toInt().coerceAtLeast(1)
                            val calories   = Exercise.calculateCalories(
                                exerciseName, 5, repsPerSet
                            )
                            trainingPlan = TrainingPlan(
                                level      = "Pro",
                                sets       = 5,
                                repsPerSet = repsPerSet,
                                calories   = calories
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedLevel == "Pro")
                                NeonGreen else CardBackground,
                            contentColor = if (selectedLevel == "Pro")
                                DarkBackground else TextPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, NeonGreen
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "💪",
                                fontSize = 20.sp
                            )
                            Text(
                                text = "Pro",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "60-65%",
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // ── Training Plan Result ──────────────────────────────────────
            trainingPlan?.let { plan ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = DarkBackground
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Plan title
                        Text(
                            text = "📋 Your ${plan.level} Plan",
                            color = NeonGreen,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        HorizontalDivider(
                            color = NeonGreen.copy(alpha = 0.3f)
                        )

                        // Sets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Sets",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${plan.sets}",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Reps per set
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Reps per set",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${plan.repsPerSet}",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Total reps
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total reps",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${plan.sets * plan.repsPerSet}",
                                color = NeonGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        HorizontalDivider(
                            color = NeonGreen.copy(alpha = 0.3f)
                        )

                        HorizontalDivider(color = NeonGreen.copy(alpha = 0.3f))

// ── User Friendly Instructions ────────────────────────────────
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = CardBackground
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Title
                                Text(
                                    text = "📋 How to do your training:",
                                    color = NeonGreen,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                // Step 1 — Sets
                                Row(verticalAlignment = Alignment.Top) {
                                    Text(text = "1️⃣  ", fontSize = 13.sp)
                                    Text(
                                        text = "Do ${plan.sets} series in total",
                                        color = TextPrimary,
                                        fontSize = 13.sp
                                    )
                                }

                                // Step 2 — Reps
                                Row(verticalAlignment = Alignment.Top) {
                                    Text(text = "2️⃣  ", fontSize = 13.sp)
                                    Text(
                                        text = "Do ${plan.repsPerSet} repetitions per series",
                                        color = TextPrimary,
                                        fontSize = 13.sp
                                    )
                                }

                                // Step 3 — Rest
                                Row(verticalAlignment = Alignment.Top) {
                                    Text(text = "3️⃣  ", fontSize = 13.sp)
                                    Text(
                                        text = "Rest between 30 seconds and 2 minutes between each series",
                                        color = TextPrimary,
                                        fontSize = 13.sp
                                    )
                                }

                                // Step 4 — Progress tip
                                Row(verticalAlignment = Alignment.Top) {
                                    Text(text = "💡  ", fontSize = 13.sp)
                                    Text(
                                        text = if (plan.level == "Beginner")
                                            "As a beginner focus on proper form rather than speed. " +
                                                    "Increase reps gradually each week!"
                                        else
                                            "As a pro push yourself but listen to your body. " +
                                                    "Shorter rest periods will increase intensity!",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = NeonGreen.copy(alpha = 0.3f))

                        // Calories burned
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔥 Calories burned",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${String.format("%.1f", plan.calories)} kcal",
                                color = NeonGreen,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Save training button
                        Button(
                            onClick = {
                                onSave(plan.sets, plan.repsPerSet, plan.calories)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonGreen,
                                contentColor   = DarkBackground
                            )
                        ) {
                            Text(
                                text       = "SAVE TRAINING",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * TrainingPlan – holds the calculated training plan data.
 */
data class TrainingPlan(
    val level      : String,
    val sets       : Int,
    val repsPerSet : Int,
    val calories   : Double
)