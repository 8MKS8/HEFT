package com.heft.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.heft.R

/**
 * NotificationHelper – creates and manages notification channels.
 *
 * Two channels:
 *  • Exercise reminders — daily workout notifications
 *  • Hydration reminders — water intake reminders
 */
class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        // Exercise reminder channel
        const val EXERCISE_CHANNEL_ID   = "exercise_reminder_channel"
        const val EXERCISE_CHANNEL_NAME = "Exercise Reminders"

        // Hydration reminder channel
        const val HYDRATION_CHANNEL_ID   = "hydration_reminder_channel"
        const val HYDRATION_CHANNEL_NAME = "Hydration Reminders"

        // Notification IDs
        const val EXERCISE_NOTIFICATION_ID  = 1001
        const val HYDRATION_NOTIFICATION_ID = 1002
    }

    /**
     * Create all notification channels.
     * Must be called before showing any notifications.
     */
    fun createNotificationChannels() {
        // Exercise reminder channel
        val exerciseChannel = NotificationChannel(
            EXERCISE_CHANNEL_ID,
            EXERCISE_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminders to complete your daily workout"
        }

        // Hydration reminder channel
        val hydrationChannel = NotificationChannel(
            HYDRATION_CHANNEL_ID,
            HYDRATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Reminders to drink water throughout the day"
        }

        // Register both channels
        notificationManager.createNotificationChannel(exerciseChannel)
        notificationManager.createNotificationChannel(hydrationChannel)
    }

    /**
     * Show exercise reminder notification.
     */
    fun showExerciseNotification() {
        val notification = NotificationCompat.Builder(context, EXERCISE_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("💪 Time to Exercise!")
            .setContentText("Your workout is scheduled for now. Let's go!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(EXERCISE_NOTIFICATION_ID, notification)
    }

    /**
     * Show hydration reminder notification.
     */
    fun showHydrationNotification() {
        val notification = NotificationCompat.Builder(context, HYDRATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("💧 Time to Hydrate!")
            .setContentText("Don't forget to drink a glass of water!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(HYDRATION_NOTIFICATION_ID, notification)
    }
}