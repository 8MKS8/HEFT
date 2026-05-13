package com.heft.ui.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.work.*
import com.heft.notifications.ExerciseWorker
import com.heft.notifications.HydrationWorker
import com.heft.ui.auth.CardBackground
import com.heft.ui.auth.DarkBackground
import com.heft.ui.auth.NeonGreen
import com.heft.ui.auth.TextPrimary
import com.heft.ui.auth.TextSecondary
import java.util.concurrent.TimeUnit

/**
 * NotificationsScreen – lets the user configure two types of reminders:
 *
 *  1. Exercise reminders — set day and time for workout notifications
 *  2. Hydration reminders — set interval between water intake reminders
 *
 * @param onBack – navigates back to Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onBack: () -> Unit) {

    val context = LocalContext.current

    // ── Exercise Reminder States ──────────────────────────────────────────
    var exerciseEnabled    by remember { mutableStateOf(false) }
    var exerciseHour       by remember { mutableStateOf("07") }
    var exerciseMinute     by remember { mutableStateOf("00") }
    var selectedDays       by remember { mutableStateOf(setOf<String>()) }

    // ── Hydration Reminder States ─────────────────────────────────────────
    var hydrationEnabled   by remember { mutableStateOf(false) }
    var hydrationInterval  by remember { mutableStateOf("2") }
    var hydrationStartHour by remember { mutableStateOf("08") }
    var hydrationEndHour   by remember { mutableStateOf("22") }

    // Days of week
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    // Feedback message
    var feedbackMessage by remember { mutableStateOf("") }

    // ── Notification Permission ───────────────────────────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        feedbackMessage = if (isGranted) "Permission granted!" else "Permission denied!"
    }

    // Check if notification permission is granted
    val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else true

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
                text = "Notifications",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(80.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Permission Banner ─────────────────────────────────────────────
        if (!hasPermission) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF5C3317)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚠️ Notification permission required",
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                        fontSize = 13.sp
                    )
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGreen,
                            contentColor   = DarkBackground
                        )
                    ) {
                        Text("Allow")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Exercise Reminder Card ────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "💪 Exercise Reminders",
                            color = NeonGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Set days and time for workout reminders",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    // Enable/disable toggle
                    Switch(
                        checked = exerciseEnabled,
                        onCheckedChange = { exerciseEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor  = DarkBackground,
                            checkedTrackColor  = NeonGreen
                        )
                    )
                }

                // Show options only when enabled
                if (exerciseEnabled) {

                    // ── Day Selection ─────────────────────────────────────
                    Text(
                        text = "Select Days",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    // Day chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        daysOfWeek.forEach { day ->
                            val isSelected = day in selectedDays
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedDays = if (isSelected) {
                                        selectedDays - day
                                    } else {
                                        selectedDays + day
                                    }
                                },
                                label = {
                                    Text(
                                        text = day,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonGreen,
                                    selectedLabelColor     = DarkBackground
                                )
                            )
                        }
                    }

                    // ── Time Selection ────────────────────────────────────
                    Text(
                        text = "Reminder Time",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Hour input
                        OutlinedTextField(
                            value = exerciseHour,
                            onValueChange = {
                                if (it.length <= 2) exerciseHour = it
                            },
                            label = { Text("Hour", color = TextSecondary) },
                            modifier = Modifier.weight(1f),
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

                        Text(
                            text = ":",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Minute input
                        OutlinedTextField(
                            value = exerciseMinute,
                            onValueChange = {
                                if (it.length <= 2) exerciseMinute = it
                            },
                            label = { Text("Min", color = TextSecondary) },
                            modifier = Modifier.weight(1f),
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
                    }

                    // Save exercise reminder button
                    Button(
                        onClick = {
                            val hour   = exerciseHour.toIntOrNull() ?: 7
                            val minute = exerciseMinute.toIntOrNull() ?: 0

                            // Schedule exercise reminder with WorkManager
                            scheduleExerciseReminder(
                                context      = context,
                                hour         = hour,
                                minute       = minute,
                                selectedDays = selectedDays
                            )
                            feedbackMessage = "💪 Exercise reminders set for " +
                                    "${exerciseHour.padStart(2,'0')}:" +
                                    "${exerciseMinute.padStart(2,'0')}"
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGreen,
                            contentColor   = DarkBackground
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save Exercise Reminder", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Hydration Reminder Card ───────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "💧 Hydration Reminders",
                            color = NeonGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Set interval between water reminders",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    // Enable/disable toggle
                    Switch(
                        checked = hydrationEnabled,
                        onCheckedChange = { hydrationEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DarkBackground,
                            checkedTrackColor = NeonGreen
                        )
                    )
                }

                // Show options only when enabled
                if (hydrationEnabled) {

                    // ── Active Hours ──────────────────────────────────────
                    Text(
                        text = "Active Hours",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Start hour
                        OutlinedTextField(
                            value = hydrationStartHour,
                            onValueChange = {
                                if (it.length <= 2) hydrationStartHour = it
                            },
                            label = { Text("From (hr)", color = TextSecondary) },
                            modifier = Modifier.weight(1f),
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

                        Text(
                            text = "to",
                            color = TextPrimary,
                            fontSize = 16.sp
                        )

                        // End hour
                        OutlinedTextField(
                            value = hydrationEndHour,
                            onValueChange = {
                                if (it.length <= 2) hydrationEndHour = it
                            },
                            label = { Text("To (hr)", color = TextSecondary) },
                            modifier = Modifier.weight(1f),
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
                    }

                    // ── Interval ──────────────────────────────────────────
                    Text(
                        text = "Remind every (hours)",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    OutlinedTextField(
                        value = hydrationInterval,
                        onValueChange = { hydrationInterval = it },
                        label = { Text("Interval in hours", color = TextSecondary) },
                        placeholder = { Text("e.g. 2", color = TextSecondary) },
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

                    // Save hydration reminder button
                    Button(
                        onClick = {
                            val intervalHours = hydrationInterval.toLongOrNull() ?: 2L

                            // Schedule hydration reminder with WorkManager
                            scheduleHydrationReminder(
                                context       = context,
                                intervalHours = intervalHours
                            )
                            feedbackMessage = "💧 Hydration reminders set every " +
                                    "$hydrationInterval hour(s)"
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGreen,
                            contentColor   = DarkBackground
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save Hydration Reminder", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Cancel All Button ─────────────────────────────────────────────
        OutlinedButton(
            onClick = {
                // Cancel all scheduled notifications
                WorkManager.getInstance(context).cancelAllWork()
                feedbackMessage = "All reminders cancelled"
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Red
            )
        ) {
            Text(
                text = "Cancel All Reminders",
                fontWeight = FontWeight.Bold
            )
        }

        // ── Feedback Message ──────────────────────────────────────────────
        if (feedbackMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackground
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = feedbackMessage,
                    color = NeonGreen,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * scheduleExerciseReminder – schedules a daily exercise reminder
 * using WorkManager at the specified time.
 */
fun scheduleExerciseReminder(
    context: Context,
    hour: Int,
    minute: Int,
    selectedDays: Set<String>
) {
    // Calculate delay until next occurrence
    val currentTime   = System.currentTimeMillis()
    val calendar      = java.util.Calendar.getInstance()
    calendar.set(java.util.Calendar.HOUR_OF_DAY, hour)
    calendar.set(java.util.Calendar.MINUTE, minute)
    calendar.set(java.util.Calendar.SECOND, 0)

    // If time has passed today schedule for tomorrow
    if (calendar.timeInMillis <= currentTime) {
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
    }

    val delay = calendar.timeInMillis - currentTime

    // Create periodic work request — repeats every 24 hours
    val exerciseRequest = PeriodicWorkRequestBuilder<ExerciseWorker>(
        24, TimeUnit.HOURS
    )
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .build()

    // Schedule with WorkManager
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "exercise_reminder",
        ExistingPeriodicWorkPolicy.REPLACE,
        exerciseRequest
    )
}

/**
 * scheduleHydrationReminder – schedules periodic hydration reminders
 * using WorkManager at the specified interval.
 */
fun scheduleHydrationReminder(
    context: Context,
    intervalHours: Long
) {
    // Create periodic work request
    val hydrationRequest = PeriodicWorkRequestBuilder<HydrationWorker>(
        intervalHours, TimeUnit.HOURS
    )
        .build()

    // Schedule with WorkManager
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "hydration_reminder",
        ExistingPeriodicWorkPolicy.REPLACE,
        hydrationRequest
    )
}