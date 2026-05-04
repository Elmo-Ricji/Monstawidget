package com.example.notesappwidget.service

import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.notesappwidget.receiver.ReminderReceiver

class ReminderService : IntentService("ReminderService") {

    override fun onHandleIntent(intent: Intent?) {
        val noteId       = intent?.getIntExtra("NOTE_ID", -1) ?: return
        val noteTitle    = intent.getStringExtra("NOTE_TITLE") ?: return
        val reminderPhrase = intent.getStringExtra("REMINDER_PHRASE") ?: ""
        val triggerTime  = intent.getLongExtra("TRIGGER_TIME", -1L)
        if (noteId == -1 || triggerTime == -1L) return

        scheduleAlarm(this, noteId, noteTitle, reminderPhrase, triggerTime)
    }

    companion object {
        fun scheduleAlarm(
            context: Context,
            noteId: Int,
            noteTitle: String,
            reminderPhrase: String,
            triggerTime: Long
        ) {
            val alarmIntent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("NOTE_ID", noteId)
                putExtra("NOTE_TITLE", noteTitle)
                putExtra("REMINDER_PHRASE", reminderPhrase)
                putExtra("TRIGGER_TIME", triggerTime)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                noteId,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (alarmManager.canScheduleExactAlarms()) {
                try {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } catch (e: SecurityException) {

                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            } else {

                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        }
    }
}