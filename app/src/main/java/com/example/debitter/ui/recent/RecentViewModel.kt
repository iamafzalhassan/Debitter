package com.example.debitter.ui.recent

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.debitter.data.RecentNotesRepository
import com.example.debitter.data.sources.NoteDatabase
import com.example.debitter.model.DebitNote
import com.example.debitter.model.SavedNote
import com.example.debitter.model.ShipmentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Immutable
data class RecentState(val isLoading: Boolean, val notes: List<SavedNote>)

class RecentViewModel(private val repository: RecentNotesRepository) : ViewModel() {
    companion object {
        const val RETENTION_DAYS: Long = RecentNotesRepository.RETENTION_DAYS

        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer { RecentViewModel(RecentNotesRepository(NoteDatabase(context.applicationContext))) }
        }
    }

    private val mutableState: MutableStateFlow<RecentState> = MutableStateFlow(RecentState(isLoading = true, notes = emptyList()))

    val state: StateFlow<RecentState> = mutableState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val notes = withContext(Dispatchers.IO) { repository.load() }

            mutableState.value = RecentState(isLoading = false, notes = notes)
        }
    }

    fun save(note: DebitNote, shipmentType: ShipmentType) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { repository.save(note, shipmentType) }
            refresh()
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { repository.delete(id) }
            refresh()
        }
    }
}
