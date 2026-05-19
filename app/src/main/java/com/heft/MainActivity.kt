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
import com.heft.ui.exercise.ExerciseScreen
import com.heft.ui.history.HistoryScreen
import com.heft.ui.profile.ProfileScreen
import com.heft.ui.NotificationsScreen
import com.heft.ui.analytics.AnalyticsScreen
import com.heft.ui.practice.PracticeScreen


class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val authViewModel: AuthViewModel = viewModel()


            Surface(
                modifier = Modifier.fillMaxSize(),
                color = DarkBackground
            ) {
                NavHost(
                    navController = navController,
                    startDestination = if (authViewModel.isLoggedIn) "home" else "auth"
                ) {
                    composable("auth") {
                        AuthScreen(
                            onAuthSuccess = {
                                navController.navigate("home") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("home") {
                        HomeScreen(
                            onAddExercise = { navController.navigate("exercise") },
                            onHistory = { navController.navigate("history") },
                            onAnalytics = { navController.navigate("analytics") },
                            onPractice = { navController.navigate("practice") },
                            onProfile = { navController.navigate("profile") },
                            onNotifications = { navController.navigate("notifications") },
                            onLogout = {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate("auth") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("exercise") {
                        ExerciseScreen(onBack = { navController.popBackStack() })
                    }

                    composable("history") {
                        HistoryScreen(onBack = { navController.popBackStack() })
                    }

                    composable("profile") {
                        ProfileScreen(onBack = { navController.popBackStack() })
                    }

                    composable("notifications") {
                        NotificationsScreen(onBack = { navController.popBackStack() })
                    }

                    composable("analytics") {
                        AnalyticsScreen(onBack = { navController.popBackStack() })
                    }

                    composable("practice") {
                        PracticeScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}