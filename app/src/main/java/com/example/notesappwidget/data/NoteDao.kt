package com.example.notesappwidget.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
@Dao
interface NoteDao {
    @Insert
    suspend fun insertNote(note: com.example.notesappwidget.data.Note): Long

    @Update
    suspend fun updateNote(note: com.example.notesappwidget.data.Note)

    @Delete
    suspend fun deleteNote(note: com.example.notesappwidget.data.Note)

    @Query("SELECT * FROM Note ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<com.example.notesappwidget.data.Note>>

    @Query("SELECT * FROM Note WHERE id = :id")
    suspend fun getNoteById(id: Int): com.example.notesappwidget.data.Note?

    @Query("SELECT * FROM Note ORDER BY updatedAt DESC")
    suspend fun getAllNotesOnce(): List<Note>
}