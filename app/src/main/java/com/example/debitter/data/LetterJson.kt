package com.example.debitter.data

import com.example.debitter.model.AgentField
import com.example.debitter.model.LetterLabelField
import com.example.debitter.model.LetterheadField
import com.example.debitter.model.ReferenceField
import com.example.debitter.model.RefundLetter
import com.example.debitter.model.value
import com.example.debitter.model.with
import org.json.JSONObject
import java.time.LocalDate

object LetterJson {
    private const val KEY_AGENT: String = "agent"
    private const val KEY_DATE: String = "date"
    private const val KEY_LABELS: String = "labels"
    private const val KEY_LETTERHEAD: String = "letterhead"
    private const val KEY_REFERENCES: String = "references"

    fun decode(payload: String): RefundLetter? = runCatching {
        val root = JSONObject(payload)
        val agent = root.optJSONObject(KEY_AGENT) ?: JSONObject()
        val blank = Defaults.letter()
        val labels = root.optJSONObject(KEY_LABELS)
        val letterhead = root.optJSONObject(KEY_LETTERHEAD) ?: JSONObject()
        val references = root.optJSONObject(KEY_REFERENCES) ?: JSONObject()

        RefundLetter(
            letterhead = LetterheadField.entries.fold(blank.letterhead) { current, field -> current.with(field, letterhead.optString(field.key)) },
            labels = if (labels == null) blank.labels else LetterLabelField.entries.fold(blank.labels) { current, field -> current.with(field, labels.optString(field.key, current.value(field))) },
            references = ReferenceField.entries.fold(blank.references) { current, field -> current.with(field, references.optString(field.key)) },
            date = root.optString(KEY_DATE).takeIf { it.isNotBlank() }?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
            agent = AgentField.entries.fold(blank.agent) { current, field -> current.with(field, agent.optString(field.key)) },
        )
    }.getOrNull()

    fun encode(letter: RefundLetter): String = JSONObject()
        .put(KEY_LETTERHEAD, LetterheadField.entries.fold(JSONObject()) { json, field -> json.put(field.key, letter.letterhead.value(field)) })
        .put(KEY_LABELS, LetterLabelField.entries.fold(JSONObject()) { json, field -> json.put(field.key, letter.labels.value(field)) })
        .put(KEY_REFERENCES, ReferenceField.entries.fold(JSONObject()) { json, field -> json.put(field.key, letter.references.value(field)) })
        .put(KEY_DATE, letter.date?.toString())
        .put(KEY_AGENT, AgentField.entries.fold(JSONObject()) { json, field -> json.put(field.key, letter.agent.value(field)) })
        .toString()
}
