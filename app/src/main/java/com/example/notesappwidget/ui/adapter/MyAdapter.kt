package com.example.notesappwidget.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.notesappwidget.R
import com.example.notesappwidget.ui.DetailActivity

class MyAdapter(private val context: Context, private val values: MutableList<String>) :
    ArrayAdapter<String?>(context, R.layout.list_item, values as List<String?>) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = inflater.inflate(R.layout.list_item, parent, false)

        val textView = rowView.findViewById<TextView>(R.id.textView)
        val openButton = rowView.findViewById<Button>(R.id.button)
        val deleteButton = rowView.findViewById<Button>(R.id.delete_button)

        textView.text = values[position]

        openButton.setOnClickListener { // Start DetailActivity and pass the item data
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("ITEM", values[position])
            context.startActivity(intent)
        }

        deleteButton.setOnClickListener {
            Toast.makeText(
                context,
                "Delete button clicked for item " + values[position],
                Toast.LENGTH_SHORT
            ).show()
            // Remove the item from the list
            values.removeAt(position)
            // Notify the adapter to refresh the list view
            notifyDataSetChanged()
        }

        return rowView
    }
}
