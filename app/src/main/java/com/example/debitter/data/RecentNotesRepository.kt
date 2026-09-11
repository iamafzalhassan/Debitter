package com.example.debitter.data

import com.example.debitter.data.sources.NoteDatabase
import com.example.debitter.data.sources.NoteRow
import com.example.debitter.model.DebitNote
import com.example.debitter.model.SavedNote
import java.math.BigDecimal
import java.util.UUID
import java.util.concurrent.TimeUnit

class RecentNotesRepository(private val database: NoteDatabase) {
    companion object {
        const val RETENTION_DAYS: Long = 90
    }

    fun delete(id: String) = database.delete(id)

    fun load(): List<SavedNote> {
        purge()
        return database.readAll().mapNotNull { row ->
            val note = NoteJson.decode(row.payload) ?: return@mapNotNull null

            SavedNote(
                createdAt = row.createdAt,
                billTo = row.billTo,
                id = row.id,
                total = row.total.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                note = note,
            )
        }
    }

    fun save(note: DebitNote) = database.upsert(
        NoteRow(
            createdAt = System.currentTimeMillis(),
            billTo = note.header.billTo,
            id = UUID.randomUUID().toString(),
            payload = NoteJson.encode(note),
            total = note.total.toPlainString(),
        ),
    )

    private fun purge() = database.purgeOlderThan(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(RETENTION_DAYS))
}
