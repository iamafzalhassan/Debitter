package com.example.debitter.ui.recent

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.debitter.data.RecentLettersRepository
import com.example.debitter.data.RecentNotesRepository
import com.example.debitter.data.sources.NoteDatabase
import com.example.debitter.model.DebitNote
import com.example.debitter.model.RefundLetter
import com.example.debitter.model.SavedDocument
import com.example.debitter.model.SavedLetter
import com.example.debitter.model.SavedNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Immutable
data class RecentState(val isLoading: Boolean, val letters: List<SavedLetter>, val notes: List<SavedNote>)

class RecentViewModel(private val letters: RecentLettersRepository, private val notes: RecentNotesRepository) : ViewModel() {
    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val database = NoteDatabase(context.applicationContext)

                RecentViewModel(RecentLettersRepository(database), RecentNotesRepository(database))
            }
        }
    }

    private val mutableState: MutableStateFlow<RecentState> = MutableStateFlow(RecentState(isLoading = true, letters = emptyList(), notes = emptyList()))

    val state: StateFlow<RecentState> = mutableState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val loaded = withContext(Dispatchers.IO) { RecentState(isLoading = false, letters = letters.load(), notes = notes.load()) }

            mutableState.value = loaded
        }
    }

    fun delete(saved: SavedDocument) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (saved) {
                    is SavedLetter -> letters.delete(saved.id)
                    is SavedNote -> notes.delete(saved.id)
                }
            }
            refresh()
        }
    }

    fun save(letter: RefundLetter) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { letters.save(letter) }
            refresh()
        }
    }

    fun save(note: DebitNote) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { notes.save(note) }
            refresh()
        }
    }
}
