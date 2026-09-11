package com.example.debitter.data

import com.example.debitter.data.sources.LetterRow
import com.example.debitter.data.sources.NoteDatabase
import com.example.debitter.model.RefundLetter
import com.example.debitter.model.SavedLetter
import java.util.UUID

class RecentLettersRepository(private val database: NoteDatabase) {
    fun delete(id: String) = database.deleteLetter(id)

    fun load(): List<SavedLetter> = database.readAllLetters().mapNotNull { row ->
        val letter = LetterJson.decode(row.payload) ?: return@mapNotNull null

        SavedLetter(
            createdAt = row.createdAt,
            agent = row.agent,
            customer = row.customer,
            id = row.id,
            letter = letter,
        )
    }

    fun save(letter: RefundLetter) = database.upsertLetter(
        LetterRow(
            createdAt = System.currentTimeMillis(),
            agent = letter.agent.name,
            customer = letter.letterhead.name,
            id = UUID.randomUUID().toString(),
            payload = LetterJson.encode(letter),
        ),
    )
}
