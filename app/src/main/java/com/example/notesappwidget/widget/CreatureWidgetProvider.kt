package com.example.notesappwidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.example.notesappwidget.R
import com.example.notesappwidget.data.AppDatabase
import com.example.notesappwidget.data.Note
import com.example.notesappwidget.ui.DetailActivity
import com.example.notesappwidget.util.getDayBackground
import com.example.notesappwidget.util.getFullDrawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreatureWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (widgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId)
        }
    }

    companion object {

        // slot config: layout IDs for each Monsta slot
        data class Slot(
            val slotLayout: Int,
            val imageView: Int,
            val cloudView: Int
        )

        val SLOTS = listOf(
            Slot(R.id.monstaSlot0, R.id.monsta0, R.id.cloud0),
            Slot(R.id.monstaSlot1, R.id.monsta1, R.id.cloud1),
            Slot(R.id.monstaSlot2, R.id.monsta2, R.id.cloud2),
            Slot(R.id.monstaSlot3, R.id.monsta3, R.id.cloud3),
            Slot(R.id.monstaSlot4, R.id.monsta4, R.id.cloud4),
        )

        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getInstance(context)
                val notes = db.noteDao().getAllNotesOnce()
                val activeNotes = notes.filter { it.creatureId != null }.take(5)

                withContext(Dispatchers.Main) {
                    val views = RemoteViews(context.packageName, R.layout.monsta_app_widget)

                    // set day background
                    views.setImageViewResource(R.id.widgetBackground, getDayBackground())

                    // hide all slots first
                    for (slot in SLOTS) {
                        views.setViewVisibility(slot.slotLayout, View.INVISIBLE)
                    }

                    // fill slots with active notes
                    activeNotes.forEachIndexed { index, note ->
                        if (index >= SLOTS.size) return@forEachIndexed
                        val slot = SLOTS[index]

                        views.setViewVisibility(slot.slotLayout, View.VISIBLE)
                        views.setImageViewResource(slot.imageView, getFullDrawable(note.creatureId))
                        views.setTextViewText(slot.cloudView, note.reminderPhrase ?: note.title ?: "")

                        // tap monsta → open DetailActivity for that note
                        val intent = Intent(context, DetailActivity::class.java).apply {
                            putExtra("NOTE_ID", note.id)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                        val pendingIntent = PendingIntent.getActivity(
                            context, note.id, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        views.setOnClickPendingIntent(slot.slotLayout, pendingIntent)
                    }

                    appWidgetManager.updateAppWidget(widgetId, views)
                }
            }
        }
    }
}
