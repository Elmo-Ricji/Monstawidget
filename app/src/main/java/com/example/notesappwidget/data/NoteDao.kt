package com.example.notesappwidget.data

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