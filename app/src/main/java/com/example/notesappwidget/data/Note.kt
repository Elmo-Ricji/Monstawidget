package com.example.notesappwidget.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note (
    @PrimaryKey(autoGenerate = true) val id: Int=0,

   val title : String?,
   val body : String?,
   val reminderPhrase : String?,
   val creatureId : Int?,
   val reminderTime : Long?,
   val createdAt : Long?,
   val updatedAt : Long?,
)

