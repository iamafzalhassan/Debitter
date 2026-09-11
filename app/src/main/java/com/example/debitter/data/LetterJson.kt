package com.example.debitter.data

import com.example.debitter.model.LetterLabels
import com.example.debitter.model.LetterReferences
import com.example.debitter.model.Letterhead
import com.example.debitter.model.RefundLetter
import com.example.debitter.model.ShippingAgent
import org.json.JSONObject
import java.time.LocalDate

object LetterJson {
    private const val KEY_ADDRESS: String = "address"
    private const val KEY_ADDRESS_LINE: String = "addressLine"
    private const val KEY_AGENT: String = "agent"
    private const val KEY_ATTENTION: String = "attention"
    private const val KEY_BL_NO: String = "blNo"
    private const val KEY_BODY: String = "body"
    private const val KEY_CLOSING: String = "closing"
    private const val KEY_CONTACT_LINE: String = "contactLine"
    private const val KEY_CONTAINER_NO: String = "containerNo"
    private const val KEY_DATE: String = "date"
    private const val KEY_EMPHASIS: String = "emphasis"
    private const val KEY_LABELS: String = "labels"
    private const val KEY_LETTERHEAD: String = "letterhead"
    private const val KEY_NAME: String = "name"
    private const val KEY_RECEIPT_NO: String = "receiptNo"
    private const val KEY_REFERENCES: String = "references"
    private const val KEY_SALUTATION: String = "salutation"
    private const val KEY_SIGN_OFF: String = "signOff"
    private const val KEY_SIGNATORY_NAME: String = "signatoryName"
    private const val KEY_SIGNATORY_PHONE: String = "signatoryPhone"
    private const val KEY_SIGNATORY_TITLE: String = "signatoryTitle"
    private const val KEY_TAGLINE: String = "tagline"
    private const val KEY_TITLE: String = "title"
    private const val KEY_VESSEL: String = "vessel"
    private const val KEY_VOYAGE: String = "voyage"

    fun decode(payload: String): RefundLetter? = runCatching {
        val root = JSONObject(payload)

        RefundLetter(
            letterhead = decodeLetterhead(root.optJSONObject(KEY_LETTERHEAD) ?: JSONObject()),
            labels = decodeLabels(root.optJSONObject(KEY_LABELS)),
            references = decodeReferences(root.optJSONObject(KEY_REFERENCES) ?: JSONObject()),
            date = root.optString(KEY_DATE).takeIf { it.isNotBlank() }?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
            agent = decodeAgent(root.optJSONObject(KEY_AGENT) ?: JSONObject()),
        )
    }.getOrNull()

    fun encode(letter: RefundLetter): String = JSONObject()
        .put(KEY_LETTERHEAD, encodeLetterhead(letter.letterhead))
        .put(KEY_LABELS, encodeLabels(letter.labels))
        .put(KEY_REFERENCES, encodeReferences(letter.references))
        .put(KEY_DATE, letter.date?.toString())
        .put(KEY_AGENT, encodeAgent(letter.agent))
        .toString()

    private fun decodeLetterhead(json: JSONObject): Letterhead = Letterhead(
        addressLine = json.optString(KEY_ADDRESS_LINE),
        contactLine = json.optString(KEY_CONTACT_LINE),
        name = json.optString(KEY_NAME),
        tagline = json.optString(KEY_TAGLINE),
    )

    private fun decodeLabels(json: JSONObject?): LetterLabels {
        val fallback = Defaults.letterLabels

        if (json == null) return fallback
        return LetterLabels(
            attention = json.optString(KEY_ATTENTION, fallback.attention),
            blNo = json.optString(KEY_BL_NO, fallback.blNo),
            body = json.optString(KEY_BODY, fallback.body),
            closing = json.optString(KEY_CLOSING, fallback.closing),
            containerNo = json.optString(KEY_CONTAINER_NO, fallback.containerNo),
            emphasis = json.optString(KEY_EMPHASIS, fallback.emphasis),
            receiptNo = json.optString(KEY_RECEIPT_NO, fallback.receiptNo),
            salutation = json.optString(KEY_SALUTATION, fallback.salutation),
            signatoryName = json.optString(KEY_SIGNATORY_NAME, fallback.signatoryName),
            signatoryPhone = json.optString(KEY_SIGNATORY_PHONE, fallback.signatoryPhone),
            signatoryTitle = json.optString(KEY_SIGNATORY_TITLE, fallback.signatoryTitle),
            signOff = json.optString(KEY_SIGN_OFF, fallback.signOff),
            title = json.optString(KEY_TITLE, fallback.title),
            vessel = json.optString(KEY_VESSEL, fallback.vessel),
            voyage = json.optString(KEY_VOYAGE, fallback.voyage),
        )
    }

    private fun decodeReferences(json: JSONObject): LetterReferences = LetterReferences(
        blNo = json.optString(KEY_BL_NO),
        containerNo = json.optString(KEY_CONTAINER_NO),
        receiptNo = json.optString(KEY_RECEIPT_NO),
        vessel = json.optString(KEY_VESSEL),
        voyage = json.optString(KEY_VOYAGE),
    )

    private fun decodeAgent(json: JSONObject): ShippingAgent = ShippingAgent(address = json.optString(KEY_ADDRESS), name = json.optString(KEY_NAME))

    private fun encodeLetterhead(letterhead: Letterhead): JSONObject = JSONObject()
        .put(KEY_ADDRESS_LINE, letterhead.addressLine)
        .put(KEY_CONTACT_LINE, letterhead.contactLine)
        .put(KEY_NAME, letterhead.name)
        .put(KEY_TAGLINE, letterhead.tagline)

    private fun encodeLabels(labels: LetterLabels): JSONObject = JSONObject()
        .put(KEY_ATTENTION, labels.attention)
        .put(KEY_BL_NO, labels.blNo)
        .put(KEY_BODY, labels.body)
        .put(KEY_CLOSING, labels.closing)
        .put(KEY_CONTAINER_NO, labels.containerNo)
        .put(KEY_EMPHASIS, labels.emphasis)
        .put(KEY_RECEIPT_NO, labels.receiptNo)
        .put(KEY_SALUTATION, labels.salutation)
        .put(KEY_SIGNATORY_NAME, labels.signatoryName)
        .put(KEY_SIGNATORY_PHONE, labels.signatoryPhone)
        .put(KEY_SIGNATORY_TITLE, labels.signatoryTitle)
        .put(KEY_SIGN_OFF, labels.signOff)
        .put(KEY_TITLE, labels.title)
        .put(KEY_VESSEL, labels.vessel)
        .put(KEY_VOYAGE, labels.voyage)

    private fun encodeReferences(references: LetterReferences): JSONObject = JSONObject()
        .put(KEY_BL_NO, references.blNo)
        .put(KEY_CONTAINER_NO, references.containerNo)
        .put(KEY_RECEIPT_NO, references.receiptNo)
        .put(KEY_VESSEL, references.vessel)
        .put(KEY_VOYAGE, references.voyage)

    private fun encodeAgent(agent: ShippingAgent): JSONObject = JSONObject()
        .put(KEY_ADDRESS, agent.address)
        .put(KEY_NAME, agent.name)
}
