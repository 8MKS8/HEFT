package com.heft.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// Colours for our dark fitness theme
val DarkBackground = Color(0xFF121212)
val CardBackground = Color(0xFF1E1E1E)
val NeonGreen      = Color(0xFF39FF14)
val TextPrimary    = Color.White
val TextSecondary  = Color(0xFFA0A0A0)

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    // Track which mode we are in — Login or Register
    var isLoginMode by remember { mutableStateOf(true) }

    // Input field states
    var email       by remember { mutableStateOf("") }
    var password    by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // Observe auth state
    val authState by viewModel.authState.collectAsState()

    // Navigate to home when login/register succeeds
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onAuthSuccess()
        }
    }

    // Full screen dark background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── App Logo & Title ─────────────────────────────────────────
            Text(
                text = "💪",
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "HEFT",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = NeonGreen
            )

            Text(
                text = "Home Exercise Fitness Tracker",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Card ─────────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Title
                    Text(
                        text = if (isLoginMode) "Welcome Back!" else "Create Account",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Name field — only show on Register
                    AnimatedVisibility(visible = !isLoginMode) {
                        HEFTTextField(
                            value = displayName,
                            onValueChange = { displayName = it },
                            label = "Full Name"
                        )
                    }

                    // Email field
                    HEFTTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        keyboardType = KeyboardType.Email
                    )

                    // Password field
                    HEFTTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        showPassword = showPassword,
                        onTogglePassword = { showPassword = !showPassword }
                    )

                    // Error message
                    if (authState is AuthState.Error) {
                        Text(
                            text = (authState as AuthState.Error).message,
                            color = Color.Red,
                            fontSize = 13.sp
                        )
                    }

                    // Login / Register button
                    Button(
                        onClick = {
                            if (isLoginMode) {
                                viewModel.login(email, password)
                            } else {
                                viewModel.register(email, password, displayName)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGreen,
                            contentColor   = DarkBackground
                        ),
                        enabled = authState !is AuthState.Loading
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(
                                color = DarkBackground,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = if (isLoginMode) "LOGIN" else "REGISTER",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    // Switch between Login and Register
                    TextButton(
                        onClick = {
                            isLoginMode = !isLoginMode
                            viewModel.resetState()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isLoginMode)
                                "Don't have an account? Register"
                            else
                                "Already have an account? Login",
                            color = NeonGreen,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

// ── Reusable Text Field ───────────────────────────────────────────────────────

@Composable
fun HEFTTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondary) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword && !showPassword)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        trailingIcon = {
            if (isPassword && onTogglePassword != null) {
                TextButton(onClick = onTogglePassword) {
                    Text(
                        text = if (showPassword) "Hide" else "Show",
                        color = NeonGreen,
                        fontSize = 12.sp
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = NeonGreen,
            unfocusedBorderColor = TextSecondary,
            focusedTextColor     = TextPrimary,
            unfocusedTextColor   = TextPrimary,
            cursorColor          = NeonGreen
        ),
        shape = RoundedCornerShape(8.dp)
    )
}