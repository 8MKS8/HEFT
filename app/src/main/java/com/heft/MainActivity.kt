package com.heft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.heft.ui.auth.AuthScreen
import com.heft.ui.auth.AuthViewModel
import com.heft.ui.auth.DarkBackground
import com.heft.ui.home.HomeScreen
import com.google.firebase.auth.FirebaseAuth


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

                    // Home screen — main navigation hub
                    composable("home") {
                        HomeScreen(
                            // Navigate to Add Exercise screen
                            onAddExercise = {
                                navController.navigate("exercise")
                            },
                            // Navigate to History screen
                            onHistory = {
                                navController.navigate("history")
                            },
                            // Navigate to Analytics screen
                            onAnalytics = {
                                navController.navigate("analytics")
                            },
                            // Navigate to Practice screen
                            onPractice = {
                                navController.navigate("practice")
                            },
                            // Navigate to Profile screen
                            onProfile = {
                                navController.navigate("profile")
                            },
                            // Logout — clear back stack and go to auth
                            onLogout = {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate("auth") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}