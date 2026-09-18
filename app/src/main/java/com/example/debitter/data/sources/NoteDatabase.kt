package com.example.debitter.data.sources

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import com.example.debitter.data.LetterPresets
import java.util.UUID

data class AgentRow(val address: String, val id: String, val name: String)

data class CustomerRow(val addressLine: String, val contactLine: String, val id: String, val name: String, val tagline: String)

data class LetterRow(val createdAt: Long, val agent: String, val customer: String, val id: String, val payload: String)

data class NoteRow(val createdAt: Long, val billTo: String, val id: String, val payload: String, val total: String)

class NoteDatabase(context: Context) : SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, SCHEMA_VERSION) {
    companion object {
        const val DIRECTORY_VERSION: Int = 3
        const val LETTERS_VERSION: Int = 2
        const val SCHEMA_VERSION: Int = 3

        const val COLUMN_ADDRESS: String = "address"
        const val COLUMN_ADDRESS_LINE: String = "address_line"
        const val COLUMN_AGENT: String = "agent"
        const val COLUMN_BILL_TO: String = "bill_to"
        const val COLUMN_CONTACT_LINE: String = "contact_line"
        const val COLUMN_CREATED_AT: String = "created_at"
        const val COLUMN_CUSTOMER: String = "customer"
        const val COLUMN_ID: String = "id"
        const val COLUMN_NAME: String = "name"
        const val COLUMN_PAYLOAD: String = "payload"
        const val COLUMN_TAGLINE: String = "tagline"
        const val COLUMN_TOTAL: String = "total"
        const val DATABASE_NAME: String = "debitter.db"
        const val TABLE_AGENTS: String = "agents"
        const val TABLE_CUSTOMERS: String = "customers"
        const val TABLE_LETTERS: String = "recent_letters"
        const val TABLE_NOTES: String = "recent_notes"
    }

    fun delete(id: String) = deleteRow(TABLE_NOTES, id)

    fun deleteAgent(id: String) = deleteRow(TABLE_AGENTS, id)

    fun deleteCustomer(id: String) = deleteRow(TABLE_CUSTOMERS, id)

    fun deleteLetter(id: String) = deleteRow(TABLE_LETTERS, id)

    fun readAll(): List<NoteRow> = readRows(TABLE_NOTES, "$COLUMN_CREATED_AT DESC") { toRow() }

    fun readAllAgents(): List<AgentRow> = readRows(TABLE_AGENTS, "$COLUMN_NAME COLLATE NOCASE ASC") { toAgentRow() }

    fun readAllCustomers(): List<CustomerRow> = readRows(TABLE_CUSTOMERS, "$COLUMN_NAME COLLATE NOCASE ASC") { toCustomerRow() }

    fun readAllLetters(): List<LetterRow> = readRows(TABLE_LETTERS, "$COLUMN_CREATED_AT DESC") { toLetterRow() }

    fun upsert(row: NoteRow) = writableDatabase.upsertRow(TABLE_NOTES, COLUMN_CREATED_AT to row.createdAt, COLUMN_BILL_TO to row.billTo, COLUMN_ID to row.id, COLUMN_PAYLOAD to row.payload, COLUMN_TOTAL to row.total)

    fun upsertAgent(row: AgentRow) = insertAgent(writableDatabase, row)

    fun upsertCustomer(row: CustomerRow) = insertCustomer(writableDatabase, row)

    fun upsertLetter(row: LetterRow) = writableDatabase.upsertRow(TABLE_LETTERS, COLUMN_CREATED_AT to row.createdAt, COLUMN_AGENT to row.agent, COLUMN_CUSTOMER to row.customer, COLUMN_ID to row.id, COLUMN_PAYLOAD to row.payload)

    private fun deleteRow(table: String, id: String) {
        writableDatabase.delete(table, "$COLUMN_ID = ?", arrayOf(id))
    }

    private fun <T> readRows(table: String, orderBy: String, read: Cursor.() -> T): List<T> = readableDatabase.query(table, null, null, null, null, null, orderBy).use { cursor ->
        buildList { while (cursor.moveToNext()) add(cursor.read()) }
    }

    private fun Cursor.toRow(): NoteRow = NoteRow(
        createdAt = getLong(getColumnIndexOrThrow(COLUMN_CREATED_AT)),
        billTo = getString(getColumnIndexOrThrow(COLUMN_BILL_TO)),
        id = getString(getColumnIndexOrThrow(COLUMN_ID)),
        payload = getString(getColumnIndexOrThrow(COLUMN_PAYLOAD)),
        total = getString(getColumnIndexOrThrow(COLUMN_TOTAL)),
    )

    private fun Cursor.toAgentRow(): AgentRow = AgentRow(
        address = getString(getColumnIndexOrThrow(COLUMN_ADDRESS)),
        id = getString(getColumnIndexOrThrow(COLUMN_ID)),
        name = getString(getColumnIndexOrThrow(COLUMN_NAME)),
    )

    private fun Cursor.toCustomerRow(): CustomerRow = CustomerRow(
        addressLine = getString(getColumnIndexOrThrow(COLUMN_ADDRESS_LINE)),
        contactLine = getString(getColumnIndexOrThrow(COLUMN_CONTACT_LINE)),
        id = getString(getColumnIndexOrThrow(COLUMN_ID)),
        name = getString(getColumnIndexOrThrow(COLUMN_NAME)),
        tagline = getString(getColumnIndexOrThrow(COLUMN_TAGLINE)),
    )

    private fun Cursor.toLetterRow(): LetterRow = LetterRow(
        createdAt = getLong(getColumnIndexOrThrow(COLUMN_CREATED_AT)),
        agent = getString(getColumnIndexOrThrow(COLUMN_AGENT)),
        customer = getString(getColumnIndexOrThrow(COLUMN_CUSTOMER)),
        id = getString(getColumnIndexOrThrow(COLUMN_ID)),
        payload = getString(getColumnIndexOrThrow(COLUMN_PAYLOAD)),
    )

    private fun createNotes(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_NOTES (" +
                "$COLUMN_ID TEXT PRIMARY KEY, " +
                "$COLUMN_BILL_TO TEXT NOT NULL, " +
                "$COLUMN_TOTAL TEXT NOT NULL, " +
                "$COLUMN_CREATED_AT INTEGER NOT NULL, " +
                "$COLUMN_PAYLOAD TEXT NOT NULL)",
        )
    }

    private fun createLetters(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_LETTERS (" +
                "$COLUMN_ID TEXT PRIMARY KEY, " +
                "$COLUMN_AGENT TEXT NOT NULL, " +
                "$COLUMN_CUSTOMER TEXT NOT NULL, " +
                "$COLUMN_CREATED_AT INTEGER NOT NULL, " +
                "$COLUMN_PAYLOAD TEXT NOT NULL)",
        )
    }

    private fun createCustomers(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_CUSTOMERS (" +
                "$COLUMN_ID TEXT PRIMARY KEY, " +
                "$COLUMN_NAME TEXT NOT NULL, " +
                "$COLUMN_TAGLINE TEXT NOT NULL, " +
                "$COLUMN_ADDRESS_LINE TEXT NOT NULL, " +
                "$COLUMN_CONTACT_LINE TEXT NOT NULL)",
        )
        for (letterhead in LetterPresets.letterheads) {
            insertCustomer(
                db,
                CustomerRow(
                    addressLine = letterhead.addressLine,
                    contactLine = letterhead.contactLine,
                    id = UUID.randomUUID().toString(),
                    name = letterhead.name,
                    tagline = letterhead.tagline,
                ),
            )
        }
    }

    private fun insertCustomer(db: SQLiteDatabase, row: CustomerRow) = db.upsertRow(TABLE_CUSTOMERS, COLUMN_ADDRESS_LINE to row.addressLine, COLUMN_CONTACT_LINE to row.contactLine, COLUMN_ID to row.id, COLUMN_NAME to row.name, COLUMN_TAGLINE to row.tagline)

    private fun createAgents(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_AGENTS (" +
                "$COLUMN_ID TEXT PRIMARY KEY, " +
                "$COLUMN_NAME TEXT NOT NULL, " +
                "$COLUMN_ADDRESS TEXT NOT NULL)",
        )
        for (agent in LetterPresets.agents) {
            insertAgent(db, AgentRow(address = agent.address, id = UUID.randomUUID().toString(), name = agent.name))
        }
    }

    private fun insertAgent(db: SQLiteDatabase, row: AgentRow) = db.upsertRow(TABLE_AGENTS, COLUMN_ADDRESS to row.address, COLUMN_ID to row.id, COLUMN_NAME to row.name)

    private fun SQLiteDatabase.upsertRow(table: String, vararg values: Pair<String, Any?>) {
        insertWithOnConflict(table, null, contentValuesOf(*values), SQLiteDatabase.CONFLICT_REPLACE)
    }

    override fun onCreate(db: SQLiteDatabase) {
        createNotes(db)
        createLetters(db)
        createCustomers(db)
        createAgents(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_AGENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CUSTOMERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LETTERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < LETTERS_VERSION) createLetters(db)
        if (oldVersion < DIRECTORY_VERSION) {
            createCustomers(db)
            createAgents(db)
        }
    }
}
