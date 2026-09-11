package com.example.debitter.data.sources

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class NoteRow(val createdAt: Long, val billTo: String, val id: String, val payload: String, val total: String)

class NoteDatabase(context: Context) : SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, SCHEMA_VERSION) {
    companion object {
        const val SCHEMA_VERSION: Int = 1

        const val COLUMN_BILL_TO: String = "bill_to"
        const val COLUMN_CREATED_AT: String = "created_at"
        const val COLUMN_ID: String = "id"
        const val COLUMN_PAYLOAD: String = "payload"
        const val COLUMN_TOTAL: String = "total"
        const val DATABASE_NAME: String = "debitter.db"
        const val TABLE_RECENT: String = "recent_notes"
    }

    fun delete(id: String) {
        writableDatabase.delete(TABLE_RECENT, "$COLUMN_ID = ?", arrayOf(id))
    }

    fun readAll(): List<NoteRow> {
        val rows = mutableListOf<NoteRow>()

        readableDatabase.query(TABLE_RECENT, null, null, null, null, null, "$COLUMN_CREATED_AT DESC").use { cursor ->
            while (cursor.moveToNext()) rows += cursor.toRow()
        }
        return rows
    }

    fun upsert(row: NoteRow) {
        val values = ContentValues().apply {
            put(COLUMN_CREATED_AT, row.createdAt)
            put(COLUMN_BILL_TO, row.billTo)
            put(COLUMN_ID, row.id)
            put(COLUMN_PAYLOAD, row.payload)
            put(COLUMN_TOTAL, row.total)
        }

        writableDatabase.insertWithOnConflict(TABLE_RECENT, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    private fun Cursor.toRow(): NoteRow = NoteRow(
        createdAt = getLong(getColumnIndexOrThrow(COLUMN_CREATED_AT)),
        billTo = getString(getColumnIndexOrThrow(COLUMN_BILL_TO)),
        id = getString(getColumnIndexOrThrow(COLUMN_ID)),
        payload = getString(getColumnIndexOrThrow(COLUMN_PAYLOAD)),
        total = getString(getColumnIndexOrThrow(COLUMN_TOTAL)),
    )

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_RECENT (" +
                "$COLUMN_ID TEXT PRIMARY KEY, " +
                "$COLUMN_BILL_TO TEXT NOT NULL, " +
                "$COLUMN_TOTAL TEXT NOT NULL, " +
                "$COLUMN_CREATED_AT INTEGER NOT NULL, " +
                "$COLUMN_PAYLOAD TEXT NOT NULL)",
        )
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = onUpgrade(db, oldVersion, newVersion)

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RECENT")
        onCreate(db)
    }
}
