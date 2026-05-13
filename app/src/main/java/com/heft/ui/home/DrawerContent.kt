package com.heft.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heft.ui.auth.CardBackground
import com.heft.ui.auth.NeonGreen
import com.heft.ui.auth.TextPrimary
import com.heft.ui.auth.TextSecondary

/**
 * DrawerContent – Side menu that slides in from the left
 * when the hamburger menu icon is tapped.
 *
 * Contains:
 *  • App header with user info
 *  • Profile option
 *  • Notifications option
 *  • Logout option
 *
 * @param userEmail    – current logged in user email
 * @param onProfile    – navigates to Profile screen
 * @param onNotifications – navigates to Notifications
 * @param onLogout     – logs out the user
 * @param onClose      – closes the drawer
 */
@Composable
fun DrawerContent(
    userEmail: String = "",
    onProfile: () -> Unit,
    onNotifications: () -> Unit,
    onLogout: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(CardBackground)
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(48.dp))

        // ── App Header ────────────────────────────────────────────────────
        Text(
            text = "HEFT",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = NeonGreen
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Show current user email
        Text(
            text = userEmail,
            fontSize = 13.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Divider ───────────────────────────────────────────────────────
        Divider(color = NeonGreen.copy(alpha = 0.3f))

        Spacer(modifier = Modifier.height(24.dp))

        // ── Menu Items ────────────────────────────────────────────────────

        // Profile option
        DrawerMenuItem(
            emoji = "👤",
            title = "Profile",
            subtitle = "View and edit your profile",
            onClick = {
                onClose()
                onProfile()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Notifications option
        DrawerMenuItem(
            emoji = "🔔",
            title = "Notifications",
            subtitle = "Manage your notifications",
            onClick = {
                onClose()
                onNotifications()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Divider ───────────────────────────────────────────────────────
        Divider(color = NeonGreen.copy(alpha = 0.3f))

        Spacer(modifier = Modifier.height(8.dp))

        // Logout option — at the bottom
        DrawerMenuItem(
            emoji = "🚪",
            title = "Logout",
            subtitle = "Sign out of your account",
            titleColor = androidx.compose.ui.graphics.Color.Red,
            onClick = {
                onClose()
                onLogout()
            }
        )
    }
}

/**
 * DrawerMenuItem – Reusable menu item row for the drawer.
 *
 * @param emoji      – icon for the menu item
 * @param title      – main label
 * @param subtitle   – description text below title
 * @param titleColor – color of the title text
 * @param onClick    – action when item is tapped
 */
@Composable
fun DrawerMenuItem(
    emoji: String,
    title: String,
    subtitle: String,
    titleColor: androidx.compose.ui.graphics.Color = TextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Menu item icon
        Text(
            text = emoji,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Menu item text
        Column {
            Text(
                text = title,
                color = titleColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}