package com.example.debitter.pdf

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.debitter.model.ChargeLine
import com.example.debitter.model.DebitNote
import com.example.debitter.model.NoteHeader
import com.example.debitter.model.NoteLabels
import com.example.debitter.util.DateFormat
import com.example.debitter.util.MoneyFormat
import java.io.ByteArrayOutputStream
import java.math.BigDecimal

class DebitNotePdfGenerator(private val layout: PdfLayout) {
    fun render(note: DebitNote): ByteArray {
        val document = PdfDocument()
        val sheet = Sheet(document, layout)

        sheet.start()
        drawCompany(sheet, note)
        drawTitle(sheet, note)
        drawHeader(sheet, note.labels, note.header)
        drawCharges(sheet, note.labels.statutorySection, note.printableStatutory, note.labels.chargeSuffix)
        drawCharges(sheet, note.labels.otherSection, note.printableOther, note.labels.chargeSuffix)
        drawTotals(sheet, note)
        drawSignature(sheet, note.labels.signature)
        sheet.finish()

        val stream = ByteArrayOutputStream()
        document.writeTo(stream)
        document.close()
        return stream.toByteArray()
    }

    private fun drawCompany(sheet: Sheet, note: DebitNote) {
        val block = note.company
        val height = layout.lineHeight(layout.companyNamePaint) + layout.lineHeight(layout.companyDetailPaint) * 2 + PdfLayout.GAP_SM

        sheet.ensure(height)
        drawCentered(sheet, block.name, layout.companyNamePaint)
        drawCentered(sheet, block.addressLine, layout.companyDetailPaint)
        drawCentered(sheet, block.contactLine, layout.companyDetailPaint)
        sheet.y += PdfLayout.GAP_SM
        drawRule(sheet, layout.ruleStrongPaint)
    }

    private fun drawTitle(sheet: Sheet, note: DebitNote) {
        sheet.y += PdfLayout.GAP_MD
        drawCentered(sheet, note.labels.title, layout.titlePaint)
        sheet.y += PdfLayout.GAP_MD
    }

    private fun drawHeader(sheet: Sheet, labels: NoteLabels, header: NoteHeader) {
        val rows = listOf(
            labels.date to DateFormat.format(header.date),
            labels.billTo to header.billTo,
            labels.vesselFlight to header.vesselFlight,
            labels.customsEntry to header.customsEntry,
            labels.containerNo to header.containerNo,
            labels.blAwbNo to header.blAwbNo,
            labels.voyageNoDate to header.voyageNoDate,
            labels.consignment to header.consignment,
        )

        for ((label, value) in rows) {
            if (label.isBlank() && value.isBlank()) continue
            drawHeaderRow(sheet, label, value)
        }
        sheet.y += PdfLayout.GAP_SM
        drawRule(sheet, layout.rulePaint)
    }

    private fun drawHeaderRow(sheet: Sheet, label: String, value: String) {
        val lines = layout.wrap(value, layout.headerValuePaint, layout.headerValueWidth)
        val lineHeight = layout.lineHeight(layout.headerValuePaint)
        val height = maxOf(PdfLayout.HEADER_ROW_HEIGHT, lines.size * lineHeight)

        sheet.ensure(height)
        val firstBaseline = layout.blockBaseline(sheet.y, height, lines.size, layout.headerValuePaint)
        sheet.canvas.drawText(label, layout.contentLeft, firstBaseline, layout.headerLabelPaint)
        sheet.canvas.drawText(":", layout.colonX, firstBaseline, layout.headerLabelPaint)
        lines.forEachIndexed { index, line -> sheet.canvas.drawText(line, layout.headerValueLeft, firstBaseline + index * lineHeight, layout.headerValuePaint) }
        sheet.y += height
    }

    private fun drawCharges(sheet: Sheet, heading: String, lines: List<ChargeLine>, suffix: String) {
        if (lines.isEmpty()) return

        sheet.ensure(PdfLayout.BAND_HEIGHT + PdfLayout.CHARGE_ROW_HEIGHT)
        sheet.canvas.drawRect(layout.contentLeft, sheet.y, layout.contentRight, sheet.y + PdfLayout.BAND_HEIGHT, layout.bandPaint)
        sheet.canvas.drawText(heading, layout.chargeLabelLeft, layout.baseline(sheet.y, PdfLayout.BAND_HEIGHT, layout.sectionPaint), layout.sectionPaint)
        sheet.y += PdfLayout.BAND_HEIGHT

        for (line in lines) drawChargeRow(sheet, line, suffix)
        drawRule(sheet, layout.rulePaint)
    }

    private fun drawChargeRow(sheet: Sheet, line: ChargeLine, suffix: String) {
        val wrapped = layout.wrap(line.printedLabel(suffix), layout.chargeLabelPaint, layout.chargeLabelWidth)
        val lineHeight = layout.lineHeight(layout.chargeLabelPaint)
        val height = maxOf(PdfLayout.CHARGE_ROW_HEIGHT, wrapped.size * lineHeight)

        sheet.ensure(height)
        val firstBaseline = layout.blockBaseline(sheet.y, height, wrapped.size, layout.chargeLabelPaint)
        wrapped.forEachIndexed { index, text -> sheet.canvas.drawText(text, layout.chargeLabelLeft, firstBaseline + index * lineHeight, layout.chargeLabelPaint) }
        sheet.canvas.drawText(MoneyFormat.format(line.amount ?: BigDecimal.ZERO), layout.amountRight, firstBaseline, layout.amountPaint)
        sheet.y += height
    }

    private fun drawTotals(sheet: Sheet, note: DebitNote) {
        val rowCount = if (note.showsAdvance) 2 else 1
        val height = PdfLayout.GAP_SM + rowCount * PdfLayout.TOTALS_ROW_HEIGHT + PdfLayout.GAP_XS + PdfLayout.TOTALS_ROW_HEIGHT + PdfLayout.RULE_STRONG * 2

        sheet.ensure(height)
        sheet.y += PdfLayout.GAP_SM
        drawTotalsRow(sheet, note.labels.subTotal, note.subTotal, layout.totalsValuePaint)
        if (note.showsAdvance) drawTotalsRow(sheet, note.labels.advanceReceived, note.advanceReceived ?: BigDecimal.ZERO, layout.totalsValuePaint)
        sheet.y += PdfLayout.GAP_XS
        drawRule(sheet, layout.ruleStrongPaint)
        drawTotalsRow(sheet, note.labels.total, note.total, layout.totalsValueStrongPaint)
        drawRule(sheet, layout.ruleStrongPaint)
    }

    private fun drawTotalsRow(sheet: Sheet, label: String, amount: BigDecimal, valuePaint: Paint) {
        val baseline = layout.baseline(sheet.y, PdfLayout.TOTALS_ROW_HEIGHT, valuePaint)

        sheet.canvas.drawText(label, layout.amountLeft - PdfLayout.LABEL_GAP, baseline, layout.totalsLabelPaint)
        sheet.canvas.drawText(MoneyFormat.format(amount), layout.amountRight, baseline, valuePaint)
        sheet.y += PdfLayout.TOTALS_ROW_HEIGHT
    }

    private fun drawSignature(sheet: Sheet, caption: String) {
        if (caption.isBlank()) return

        sheet.ensure(layout.signatureBlockHeight)
        sheet.y = maxOf(sheet.y + PdfLayout.SIGNATURE_GAP, layout.contentBottom - layout.signatureBlockHeight + PdfLayout.SIGNATURE_GAP)

        val lineY = layout.snap(sheet.y)
        val lineLeft = layout.contentRight - PdfLayout.SIGNATURE_LINE_WIDTH

        sheet.canvas.drawLine(lineLeft, lineY, layout.contentRight, lineY, layout.rulePaint)
        sheet.y += PdfLayout.GAP_SM
        sheet.canvas.drawText(caption, lineLeft + PdfLayout.SIGNATURE_LINE_WIDTH / 2f, sheet.y - layout.signaturePaint.fontMetrics.ascent, layout.signaturePaint)
        sheet.y += layout.lineHeight(layout.signaturePaint)
    }

    private fun drawCentered(sheet: Sheet, text: String, paint: Paint) {
        val height = layout.lineHeight(paint)

        sheet.ensure(height)
        sheet.canvas.drawText(text, layout.pageCenterX, layout.baseline(sheet.y, height, paint), paint)
        sheet.y += height
    }

    private fun drawRule(sheet: Sheet, paint: Paint) {
        sheet.ensure(paint.strokeWidth)
        val ruleY = layout.snap(sheet.y)

        sheet.canvas.drawLine(layout.contentLeft, ruleY, layout.contentRight, ruleY, paint)
        sheet.y += paint.strokeWidth
    }
}

private class Sheet(private val document: PdfDocument, private val layout: PdfLayout) {
    lateinit var canvas: Canvas

    var y: Float = 0f

    private var number: Int = 0

    private var page: PdfDocument.Page? = null

    fun start() = newPage()

    fun ensure(height: Float) {
        if (y + height > layout.contentBottom) newPage()
    }

    fun newPage() {
        finish()
        number += 1
        val info = PdfDocument.PageInfo.Builder(PdfLayout.PAGE_WIDTH, PdfLayout.PAGE_HEIGHT, number).create()
        val started = document.startPage(info)

        page = started
        canvas = started.canvas
        y = layout.contentTop
    }

    fun finish() {
        page?.let { document.finishPage(it) }
        page = null
    }
}
