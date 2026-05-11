package com.heft.ui.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

/**
 * ExerciseScreen – Add Exercise screen.
 *
 * Features:
 *  • Exercise type dropdown
 *  • Sets and reps input
 *  • Notes input
 *  • Automatic calorie calculation
 *  • Save to Firebase Firestore
 *
 * @param onBack – navigates back to Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(
    onBack: () -> Unit,
    viewModel: ExerciseViewModel = viewModel()
) {
    // ── Input States ──────────────────────────────────────────────────────
    var selectedExercise by remember { mutableStateOf("") }
    var sets             by remember { mutableStateOf("") }
    var reps             by remember { mutableStateOf("") }
    var notes            by remember { mutableStateOf("") }
    var expanded         by remember { mutableStateOf(false) }

    // Observe exercise state
    val exerciseState by viewModel.exerciseState.collectAsState()

    // Scroll state for smaller screens
    val scrollState = rememberScrollState()

    // Show success dialog
    var showSuccessDialog by remember { mutableStateOf(false) }
    var savedCalories     by remember { mutableStateOf(0.0) }

    // Handle state changes
    LaunchedEffect(exerciseState) {
        when (val state = exerciseState) {
            is ExerciseState.Success -> {
                savedCalories = state.calories
                showSuccessDialog = true
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
                    text = "Workout Saved! 💪",
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Great job! You burned approximately " +
                            "${String.format("%.1f", savedCalories)} calories!",
                    color = TextPrimary
                )
            },
            confirmButton = {
                // Add another exercise
                Button(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.resetState()
                        // Clear form
                        selectedExercise = ""
                        sets  = ""
                        reps  = ""
                        notes = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen,
                        contentColor   = DarkBackground
                    )
                ) {
                    Text("Add Another")
                }
            },
            dismissButton = {
                // Go back to home
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
            .verticalScroll(scrollState)
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
                text = "Add Exercise",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            // Spacer to balance back button
            Spacer(modifier = Modifier.width(80.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Exercise Card ─────────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = CardBackground
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ── Exercise Type Dropdown ────────────────────────────────
                Text(
                    text = "Exercise Type",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                // Dropdown menu for exercise selection
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedExercise.ifBlank { "Select exercise..." },
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

                    // Dropdown items
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(CardBackground)
                    ) {
                        Exercise.exerciseTypes.forEach { type ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = type,
                                        color = TextPrimary
                                    )
                                },
                                onClick = {
                                    selectedExercise = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // ── Sets and Reps Row ─────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Sets input
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sets",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = sets,
                            onValueChange = { sets = it },
                            placeholder = {
                                Text("e.g. 3", color = TextSecondary)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = NeonGreen,
                                unfocusedBorderColor = TextSecondary,
                                focusedTextColor     = TextPrimary,
                                unfocusedTextColor   = TextPrimary,
                                cursorColor          = NeonGreen
                            ),
                            shape = RoundedCornerShape(8.dp),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            )
                        )
                    }

                    // Reps input
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reps",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = reps,
                            onValueChange = { reps = it },
                            placeholder = {
                                Text("e.g. 10", color = TextSecondary)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = NeonGreen,
                                unfocusedBorderColor = TextSecondary,
                                focusedTextColor     = TextPrimary,
                                unfocusedTextColor   = TextPrimary,
                                cursorColor          = NeonGreen
                            ),
                            shape = RoundedCornerShape(8.dp),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            )
                        )
                    }
                }

                // ── Calorie Preview ───────────────────────────────────────
                // Show estimated calories as user types
                if (selectedExercise.isNotBlank() &&
                    sets.toIntOrNull() != null &&
                    reps.toIntOrNull() != null
                ) {
                    val estimatedCalories = Exercise.calculateCalories(
                        selectedExercise,
                        sets.toInt(),
                        reps.toInt()
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

                // ── Notes Input ───────────────────────────────────────────
                Text(
                    text = "Notes (optional)",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = {
                        Text("How did it feel?", color = TextSecondary)
                    },
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

                // ── Error Message ─────────────────────────────────────────
                if (exerciseState is ExerciseState.Error) {
                    Text(
                        text = (exerciseState as ExerciseState.Error).message,
                        color = androidx.compose.ui.graphics.Color.Red,
                        fontSize = 13.sp
                    )
                }

                // ── Save Button ───────────────────────────────────────────
                Button(
                    onClick = {
                        viewModel.saveExercise(
                            exerciseType = selectedExercise,
                            sets         = sets,
                            reps         = reps,
                            notes        = notes
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
                    enabled = exerciseState !is ExerciseState.Loading
                ) {
                    if (exerciseState is ExerciseState.Loading) {
                        // Show spinner while saving
                        CircularProgressIndicator(
                            color    = DarkBackground,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text       = "SAVE WORKOUT",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 16.sp
                        )
                    }
                }
            }
        }
    }
}