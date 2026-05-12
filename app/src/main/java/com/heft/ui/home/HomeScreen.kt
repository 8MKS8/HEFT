package com.heft.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.google.firebase.auth.FirebaseAuth
import com.heft.ui.auth.CardBackground
import com.heft.ui.auth.DarkBackground
import com.heft.ui.auth.NeonGreen
import com.heft.ui.auth.TextPrimary
import com.heft.ui.auth.TextSecondary

/**
 * HomeScreen – Main landing screen after login.
 *
 * Features:
 *  • Hamburger menu top RIGHT (dialog popup)
 *  • Welcome header
 *  • Quick Add button
 *  • 2x2 navigation grid
 */
@Composable
fun HomeScreen(
    onAddExercise: () -> Unit,
    onHistory: () -> Unit,
    onAnalytics: () -> Unit,
    onPractice: () -> Unit,
    onProfile: () -> Unit,
    onLogout: () -> Unit
) {
    // Controls whether the menu dialog is showing
    var showMenu by remember { mutableStateOf(false) }

    // Get current user email from Firebase
    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

    // ── Menu Dialog ───────────────────────────────────────────────────────
    if (showMenu) {
        AlertDialog(
            onDismissRequest = { showMenu = false },
            containerColor = CardBackground,
            title = {
                // Dialog header
                Column {
                    Text(
                        text = "HEFT",
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = userEmail,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            },
            text = {
                Column {
                    Divider(color = NeonGreen.copy(alpha = 0.3f))

                    Spacer(modifier = Modifier.height(8.dp))

                    // Profile menu item
                    DrawerMenuItem(
                        emoji = "👤",
                        title = "Profile",
                        subtitle = "View and edit your profile",
                        onClick = {
                            showMenu = false
                            onProfile()
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Notifications menu item
                    DrawerMenuItem(
                        emoji = "🔔",
                        title = "Notifications",
                        subtitle = "Manage your notifications",
                        onClick = { showMenu = false }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Divider(color = NeonGreen.copy(alpha = 0.3f))

                    Spacer(modifier = Modifier.height(4.dp))

                    // Logout menu item
                    DrawerMenuItem(
                        emoji = "🚪",
                        title = "Logout",
                        subtitle = "Sign out of your account",
                        titleColor = Color.Red,
                        onClick = {
                            showMenu = false
                            onLogout()
                        }
                    )
                }
            },
            confirmButton = {}
        )
    }

    // ── Main Screen Content ───────────────────────────────────────────────
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
            // Spacer to push title to center
            Spacer(modifier = Modifier.width(44.dp))

            // App title centered
            Text(
                text = "HEFT",
                color = NeonGreen,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            // Hamburger menu — 3 lines on RIGHT side
            Column(
                modifier = Modifier
                    .clickable { showMenu = true }
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Draw 3 horizontal lines
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(3.dp)
                            .background(
                                NeonGreen,
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Welcome Header ────────────────────────────────────────────────
        Text(
            text = "Welcome to Home Exercise",
            color = TextSecondary,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Fitness Tracker (HEFT)",
            color = NeonGreen,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Quick Add Button ──────────────────────────────────────────────
        Button(
            onClick = onAddExercise,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(200.dp)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonGreen,
                contentColor = DarkBackground
            )
        ) {
            Text(
                text = "Quick Add",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Navigation Grid Row 1 ─────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Add Exercise card
            HomeGridCard(
                emoji = "➕",
                title = "ADD EXERCISE",
                modifier = Modifier.weight(1f),
                onClick = onAddExercise
            )

            // History card
            HomeGridCard(
                emoji = "🕐",
                title = "HISTORY",
                modifier = Modifier.weight(1f),
                onClick = onHistory
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Navigation Grid Row 2 ─────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Analytics card
            HomeGridCard(
                emoji = "📊",
                title = "ANALYTICS",
                modifier = Modifier.weight(1f),
                onClick = onAnalytics
            )

            // Practice card
            HomeGridCard(
                emoji = "🎯",
                title = "PRACTICE",
                modifier = Modifier.weight(1f),
                onClick = onPractice
            )
        }
    }
}

/**
 * HomeGridCard – Reusable navigation card for the home screen grid.
 */
@Composable
fun HomeGridCard(
    emoji: String,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Card icon
            Text(
                text = emoji,
                fontSize = 36.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Card label
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}