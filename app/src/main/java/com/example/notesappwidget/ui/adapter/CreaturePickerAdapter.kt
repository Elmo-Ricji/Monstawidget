package com.example.notesappwidget.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notesappwidget.R

class CreaturePickerAdapter(
    private val onCreatureSelected: (Int) -> Unit
) : RecyclerView.Adapter<CreaturePickerAdapter.ViewHolder>() {

    private val creatures = listOf(
        R.drawable.monsta_head_0, R.drawable.monsta_head_1,
        R.drawable.monsta_head_2, R.drawable.monsta_head_3,
        R.drawable.monsta_head_4, R.drawable.monsta_head_5,
        R.drawable.monsta_head_6, R.drawable.monsta_head_7,
        R.drawable.monsta_head_8, R.drawable.monsta_head_9
    )

    private var selectedPosition = 0
    private var usedIds: Set<Int> = emptySet()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.creatureOptionImage)
        val inUseLabel: TextView = itemView.findViewById(R.id.inUseLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_creature_picker, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isUsed = position in usedIds
        val isSelected = position == selectedPosition

        holder.image.setImageResource(creatures[position])
        holder.itemView.alpha = when {
            isSelected -> 1.0f
            isUsed -> 0.25f
            else -> 0.6f
        }
        holder.inUseLabel.visibility = if (isUsed) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            if (isUsed) return@setOnClickListener  // can't pick a used monsta
            val prev = selectedPosition
            selectedPosition = position
            notifyItemChanged(prev)
            notifyItemChanged(position)
            onCreatureSelected(position)
        }
    }

    override fun getItemCount() = creatures.size

    fun setSelected(id: Int) {
        if (id < 0 || id >= creatures.size) return
        val prev = selectedPosition
        selectedPosition = id
        notifyItemChanged(prev)
        notifyItemChanged(id)
    }

    fun setUsedIds(ids: Set<Int>) {
        usedIds = ids
        notifyDataSetChanged()
    }
}
