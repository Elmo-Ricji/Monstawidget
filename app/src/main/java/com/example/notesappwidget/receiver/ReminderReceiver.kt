package com.example.notesappwidget.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.notesappwidget.service.ReminderService
import com.example.notesappwidget.util.NotificationHelper
import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val noteId         = intent.getIntExtra("NOTE_ID", -1)
        val noteTitle      = intent.getStringExtra("NOTE_TITLE") ?: return
        val reminderPhrase = intent.getStringExtra("REMINDER_PHRASE") ?: ""
        val triggerTime    = intent.getLongExtra("TRIGGER_TIME", -1L)
        if (noteId == -1 || triggerTime == -1L) return


        NotificationHelper.postReminder(context, noteId, noteTitle, reminderPhrase)


        val nextTrigger = Calendar.getInstance().apply {
            timeInMillis = triggerTime
            add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis

        ReminderService.scheduleAlarm(context, noteId, noteTitle, reminderPhrase, nextTrigger)
    }
}