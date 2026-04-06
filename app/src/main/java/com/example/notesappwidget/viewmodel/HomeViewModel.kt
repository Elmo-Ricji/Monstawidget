package com.example.notesappwidget.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.notesappwidget.data.Note
import com.example.notesappwidget.repository.NoteRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: NoteRepository) : ViewModel() {
    val allNotes: LiveData<List<Note>> = repository.allNotes.asLiveData()

    fun deleteNote(note: Note) = viewModelScope.launch {
        repository.delete(note)
    }
}
