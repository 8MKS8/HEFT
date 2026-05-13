package com.heft.ui.profile

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heft.data.model.User
import com.heft.ui.auth.CardBackground
import com.heft.ui.auth.DarkBackground
import com.heft.ui.auth.NeonGreen
import com.heft.ui.auth.TextPrimary
import com.heft.ui.auth.TextSecondary

/**
 * ProfileScreen – User profile screen.
 *
 * Features:
 *  • Display user email
 *  • Edit display name
 *  • Age, sex, height, weight
 *  • Goal weight
 *  • BMI calculator
 *  • Fitness goal and weekly target
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    // ── Input States ──────────────────────────────────────────────────────
    var displayName  by remember { mutableStateOf("") }
    var age          by remember { mutableStateOf("") }
    var sex          by remember { mutableStateOf("") }
    var heightCm     by remember { mutableStateOf("") }
    var weightKg     by remember { mutableStateOf("") }
    var goalWeightKg by remember { mutableStateOf("") }
    var fitnessGoal  by remember { mutableStateOf("") }
    var weeklyTarget by remember { mutableStateOf("5") }

    // Dropdown states
    var goalExpanded by remember { mutableStateOf(false) }
    var sexExpanded  by remember { mutableStateOf(false) }

    // Options
    val fitnessGoals = listOf(
        "Lose weight",
        "Build strength",
        "Improve endurance",
        "Stay active",
        "Train for sport"
    )
    val sexOptions = listOf("Male", "Female")

    // Calculate BMI live
    val bmi = User.calculateBMI(
        weightKg = weightKg.toDoubleOrNull() ?: 0.0,
        heightCm = heightCm.toDoubleOrNull() ?: 0.0
    )
    val bmiCategory = User.getBMICategory(bmi)

    // Observe profile state
    val profileState by viewModel.profileState.collectAsState()

    // Show saved dialog
    var showSavedDialog by remember { mutableStateOf(false) }

    LaunchedEffect(profileState) {
        if (profileState is ProfileState.Saved) {
            showSavedDialog = true
        }
    }

    // ── Saved Dialog ──────────────────────────────────────────────────────
    if (showSavedDialog) {
        AlertDialog(
            onDismissRequest = {
                showSavedDialog = false
                viewModel.resetState()
            },
            containerColor = CardBackground,
            title = {
                Text(
                    text = "Profile Saved! ✅",
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Your profile has been updated successfully!",
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSavedDialog = false
                        viewModel.resetState()
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen,
                        contentColor   = DarkBackground
                    )
                ) {
                    Text("Done")
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
                text = "Profile",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(80.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Avatar ────────────────────────────────────────────────────────
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(50.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackground
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👤", fontSize = 48.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // User email
        Text(
            text = viewModel.userEmail,
            color = TextSecondary,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Personal Info Card ────────────────────────────────────────────
        ProfileCard(title = "Personal Info") {

            // Display name
            ProfileTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = "Display Name"
            )

            // Age
            ProfileTextField(
                value = age,
                onValueChange = { age = it },
                label = "Age",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
            )

            // Sex dropdown
            Text(
                text = "Sex",
                color = TextSecondary,
                fontSize = 13.sp
            )
            ExposedDropdownMenuBox(
                expanded = sexExpanded,
                onExpandedChange = { sexExpanded = !sexExpanded }
            ) {
                OutlinedTextField(
                    value = sex.ifBlank { "Select..." },
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = sexExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = profileTextFieldColors(),
                    shape = RoundedCornerShape(8.dp)
                )
                ExposedDropdownMenu(
                    expanded = sexExpanded,
                    onDismissRequest = { sexExpanded = false },
                    modifier = Modifier.background(CardBackground)
                ) {
                    sexOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = TextPrimary) },
                            onClick = {
                                sex = option
                                sexExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Body Measurements Card ────────────────────────────────────────
        ProfileCard(title = "Body Measurements") {

            // Height and Weight row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Height
                Column(modifier = Modifier.weight(1f)) {
                    ProfileTextField(
                        value = heightCm,
                        onValueChange = { heightCm = it },
                        label = "Height (cm)",
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    )
                }
                // Weight
                Column(modifier = Modifier.weight(1f)) {
                    ProfileTextField(
                        value = weightKg,
                        onValueChange = { weightKg = it },
                        label = "Weight (kg)",
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    )
                }
            }

            // Goal weight
            ProfileTextField(
                value = goalWeightKg,
                onValueChange = { goalWeightKg = it },
                label = "Goal Weight (kg)",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
            )

            // BMI display — calculated automatically
            if (bmi > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = DarkBackground
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // BMI label
                        Text(
                            text = "BMI",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )

                        // BMI value and category
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = String.format("%.1f", bmi),
                                color = NeonGreen,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = bmiCategory,
                                color = when (bmiCategory) {
                                    "Normal weight" -> Color.Green
                                    "Underweight"   -> Color.Blue
                                    "Overweight"    -> Color(0xFFF39C12)
                                    "Obese"         -> Color.Red
                                    else            -> TextSecondary
                                },
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Fitness Goals Card ────────────────────────────────────────────
        ProfileCard(title = "Fitness Goals") {

            // Fitness goal dropdown
            Text(
                text = "Goal",
                color = TextSecondary,
                fontSize = 13.sp
            )
            ExposedDropdownMenuBox(
                expanded = goalExpanded,
                onExpandedChange = { goalExpanded = !goalExpanded }
            ) {
                OutlinedTextField(
                    value = fitnessGoal.ifBlank { "Select your goal..." },
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = goalExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = profileTextFieldColors(),
                    shape = RoundedCornerShape(8.dp)
                )
                ExposedDropdownMenu(
                    expanded = goalExpanded,
                    onDismissRequest = { goalExpanded = false },
                    modifier = Modifier.background(CardBackground)
                ) {
                    fitnessGoals.forEach { goal ->
                        DropdownMenuItem(
                            text = { Text(goal, color = TextPrimary) },
                            onClick = {
                                fitnessGoal = goal
                                goalExpanded = false
                            }
                        )
                    }
                }
            }

            // Weekly target
            ProfileTextField(
                value = weeklyTarget,
                onValueChange = { weeklyTarget = it },
                label = "Weekly Target (sessions)",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Error Message ─────────────────────────────────────────────────
        if (profileState is ProfileState.Error) {
            Text(
                text = (profileState as ProfileState.Error).message,
                color = Color.Red,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // ── Save Button ───────────────────────────────────────────────────
        Button(
            onClick = {
                viewModel.saveProfile(
                    displayName  = displayName,
                    fitnessGoal  = fitnessGoal,
                    weeklyTarget = weeklyTarget.toIntOrNull() ?: 5
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
            enabled = profileState !is ProfileState.Saving
        ) {
            if (profileState is ProfileState.Saving) {
                CircularProgressIndicator(
                    color    = DarkBackground,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text       = "SAVE PROFILE",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── Reusable Components ───────────────────────────────────────────────────────

/**
 * ProfileCard – reusable card with title for profile sections.
 */
@Composable
fun ProfileCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card section title
            Text(
                text = title,
                color = NeonGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}

/**
 * ProfileTextField – reusable text field for profile inputs.
 */
@Composable
fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: androidx.compose.ui.text.input.KeyboardType =
        androidx.compose.ui.text.input.KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondary) },
        modifier = Modifier.fillMaxWidth(),
        colors = profileTextFieldColors(),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = keyboardType
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

/**
 * profileTextFieldColors – reusable text field colors for profile screen.
 */
@Composable
fun profileTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = NeonGreen,
    unfocusedBorderColor = TextSecondary,
    focusedTextColor     = TextPrimary,
    unfocusedTextColor   = TextPrimary,
    cursorColor          = NeonGreen
)