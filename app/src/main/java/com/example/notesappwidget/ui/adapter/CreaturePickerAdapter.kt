package com.example.notesappwidget.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.notesappwidget.R

class CreaturePickerAdapter(
    private val onCreatureSelected: (Int) -> Unit
) : RecyclerView.Adapter<CreaturePickerAdapter.ViewHolder>() {

    private val creatures = listOf(
        R.drawable.creature_0,
        R.drawable.creature_1,
        R.drawable.creature_2,
        R.drawable.creature_3
    )

    private var selectedPosition = 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.creatureOptionImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_creature_picker, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.setImageResource(creatures[position])
        holder.itemView.alpha = if (position == selectedPosition) 1.0f else 0.4f
        holder.itemView.setOnClickListener {
            val prev = selectedPosition
            selectedPosition = position
            notifyItemChanged(prev)
            notifyItemChanged(position)
            onCreatureSelected(position)
        }
    }

    override fun getItemCount() = creatures.size

    fun setSelected(id: Int) {
        val prev = selectedPosition
        selectedPosition = id
        notifyItemChanged(prev)
        notifyItemChanged(id)
    }
}
