package com.example.notesappwidget.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.notesappwidget.R
import com.example.notesappwidget.ui.DetailActivity

object NotificationHelper {

    private const val CHANNEL_ID = "monsta_reminders"
    private const val CHANNEL_NAME = "Monsta Reminders"
    private const val CHANNEL_DESC = "Daily reminders from your Monstas"

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHANNEL_DESC
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun postReminder(context: Context, noteId: Int, noteTitle: String, reminderPhrase: String) {
        val tapIntent = Intent(context, DetailActivity::class.java).apply {
            putExtra("NOTE_ID", noteId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            noteId,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(noteTitle)
            .setContentText(reminderPhrase.ifBlank { "We neeed yaaaa boooi" })
            .setStyle(NotificationCompat.BigTextStyle().bigText(reminderPhrase))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(noteId, notification)
        }
    }
}