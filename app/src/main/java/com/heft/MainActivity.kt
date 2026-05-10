package com.heft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.heft.ui.auth.AuthScreen
import com.heft.ui.auth.AuthViewModel
import com.heft.ui.auth.DarkBackground
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.heft.ui.auth.NeonGreen
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Navigation controller
            val navController = rememberNavController()

            // Auth ViewModel
            val authViewModel: AuthViewModel = viewModel()

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = DarkBackground
            ) {
                NavHost(
                    navController = navController,
                    // Start at home if logged in, otherwise auth
                    startDestination = if (authViewModel.isLoggedIn) "home" else "auth"
                ) {
                    // Auth screen — Login / Register
                    composable("auth") {
                        AuthScreen(
                            onAuthSuccess = {
                                navController.navigate("home") {
                                    // Clear back stack so user cant go back to login
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        )
                    }

                    // Home screen — temporary placeholder
                    composable("home") {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(DarkBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Welcome to HEFT! 💪",
                                color = NeonGreen,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}