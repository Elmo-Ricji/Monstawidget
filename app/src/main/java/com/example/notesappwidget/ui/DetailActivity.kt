package com.example.notesappwidget.ui

import com.example.notesappwidget.viewmodel.NoteEditorViewModel
import com.example.notesappwidget.viewmodel.NoteEditorViewModelFactory
import com.example.notesappwidget.util.startJumping
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.notesappwidget.R
import com.example.notesappwidget.data.AppDatabase
import com.example.notesappwidget.repository.NoteRepository


class DetailActivity : AppCompatActivity()
{

    private lateinit var viewModel: NoteEditorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dao = AppDatabase.getInstance(this).noteDao()
        val repository = NoteRepository(dao)
        val factory = NoteEditorViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[NoteEditorViewModel::class.java]

        val titleView = findViewById<TextView>(R.id.detailTitle)
        val bodyView = findViewById<TextView>(R.id.detailBody)
        val thoughtCloud = findViewById<TextView>(R.id.detailThoughtCloud)
        val creatureView = findViewById<ImageView>(R.id.detailCreatureImage)
        val shareButton = findViewById<Button>(R.id.shareButton)
        val editButton = findViewById<Button>(R.id.editButton)

        val noteId = intent.getIntExtra("NOTE_ID", -1)
        if (noteId != -1) {
            viewModel.loadNote(noteId)
        }

        viewModel.currentNote.observe(this) { note ->
            note?.let {
                titleView.text = it.title ?: "Untitled"
                bodyView.text = it.body ?: ""
                thoughtCloud.text = it.reminderPhrase ?: ""

                val creatureRes = when (it.creatureId) {
                    0 -> R.drawable.creature_0
                    1 -> R.drawable.creature_1
                    2 -> R.drawable.creature_2
                    3 -> R.drawable.creature_3
                    else -> R.drawable.creature_0
                }
                creatureView.setImageResource(creatureRes)
                creatureView.startJumping()

                // share button
                shareButton.setOnClickListener { _ ->
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, it.title)
                        putExtra(Intent.EXTRA_TEXT, "${it.title}\n\n${it.body}")
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share note via"))
                }

                // edit button
                editButton.setOnClickListener { _ ->
                    val intent = Intent(this, EditorNoteActivity::class.java)
                    intent.putExtra("NOTE_ID", it.id)
                    startActivity(intent)
                }
            }
        }
    }
}
