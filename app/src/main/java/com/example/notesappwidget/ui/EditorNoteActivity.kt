package com.example.notesappwidget.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesappwidget.R
import com.example.notesappwidget.data.AppDatabase
import com.example.notesappwidget.repository.NoteRepository
import com.example.notesappwidget.ui.adapter.CreaturePickerAdapter
import com.example.notesappwidget.viewmodel.NoteEditorViewModel
import com.example.notesappwidget.viewmodel.NoteEditorViewModelFactory

class EditorNoteActivity : AppCompatActivity() {

    private lateinit var viewModel: NoteEditorViewModel
    private lateinit var creaturePickerAdapter: CreaturePickerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_note)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // setup viewmodel
        val dao = AppDatabase.getInstance(this).noteDao()
        val repository = NoteRepository(dao)
        val factory = NoteEditorViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[NoteEditorViewModel::class.java]

        // find views
        val titleEdit = findViewById<EditText>(R.id.editTitle)
        val bodyEdit = findViewById<EditText>(R.id.editBody)
        val reminderEdit = findViewById<EditText>(R.id.editReminderPhrase)
        val saveButton = findViewById<Button>(R.id.buttonSave)
        val creatureRecycler = findViewById<RecyclerView>(R.id.creaturePickerRecycler)

        // setup creature picker
        creaturePickerAdapter = CreaturePickerAdapter { selectedId ->
            viewModel.creatureId = selectedId
        }
        creatureRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        creatureRecycler.adapter = creaturePickerAdapter

        // if editing existing note, load it
        val noteId = intent.getIntExtra("NOTE_ID", -1)
        if (noteId != -1) {
            viewModel.loadNote(noteId)
        }

        // observe loaded note and fill fields
        viewModel.currentNote.observe(this) { note ->
            note?.let {
                titleEdit.setText(it.title)
                bodyEdit.setText(it.body)
                reminderEdit.setText(it.reminderPhrase)
                creaturePickerAdapter.setSelected(it.creatureId ?: 0)
            }
        }

        // restore draft state after rotation
        if (savedInstanceState == null && noteId == -1) {
            titleEdit.setText(viewModel.title)
            bodyEdit.setText(viewModel.body)
            reminderEdit.setText(viewModel.reminderPhrase)
        }

        saveButton.setOnClickListener {
            val title = titleEdit.text.toString().trim()
            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.title = title
            viewModel.body = bodyEdit.text.toString().trim()
            viewModel.reminderPhrase = reminderEdit.text.toString().trim()
            viewModel.saveNote()
            finish()
        }
    }

    // save draft to viewmodel on pause (lifecycle handling)
    override fun onPause() {
        super.onPause()
        viewModel.title = findViewById<EditText>(R.id.editTitle).text.toString()
        viewModel.body = findViewById<EditText>(R.id.editBody).text.toString()
        viewModel.reminderPhrase = findViewById<EditText>(R.id.editReminderPhrase).text.toString()
    }
}
