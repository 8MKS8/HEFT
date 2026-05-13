package com.heft.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * ExerciseWorker – background worker that triggers exercise reminder notifications.
 *
 * Uses WorkManager to schedule notifications at specific days and times.
 * Runs in the background even when the app is closed.
 */
class ExerciseWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    /**
     * doWork – called by WorkManager when the scheduled time arrives.
     * Shows the exercise reminder notification.
     */
    override suspend fun doWork(): Result {
        return try {
            // Show exercise notification
            val notificationHelper = NotificationHelper(context)
            notificationHelper.createNotificationChannels()
            notificationHelper.showExerciseNotification()

            // Return success
            Result.success()
        } catch (e: Exception) {
            // Return failure if something goes wrong
            Result.failure()
        }
    }
}