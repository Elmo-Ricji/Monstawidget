package com.example.notesappwidget.ui

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.notesappwidget.R

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val detailTextView = findViewById<TextView>(R.id.detailTextView)
        val dataNotesTextView = findViewById<TextView>(R.id.Data_Notes)

        // Get the data passed from MainActivity
        val item = intent.getStringExtra("ITEM")

        // Set the data to the TextViews
        detailTextView.text = "Notes Content"
        dataNotesTextView.text = item
    }
}