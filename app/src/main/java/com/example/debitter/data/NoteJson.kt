package com.example.debitter.data

import com.example.debitter.model.ChargeLine
import com.example.debitter.model.CompanyBlock
import com.example.debitter.model.DebitNote
import com.example.debitter.model.NoteHeader
import com.example.debitter.model.NoteLabels
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.util.UUID

object NoteJson {
    private const val KEY_ADDRESS_LINE: String = "addressLine"
    private const val KEY_ADVANCE_RECEIVED: String = "advanceReceived"
    private const val KEY_AMOUNT: String = "amount"
    private const val KEY_APPENDS_SUFFIX: String = "appendsSuffix"
    private const val KEY_BILL_TO: String = "billTo"
    private const val KEY_BL_AWB_NO: String = "blAwbNo"
    private const val KEY_CHARGE_SUFFIX: String = "chargeSuffix"
    private const val KEY_COMPANY: String = "company"
    private const val KEY_CONSIGNMENT: String = "consignment"
    private const val KEY_CONTACT_LINE: String = "contactLine"
    private const val KEY_CONTAINER_NO: String = "containerNo"
    private const val KEY_CUSTOMS_ENTRY: String = "customsEntry"
    private const val KEY_DATE: String = "date"
    private const val KEY_HEADER: String = "header"
    private const val KEY_ID: String = "id"
    private const val KEY_LABEL: String = "label"
    private const val KEY_LABELS: String = "labels"
    private const val KEY_NAME: String = "name"
    private const val KEY_OTHER: String = "other"
    private const val KEY_OTHER_SECTION: String = "otherSection"
    private const val KEY_SIGNATURE: String = "signature"
    private const val KEY_STATUTORY: String = "statutory"
    private const val KEY_STATUTORY_SECTION: String = "statutorySection"
    private const val KEY_SUB_TOTAL: String = "subTotal"
    private const val KEY_TITLE: String = "title"
    private const val KEY_TOTAL: String = "total"
    private const val KEY_VESSEL_FLIGHT: String = "vesselFlight"
    private const val KEY_VOYAGE_NO_DATE: String = "voyageNoDate"

    fun encode(note: DebitNote): String = JSONObject()
        .put(KEY_OTHER, encodeLines(note.other))
        .put(KEY_STATUTORY, encodeLines(note.statutory))
        .put(KEY_ADVANCE_RECEIVED, note.advanceReceived?.toPlainString())
        .put(KEY_COMPANY, encodeCompany(note.company))
        .put(KEY_HEADER, encodeHeader(note.header))
        .put(KEY_LABELS, encodeLabels(note.labels))
        .toString()

    fun decode(payload: String): DebitNote? = runCatching {
        val root = JSONObject(payload)

        DebitNote(
            other = decodeLines(root.optJSONArray(KEY_OTHER)),
            statutory = decodeLines(root.optJSONArray(KEY_STATUTORY)),
            advanceReceived = root.optString(KEY_ADVANCE_RECEIVED).takeIf { it.isNotBlank() }?.toBigDecimalOrNull(),
            company = decodeCompany(root.optJSONObject(KEY_COMPANY)),
            header = decodeHeader(root.optJSONObject(KEY_HEADER)),
            labels = decodeLabels(root.optJSONObject(KEY_LABELS)),
        )
    }.getOrNull()

    private fun encodeLines(lines: List<ChargeLine>): JSONArray {
        val array = JSONArray()

        for (line in lines) {
            array.put(
                JSONObject()
                    .put(KEY_APPENDS_SUFFIX, line.appendsSuffix)
                    .put(KEY_LABEL, line.label)
                    .put(KEY_AMOUNT, line.amount?.toPlainString())
                    .put(KEY_ID, line.id.toString()),
            )
        }
        return array
    }

    private fun encodeCompany(company: CompanyBlock): JSONObject = JSONObject()
        .put(KEY_ADDRESS_LINE, company.addressLine)
        .put(KEY_CONTACT_LINE, company.contactLine)
        .put(KEY_NAME, company.name)

    private fun encodeHeader(header: NoteHeader): JSONObject = JSONObject()
        .put(KEY_BILL_TO, header.billTo)
        .put(KEY_BL_AWB_NO, header.blAwbNo)
        .put(KEY_CONSIGNMENT, header.consignment)
        .put(KEY_CONTAINER_NO, header.containerNo)
        .put(KEY_CUSTOMS_ENTRY, header.customsEntry)
        .put(KEY_VESSEL_FLIGHT, header.vesselFlight)
        .put(KEY_VOYAGE_NO_DATE, header.voyageNoDate)
        .put(KEY_DATE, header.date?.toString())

    private fun encodeLabels(labels: NoteLabels): JSONObject = JSONObject()
        .put(KEY_ADVANCE_RECEIVED, labels.advanceReceived)
        .put(KEY_BILL_TO, labels.billTo)
        .put(KEY_BL_AWB_NO, labels.blAwbNo)
        .put(KEY_CHARGE_SUFFIX, labels.chargeSuffix)
        .put(KEY_CONSIGNMENT, labels.consignment)
        .put(KEY_CONTAINER_NO, labels.containerNo)
        .put(KEY_CUSTOMS_ENTRY, labels.customsEntry)
        .put(KEY_DATE, labels.date)
        .put(KEY_OTHER_SECTION, labels.otherSection)
        .put(KEY_SIGNATURE, labels.signature)
        .put(KEY_STATUTORY_SECTION, labels.statutorySection)
        .put(KEY_SUB_TOTAL, labels.subTotal)
        .put(KEY_TITLE, labels.title)
        .put(KEY_TOTAL, labels.total)
        .put(KEY_VESSEL_FLIGHT, labels.vesselFlight)
        .put(KEY_VOYAGE_NO_DATE, labels.voyageNoDate)

    private fun decodeLines(array: JSONArray?): List<ChargeLine> {
        if (array == null) return emptyList()

        val lines = mutableListOf<ChargeLine>()

        for (index in 0 until array.length()) {
            val item = array.optJSONObject(index) ?: continue

            lines += ChargeLine(
                appendsSuffix = item.optBoolean(KEY_APPENDS_SUFFIX),
                label = item.optString(KEY_LABEL),
                amount = item.optString(KEY_AMOUNT).takeIf { it.isNotBlank() }?.toBigDecimalOrNull(),
                id = runCatching { UUID.fromString(item.optString(KEY_ID)) }.getOrElse { UUID.randomUUID() },
            )
        }
        return lines
    }

    private fun decodeCompany(json: JSONObject?): CompanyBlock {
        val fallback = Defaults.company

        if (json == null) return fallback
        return CompanyBlock(
            addressLine = json.optString(KEY_ADDRESS_LINE, fallback.addressLine),
            contactLine = json.optString(KEY_CONTACT_LINE, fallback.contactLine),
            name = json.optString(KEY_NAME, fallback.name),
        )
    }

    private fun decodeHeader(json: JSONObject?): NoteHeader {
        if (json == null) return NoteHeader(billTo = "", blAwbNo = "", consignment = "", containerNo = "", customsEntry = "", vesselFlight = "", voyageNoDate = "", date = LocalDate.now())
        return NoteHeader(
            billTo = json.optString(KEY_BILL_TO),
            blAwbNo = json.optString(KEY_BL_AWB_NO),
            consignment = json.optString(KEY_CONSIGNMENT),
            containerNo = json.optString(KEY_CONTAINER_NO),
            customsEntry = json.optString(KEY_CUSTOMS_ENTRY),
            vesselFlight = json.optString(KEY_VESSEL_FLIGHT),
            voyageNoDate = json.optString(KEY_VOYAGE_NO_DATE),
            date = json.optString(KEY_DATE).takeIf { it.isNotBlank() }?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
        )
    }

    private fun decodeLabels(json: JSONObject?): NoteLabels {
        val fallback = Defaults.labels

        if (json == null) return fallback
        return NoteLabels(
            advanceReceived = json.optString(KEY_ADVANCE_RECEIVED, fallback.advanceReceived),
            billTo = json.optString(KEY_BILL_TO, fallback.billTo),
            blAwbNo = json.optString(KEY_BL_AWB_NO, fallback.blAwbNo),
            chargeSuffix = json.optString(KEY_CHARGE_SUFFIX, fallback.chargeSuffix),
            consignment = json.optString(KEY_CONSIGNMENT, fallback.consignment),
            containerNo = json.optString(KEY_CONTAINER_NO, fallback.containerNo),
            customsEntry = json.optString(KEY_CUSTOMS_ENTRY, fallback.customsEntry),
            date = json.optString(KEY_DATE, fallback.date),
            otherSection = json.optString(KEY_OTHER_SECTION, fallback.otherSection),
            signature = json.optString(KEY_SIGNATURE, fallback.signature),
            statutorySection = json.optString(KEY_STATUTORY_SECTION, fallback.statutorySection),
            subTotal = json.optString(KEY_SUB_TOTAL, fallback.subTotal),
            title = json.optString(KEY_TITLE, fallback.title),
            total = json.optString(KEY_TOTAL, fallback.total),
            vesselFlight = json.optString(KEY_VESSEL_FLIGHT, fallback.vesselFlight),
            voyageNoDate = json.optString(KEY_VOYAGE_NO_DATE, fallback.voyageNoDate),
        )
    }
}
