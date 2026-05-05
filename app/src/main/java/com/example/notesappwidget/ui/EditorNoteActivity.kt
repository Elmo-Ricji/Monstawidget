package com.example.notesappwidget.ui

import android.app.AlarmManager
import android.app.TimePickerDialog
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesappwidget.R
import com.example.notesappwidget.data.AppDatabase
import com.example.notesappwidget.repository.NoteRepository
import com.example.notesappwidget.service.ReminderService
import com.example.notesappwidget.ui.adapter.CreaturePickerAdapter
import com.example.notesappwidget.util.NotificationHelper
import com.example.notesappwidget.viewmodel.HomeViewModel
import com.example.notesappwidget.viewmodel.HomeViewModelFactory
import com.example.notesappwidget.viewmodel.NoteEditorViewModel
import com.example.notesappwidget.viewmodel.NoteEditorViewModelFactory
import com.example.notesappwidget.widget.CreatureWidgetProvider
import kotlinx.coroutines.launch
import java.util.Calendar

class EditorNoteActivity : AppCompatActivity() {

    private lateinit var viewModel: NoteEditorViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var creaturePickerAdapter: CreaturePickerAdapter

    private lateinit var titleEdit: EditText
    private lateinit var bodyEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editor_note)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        NotificationHelper.createChannel(this)

        val dao = AppDatabase.getInstance(this).noteDao()
        val repository = NoteRepository(dao)

        viewModel = ViewModelProvider(this, NoteEditorViewModelFactory(repository))[NoteEditorViewModel::class.java]
        homeViewModel = ViewModelProvider(this, HomeViewModelFactory(repository))[HomeViewModel::class.java]

        titleEdit = findViewById(R.id.editTitle)
        bodyEdit  = findViewById(R.id.editBody)
        val saveButton       = findViewById<Button>(R.id.buttonSave)
        val timeButton       = findViewById<Button>(R.id.buttonSetTime)
        val timeLabel        = findViewById<TextView>(R.id.textReminderTime)
        val creatureRecycler = findViewById<RecyclerView>(R.id.creaturePickerRecycler)

        creaturePickerAdapter = CreaturePickerAdapter { selectedId ->
            viewModel.creatureId = selectedId
        }
        creatureRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        creatureRecycler.adapter = creaturePickerAdapter

        val noteId = intent.getIntExtra("NOTE_ID", -1)

        homeViewModel.allNotes.observe(this) { notes ->
            val usedIds = notes
                .filter { it.creatureId != null && it.id != noteId }
                .map { it.creatureId!! }
                .toSet()
            creaturePickerAdapter.setUsedIds(usedIds)
        }

        if (noteId != -1) viewModel.loadNote(noteId)

        viewModel.currentNote.observe(this) { note ->
            note?.let {
                titleEdit.setText(it.title)
                bodyEdit.setText(it.body)
                creaturePickerAdapter.setSelected(it.creatureId ?: 0)
                it.reminderTime?.let { t ->
                    val cal = Calendar.getInstance().apply { timeInMillis = t }
                    timeLabel.text = "%02d:%02d".format(
                        cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
                }
            }
        }

        timeButton.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(this, { _, hour, minute ->
                val trigger = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    if (timeInMillis < System.currentTimeMillis())
                        add(Calendar.DAY_OF_YEAR, 1)
                }
                viewModel.reminderTime = trigger.timeInMillis
                timeLabel.text = "%02d:%02d".format(hour, minute)
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        saveButton.setOnClickListener {
            val title = titleEdit.text.toString().trim()
            if (title.isEmpty()) {
                android.widget.Toast.makeText(this, "Please enter a title", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.title = title
            viewModel.body = bodyEdit.text.toString().trim()
            viewModel.reminderPhrase = title  // title IS the reminder phrase

            lifecycleScope.launch {
                val savedId = viewModel.saveNote()

                viewModel.reminderTime?.let { triggerTime ->
                    val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                    if (!alarmManager.canScheduleExactAlarms()) {
                        startActivity(
                            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                data = Uri.parse("package:$packageName")
                            }
                        )
                        android.widget.Toast.makeText(
                            this@EditorNoteActivity,
                            "Please allow exact alarms so your reminder fires on time",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    }
                    startService(Intent(this@EditorNoteActivity, ReminderService::class.java).apply {
                        putExtra("NOTE_ID", savedId)
                        putExtra("NOTE_TITLE", title)
                        putExtra("REMINDER_PHRASE", title)  // title IS the reminder phrase
                        putExtra("TRIGGER_TIME", triggerTime)
                    })
                }

                refreshWidget()
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            if (hasUnsavedChanges()) {
                AlertDialog.Builder(this@EditorNoteActivity)
                    .setTitle("Discard changes?")
                    .setMessage("You have unsaved changes. Leave without saving?")
                    .setPositiveButton("Discard") { _, _ -> finish() }
                    .setNegativeButton("Keep editing", null)
                    .show()
            } else {
                finish()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.title = titleEdit.text.toString()
        viewModel.body = bodyEdit.text.toString()
        viewModel.reminderPhrase = titleEdit.text.toString()  // keep in sync
    }

    private fun hasUnsavedChanges(): Boolean {
        val note = viewModel.currentNote.value
        val currentTitle = titleEdit.text.toString().trim()
        val currentBody = bodyEdit.text.toString().trim()

        return if (note == null) {
            currentTitle.isNotEmpty() || currentBody.isNotEmpty()
        } else {
            currentTitle != (note.title ?: "") ||
                    currentBody  != (note.body ?: "")
        }
    }

    private fun refreshWidget() {
        val manager = AppWidgetManager.getInstance(this)
        val ids = manager.getAppWidgetIds(ComponentName(this, CreatureWidgetProvider::class.java))
        sendBroadcast(Intent(this, CreatureWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        })
    }
}