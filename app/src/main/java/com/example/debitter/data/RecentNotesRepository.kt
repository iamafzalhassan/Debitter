package com.example.debitter.data

import com.example.debitter.data.sources.NoteDatabase
import com.example.debitter.data.sources.NoteRow
import com.example.debitter.model.DebitNote
import com.example.debitter.model.SavedNote
import com.example.debitter.model.ShipmentType
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
            val decoded = NoteJson.decode(row.payload) ?: return@mapNotNull null

            SavedNote(
                createdAt = row.createdAt,
                billTo = row.billTo,
                id = row.id,
                total = row.total.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                note = decoded.note,
                shipmentType = decoded.shipmentType,
            )
        }
    }

    fun save(note: DebitNote, shipmentType: ShipmentType) = database.upsert(
        NoteRow(
            createdAt = System.currentTimeMillis(),
            billTo = note.header.billTo,
            id = UUID.randomUUID().toString(),
            payload = NoteJson.encode(note, shipmentType),
            total = note.total.toPlainString(),
        ),
    )

    private fun purge() = database.purgeOlderThan(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(RETENTION_DAYS))
}
