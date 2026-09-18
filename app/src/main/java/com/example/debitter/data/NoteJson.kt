package com.example.debitter.data

import com.example.debitter.model.ChargeLine
import com.example.debitter.model.CompanyField
import com.example.debitter.model.DebitNote
import com.example.debitter.model.HeaderField
import com.example.debitter.model.LabelField
import com.example.debitter.model.NoteHeader
import com.example.debitter.model.value
import com.example.debitter.model.with
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.util.UUID

object NoteJson {
    private const val KEY_ADVANCE_RECEIVED: String = "advanceReceived"
    private const val KEY_AMOUNT: String = "amount"
    private const val KEY_APPENDS_SUFFIX: String = "appendsSuffix"
    private const val KEY_COMPANY: String = "company"
    private const val KEY_DATE: String = "date"
    private const val KEY_HEADER: String = "header"
    private const val KEY_ID: String = "id"
    private const val KEY_LABEL: String = "label"
    private const val KEY_LABELS: String = "labels"
    private const val KEY_OTHER: String = "other"
    private const val KEY_STATUTORY: String = "statutory"

    fun decode(payload: String): DebitNote? = runCatching {
        val root = JSONObject(payload)
        val company = root.optJSONObject(KEY_COMPANY)
        val labels = root.optJSONObject(KEY_LABELS)

        DebitNote(
            other = decodeLines(root.optJSONArray(KEY_OTHER)),
            statutory = decodeLines(root.optJSONArray(KEY_STATUTORY)),
            advanceReceived = root.optString(KEY_ADVANCE_RECEIVED).takeIf { it.isNotBlank() }?.toBigDecimalOrNull(),
            company = if (company == null) Defaults.company else CompanyField.entries.fold(Defaults.company) { block, field -> block.with(field, company.optString(field.key, block.value(field))) },
            header = decodeHeader(root.optJSONObject(KEY_HEADER)),
            labels = if (labels == null) Defaults.labels else LabelField.entries.fold(Defaults.labels) { current, field -> current.with(field, labels.optString(field.key, current.value(field))) },
        )
    }.getOrNull()

    fun encode(note: DebitNote): String = JSONObject()
        .put(KEY_OTHER, encodeLines(note.other))
        .put(KEY_STATUTORY, encodeLines(note.statutory))
        .put(KEY_ADVANCE_RECEIVED, note.advanceReceived?.toPlainString())
        .put(KEY_COMPANY, CompanyField.entries.fold(JSONObject()) { json, field -> json.put(field.key, note.company.value(field)) })
        .put(KEY_HEADER, HeaderField.entries.fold(JSONObject().put(KEY_DATE, note.header.date?.toString())) { json, field -> json.put(field.key, note.header.value(field)) })
        .put(KEY_LABELS, LabelField.entries.fold(JSONObject()) { json, field -> json.put(field.key, note.labels.value(field)) })
        .toString()

    private fun decodeLines(array: JSONArray?): List<ChargeLine> = if (array == null) {
        emptyList()
    } else {
        (0 until array.length()).mapNotNull { array.optJSONObject(it) }.map { item ->
            ChargeLine(
                appendsSuffix = item.optBoolean(KEY_APPENDS_SUFFIX),
                label = item.optString(KEY_LABEL),
                amount = item.optString(KEY_AMOUNT).takeIf { it.isNotBlank() }?.toBigDecimalOrNull(),
                id = runCatching { UUID.fromString(item.optString(KEY_ID)) }.getOrElse { UUID.randomUUID() },
            )
        }
    }

    private fun decodeHeader(json: JSONObject?): NoteHeader {
        if (json == null) return Defaults.header(LocalDate.now())

        val date = json.optString(KEY_DATE).takeIf { it.isNotBlank() }?.let { runCatching { LocalDate.parse(it) }.getOrNull() }

        return HeaderField.entries.fold(Defaults.header(date)) { header, field -> header.with(field, json.optString(field.key)) }
    }

    private fun encodeLines(lines: List<ChargeLine>): JSONArray = JSONArray(
        lines.map { line ->
            JSONObject()
                .put(KEY_APPENDS_SUFFIX, line.appendsSuffix)
                .put(KEY_LABEL, line.label)
                .put(KEY_AMOUNT, line.amount?.toPlainString())
                .put(KEY_ID, line.id.toString())
        },
    )
}
