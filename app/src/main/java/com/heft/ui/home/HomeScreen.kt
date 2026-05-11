package com.heft.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.launch

/**
 * HomeScreen – Main landing screen after login.
 *
 * Features:
 *  • Hamburger menu (drawer) top left
 *  • Welcome header
 *  • Quick Add button
 *  • 2x2 navigation grid
 *
 * @param onAddExercise – navigates to Add Exercise screen
 * @param onHistory     – navigates to History screen
 * @param onAnalytics   – navigates to Analytics screen
 * @param onPractice    – navigates to Practice screen
 * @param onProfile     – navigates to Profile screen
 * @param onLogout      – logs out and goes to auth screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddExercise: () -> Unit,
    onHistory: () -> Unit,
    onAnalytics: () -> Unit,
    onPractice: () -> Unit,
    onProfile: () -> Unit,
    onLogout: () -> Unit
) {
    // Drawer state — open or closed
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Get current user email
    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

    // Modal Navigation Drawer — slides in from left
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Our custom drawer content
            DrawerContent(
                userEmail = userEmail,
                onProfile = onProfile,
                onNotifications = { /* TODO */ },
                onLogout = onLogout,
                onClose = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        // Main screen content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // ── Top Bar ───────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hamburger menu button — 3 lines
                Column(
                    modifier = Modifier
                        .clickable {
                            scope.launch { drawerState.open() }
                        }
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    // Three lines of hamburger menu
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

                // App title in center
                Text(
                    text = "HEFT",
                    color = NeonGreen,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                // Spacer to balance the hamburger menu
                Spacer(modifier = Modifier.width(44.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Welcome Header ────────────────────────────────────────────
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

            // ── Quick Add Button ──────────────────────────────────────────
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

            // ── Navigation Grid Row 1 ─────────────────────────────────────
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

            // ── Navigation Grid Row 2 ─────────────────────────────────────
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
}

/**
 * HomeGridCard – Reusable navigation card for the home screen grid.
 *
 * @param emoji    – icon displayed on the card
 * @param title    – label displayed below the icon
 * @param modifier – layout modifier passed from parent
 * @param onClick  – action when card is tapped
 */
@Composable
fun HomeGridCard(
    emoji: String,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Dark card with rounded corners
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {
        // Center content vertically and horizontally
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