package com.example.notesappwidget.data
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

