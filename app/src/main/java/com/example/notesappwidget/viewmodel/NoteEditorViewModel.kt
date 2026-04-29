package com.example.notesappwidget.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notesappwidget.data.Note
import com.example.notesappwidget.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteEditorViewModel(private val repository: NoteRepository) : ViewModel() {

    var title: String = ""
    var body: String = ""
    var reminderPhrase: String = ""
    var creatureId: Int = 0
    var reminderTime: Long? = null

    private val _currentNote = MutableLiveData<Note?>()
    val currentNote: LiveData<Note?> = _currentNote

    fun loadNote(id: Int) = viewModelScope.launch {
        val note = repository.getNoteById(id)
        note?.let {
            title = it.title ?: ""
            body = it.body ?: ""
            reminderPhrase = it.reminderPhrase ?: ""
            creatureId = it.creatureId ?: 0
            reminderTime = it.reminderTime
            _currentNote.postValue(it)
        }
    }

    fun saveNote() = viewModelScope.launch {
        val existing = _currentNote.value
        if (existing != null) {
            repository.update(existing.copy(
                title = title, body = body,
                reminderPhrase = reminderPhrase,
                creatureId = creatureId,
                reminderTime = reminderTime,
                updatedAt = System.currentTimeMillis()
            ))
        } else {
            repository.insert(Note(
                title = title, body = body,
                reminderPhrase = reminderPhrase,
                creatureId = creatureId,
                reminderTime = reminderTime,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ))
        }
    }

    fun deleteCurrentNote() = viewModelScope.launch {
        _currentNote.value?.let { repository.delete(it) }
    }
}

class NoteEditorViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteEditorViewModel(repository) as T
    }
}
