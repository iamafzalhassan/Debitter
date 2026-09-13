package com.example.debitter.ui.settings

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.debitter.data.Defaults
import com.example.debitter.data.DirectoryRepository
import com.example.debitter.data.sources.NoteDatabase
import com.example.debitter.model.AgentEntry
import com.example.debitter.model.CustomerEntry
import com.example.debitter.model.Letterhead
import com.example.debitter.model.ShippingAgent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

@Immutable
data class SettingsState(val isLoading: Boolean, val agents: List<AgentEntry>, val customers: List<CustomerEntry>, val letterheads: List<Letterhead>, val noteCustomers: List<Letterhead>, val shippingAgents: List<ShippingAgent>)

class SettingsViewModel(private val directory: DirectoryRepository) : ViewModel() {
    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer { SettingsViewModel(DirectoryRepository(NoteDatabase(context.applicationContext))) }
        }
    }

    private val mutableState: MutableStateFlow<SettingsState> = MutableStateFlow(SettingsState(isLoading = true, agents = emptyList(), customers = emptyList(), letterheads = emptyList(), noteCustomers = emptyList(), shippingAgents = emptyList()))

    val state: StateFlow<SettingsState> = mutableState.asStateFlow()

    init {
        refresh()
    }

    fun deleteAgent(entry: AgentEntry) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { directory.deleteAgent(entry.id) }
            refresh()
        }
    }

    fun deleteCustomer(entry: CustomerEntry) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { directory.deleteCustomer(entry.id) }
            refresh()
        }
    }

    fun saveAgent(id: String?, agent: ShippingAgent) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { directory.saveAgent(AgentEntry(id = id ?: UUID.randomUUID().toString(), agent = tidy(agent))) }
            refresh()
        }
    }

    fun saveCustomer(id: String?, letterhead: Letterhead) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { directory.saveCustomer(CustomerEntry(id = id ?: UUID.randomUUID().toString(), letterhead = tidy(letterhead))) }
            refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            val (agents, customers) = withContext(Dispatchers.IO) { directory.loadAgents() to directory.loadCustomers() }
            val letterheads = customers.map { it.letterhead }

            mutableState.value = SettingsState(
                isLoading = false,
                agents = agents,
                customers = customers,
                letterheads = letterheads,
                noteCustomers = letterheads.filter { it.name != Defaults.company.name },
                shippingAgents = agents.map { it.agent },
            )
        }
    }

    private fun tidy(agent: ShippingAgent): ShippingAgent = ShippingAgent(
        address = agent.address.lines().map { it.trim() }.filter { it.isNotEmpty() }.joinToString("\n"),
        name = agent.name.trim(),
    )

    private fun tidy(letterhead: Letterhead): Letterhead = Letterhead(
        addressLine = letterhead.addressLine.trim(),
        contactLine = letterhead.contactLine.trim(),
        name = letterhead.name.trim(),
        tagline = letterhead.tagline.trim(),
    )
}
