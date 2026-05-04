package com.example.notesappwidget.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesappwidget.R
import com.example.notesappwidget.data.AppDatabase
import com.example.notesappwidget.repository.NoteRepository
import com.example.notesappwidget.ui.adapter.NoteAdapter
import com.example.notesappwidget.viewmodel.HomeViewModel
import com.example.notesappwidget.viewmodel.HomeViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dao = AppDatabase.getInstance(this).noteDao()
        val repository = NoteRepository(dao)
        viewModel = ViewModelProvider(this, HomeViewModelFactory(repository))[HomeViewModel::class.java]

        noteAdapter = NoteAdapter(
            onNoteClick = { note ->
                startActivity(Intent(this, DetailActivity::class.java).apply {
                    putExtra("NOTE_ID", note.id)
                })
            },
            onNoteLongClick = { note ->
                AlertDialog.Builder(this)
                    .setTitle("Delete note?")
                    .setMessage("This cannot be undone")
                    .setPositiveButton("Delete") { _, _ -> viewModel.deleteNote(note) }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = noteAdapter

        viewModel.allNotes.observe(this) { notes ->
            noteAdapter.submitList(notes)
        }

        findViewById<FloatingActionButton>(R.id.add_button).setOnClickListener {
            startActivity(Intent(this, EditorNoteActivity::class.java))
        }
    }
}
