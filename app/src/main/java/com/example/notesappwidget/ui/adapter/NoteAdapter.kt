package com.example.notesappwidget.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notesappwidget.R
import com.example.notesappwidget.data.Note

class NoteAdapter(
    private val onNoteClick: (Note) -> Unit,
    private val onNoteLongClick: (Note) -> Unit
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(DiffCallback()) {

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thoughtCloud: TextView = itemView.findViewById(R.id.thoughtCloudText)
        val creatureImage: ImageView = itemView.findViewById(R.id.creatureImageView)
        val titleText: TextView = itemView.findViewById(R.id.noteTitleText)
        val updatedAt: TextView = itemView.findViewById(R.id.noteUpdatedAt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note_card, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.titleText.text = note.title ?: "Untitled"
        holder.thoughtCloud.text = note.reminderPhrase ?: ""
        holder.updatedAt.text = note.updatedAt?.let {
            android.text.format.DateUtils.getRelativeTimeSpanString(it).toString()
        } ?: ""

        val creatureRes = when (note.creatureId) {
            0 -> R.drawable.creature_0
            1 -> R.drawable.creature_1
            2 -> R.drawable.creature_2
            3 -> R.drawable.creature_3
            else -> R.drawable.creature_0
        }
        holder.creatureImage.setImageResource(creatureRes)

        holder.itemView.setOnClickListener { onNoteClick(note) }
        holder.itemView.setOnLongClickListener { onNoteLongClick(note); true }
    }

    class DiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
    }
}
