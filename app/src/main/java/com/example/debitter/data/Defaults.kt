package com.example.debitter.data

import com.example.debitter.model.ChargeLine
import com.example.debitter.model.ChargeSection
import com.example.debitter.model.CompanyBlock
import com.example.debitter.model.DebitNote
import com.example.debitter.model.LetterLabels
import com.example.debitter.model.LetterReferences
import com.example.debitter.model.Letterhead
import com.example.debitter.model.NoteHeader
import com.example.debitter.model.NoteLabels
import com.example.debitter.model.RefundLetter
import com.example.debitter.model.ShippingAgent
import java.time.LocalDate

object Defaults {
    val company: CompanyBlock = CompanyBlock(
        addressLine = "NO. 59/2, DEMATAGODA ROAD, MARADANA, COLOMBO, SRI LANKA.",
        contactLine = "TEL: +94 (77) 754 0094 | EMAIL: WORLDOFCARGO@OUTLOOK.COM",
        name = "CARGO WORLD",
    )

    val letterLabels: LetterLabels = LetterLabels(
        attention = "The Manager,",
        blNo = "BL No",
        body = "The above are the details of the container deposit we have made with regard from the removal of our consignment container in the above container. " +
            "The copies of the allied document available are annexed here to for your easy reference and kind perusal please. " +
            "We deeply regret and apologies for the delay in making this application but as we have duly handed over the container to your yard in good order within the prescribed period, " +
            "we thank you for making early arrangement to refund us the container deposit thus made. " +
            "We should also thank you to we no objection to refund the cheque ${company.name}, No. 59/2, Dematagoda Road, Maradana, Colombo, who had paid these dues to you on our behalf.",
        closing = "Thanking you in advance,",
        containerNo = "Container No",
        emphasis = company.name,
        receiptNo = "Receipt No",
        salutation = "Dear Sir,",
        signatoryName = "M.G.A. Zulfy",
        signatoryPhone = "+94 (77) 754 0094",
        signatoryTitle = "Proprietor",
        signOff = "Yours faithfully,",
        title = "REQUEST FOR THE REFUND OF THE CONTAINER DEPOSIT",
        vessel = "Vessel",
        voyage = "Voyage",
    )

    val labels: NoteLabels = NoteLabels(
        advanceReceived = "ADVANCED RECEIVED",
        billTo = "TO",
        blAwbNo = "BL/AWB NO",
        chargeSuffix = "CHARGES",
        consignment = "CONSIGNMENT",
        containerNo = "CONTAINER NO",
        customsEntry = "CUSTOMS ENTRY",
        date = "DATE",
        otherSection = "OTHER",
        signature = "PROPRIETOR'S SIGNATURE.",
        statutorySection = "STATUTORY",
        subTotal = "SUB TOTAL",
        title = "DEBIT NOTE",
        total = "TOTAL",
        vesselFlight = "VESSEL/FLIGHT",
        voyageNoDate = "VOYAGE NO/DATE",
    )

    fun letter(today: LocalDate = LocalDate.now()): RefundLetter = RefundLetter(
        letterhead = Letterhead(addressLine = "", contactLine = "", name = "", tagline = ""),
        labels = letterLabels,
        references = LetterReferences(blNo = "", containerNo = "", receiptNo = "", vessel = "", voyage = ""),
        date = today,
        agent = ShippingAgent(address = "", name = ""),
    )

    fun note(today: LocalDate = LocalDate.now()): DebitNote = DebitNote(
        other = lines(ChargeSection.OTHER),
        statutory = lines(ChargeSection.STATUTORY),
        advanceReceived = null,
        company = company,
        header = header(today),
        labels = labels,
    )

    fun lines(section: ChargeSection): List<ChargeLine> = ChargePresets.labels(section).map { ChargeLine.preset(label = it, appendsSuffix = ChargePresets.appendsSuffix(it)) }

    fun header(date: LocalDate?): NoteHeader = NoteHeader(
        billTo = "",
        blAwbNo = "",
        consignment = "",
        containerNo = "",
        customsEntry = "",
        vesselFlight = "",
        voyageNoDate = "",
        date = date,
    )
}
