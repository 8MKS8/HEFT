package com.heft.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * HydrationWorker – background worker that triggers hydration reminder notifications.
 *
 * Uses WorkManager to send periodic reminders to drink water.
 * Interval is set by the user in the Notifications screen.
 * Runs in the background even when the app is closed.
 */
class HydrationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    /**
     * doWork – called by WorkManager at each interval.
     * Shows the hydration reminder notification.
     */
    override suspend fun doWork(): Result {
        return try {
            // Show hydration notification
            val notificationHelper = NotificationHelper(context)
            notificationHelper.createNotificationChannels()
            notificationHelper.showHydrationNotification()

            // Return success
            Result.success()
        } catch (e: Exception) {
            // Return failure if something goes wrong
            Result.failure()
        }
    }
}