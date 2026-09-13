package com.example.debitter.data

import com.example.debitter.data.sources.AgentRow
import com.example.debitter.data.sources.CustomerRow
import com.example.debitter.data.sources.NoteDatabase
import com.example.debitter.model.AgentEntry
import com.example.debitter.model.CustomerEntry
import com.example.debitter.model.Letterhead
import com.example.debitter.model.ShippingAgent

class DirectoryRepository(private val database: NoteDatabase) {
    fun deleteAgent(id: String) = database.deleteAgent(id)

    fun deleteCustomer(id: String) = database.deleteCustomer(id)

    fun loadAgents(): List<AgentEntry> = database.readAllAgents().map { row ->
        AgentEntry(id = row.id, agent = ShippingAgent(address = row.address, name = row.name))
    }

    fun loadCustomers(): List<CustomerEntry> = database.readAllCustomers().map { row ->
        CustomerEntry(
            id = row.id,
            letterhead = Letterhead(addressLine = row.addressLine, contactLine = row.contactLine, name = row.name, tagline = row.tagline),
        )
    }

    fun saveAgent(entry: AgentEntry) = database.upsertAgent(AgentRow(address = entry.agent.address, id = entry.id, name = entry.agent.name))

    fun saveCustomer(entry: CustomerEntry) = database.upsertCustomer(
        CustomerRow(
            addressLine = entry.letterhead.addressLine,
            contactLine = entry.letterhead.contactLine,
            id = entry.id,
            name = entry.letterhead.name,
            tagline = entry.letterhead.tagline,
        ),
    )
}
