package com.example.notesappwidget.ui

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.notesappwidget.R
import com.example.notesappwidget.data.AppDatabase
import com.example.notesappwidget.repository.NoteRepository
import com.example.notesappwidget.util.getFullDrawable
import com.example.notesappwidget.viewmodel.NoteEditorViewModel
import com.example.notesappwidget.viewmodel.NoteEditorViewModelFactory
import com.example.notesappwidget.widget.CreatureWidgetProvider

class DetailActivity : AppCompatActivity() {

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

        val titleView   = findViewById<TextView>(R.id.detailTitle)
        val bodyView    = findViewById<TextView>(R.id.detailBody)
        val thoughtCloud = findViewById<TextView>(R.id.detailThoughtCloud)
        val creatureView = findViewById<ImageView>(R.id.detailCreatureImage)
        val shareButton  = findViewById<Button>(R.id.shareButton)
        val editButton   = findViewById<Button>(R.id.editButton)
        val doneButton   = findViewById<Button>(R.id.doneButton)

        val noteId = intent.getIntExtra("NOTE_ID", -1)
        if (noteId != -1) viewModel.loadNote(noteId)

        viewModel.currentNote.observe(this) { note ->
            note?.let {
                titleView.text   = it.title ?: "Untitled"
                bodyView.text    = it.body ?: ""
                thoughtCloud.text = it.reminderPhrase ?: ""
                // static — no jumping in app
                creatureView.setImageResource(getFullDrawable(it.creatureId))

                shareButton.setOnClickListener { _ ->
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, it.title)
                        putExtra(Intent.EXTRA_TEXT, "${it.title}\n\n${it.body}")
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share note via"))
                }

                editButton.setOnClickListener { _ ->
                    startActivity(Intent(this, EditorNoteActivity::class.java).apply {
                        putExtra("NOTE_ID", it.id)
                    })
                }


                doneButton.setOnClickListener { _ ->
                    AlertDialog.Builder(this)
                        .setTitle("Mark as done?")
                        .setMessage("This will delete the note and free your Monsta!")
                        .setPositiveButton("Done! 🎉") { _, _ ->
                            viewModel.deleteCurrentNote()
                            refreshWidget()
                            finish()
                        }
                        .setNegativeButton("Not yet", null)
                        .show()
                }
            }
        }
    }

    private fun refreshWidget() {
        val manager = AppWidgetManager.getInstance(this)
        val ids = manager.getAppWidgetIds(
            ComponentName(this, CreatureWidgetProvider::class.java)
        )
        val intent = Intent(this, CreatureWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        sendBroadcast(intent)
    }
}
