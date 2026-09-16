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
        var fitted = layout
        var attempt = NotePainter(fitted).render(note)

        while (attempt.isOverflowing && fitted.scale > PdfLayout.MIN_SCALE) {
            fitted = fitted.scaledTo(nextScale(fitted, attempt))
            attempt = NotePainter(fitted).render(note)
        }
        return attempt.bytes
    }

    private fun nextScale(fitted: PdfLayout, attempt: SheetResult): Float = (fitted.scale * attempt.fitRatio).coerceAtMost(fitted.scale - PdfLayout.FIT_STEP).coerceAtLeast(PdfLayout.MIN_SCALE)
}

private class SheetResult(val bytes: ByteArray, val fitRatio: Float, val isOverflowing: Boolean)

private class NotePainter(private val layout: PdfLayout) {
    fun render(note: DebitNote): SheetResult {
        val document = PdfDocument()

        try {
            val sheet = Sheet(document, layout)

            paint(sheet, note)
            sheet.finish()

            val stream = ByteArrayOutputStream()

            document.writeTo(stream)
            return SheetResult(bytes = stream.toByteArray(), fitRatio = sheet.fitRatio, isOverflowing = sheet.isOverflowing)
        } finally {
            document.close()
        }
    }

    private fun paint(sheet: Sheet, note: DebitNote) {
        sheet.start()
        drawLetterhead(sheet, note)
        drawTitle(sheet, note.labels.title)
        drawHeader(sheet, note.labels, note.header)
        drawTable(sheet, note)
        drawSignature(sheet, note.labels.signature)
    }

    private fun drawLetterhead(sheet: Sheet, note: DebitNote) {
        val block = note.company
        val top = sheet.y

        sheet.canvas.drawText(block.name, layout.contentLeft, layout.baseline(top, layout.lineHeight(layout.companyNamePaint), layout.companyNamePaint), layout.companyNamePaint)
        sheet.y = top + layout.lineHeight(layout.companyNamePaint) + layout.gapXs
        drawDetail(sheet, block.addressLine)
        drawDetail(sheet, block.contactLine)
        sheet.y += layout.gapMd
        drawRule(sheet, layout.ruleStrongPaint)
        sheet.y += layout.gapMd
    }

    private fun drawDetail(sheet: Sheet, text: String) {
        if (text.isBlank()) return

        val height = layout.lineHeight(layout.companyDetailPaint)

        sheet.canvas.drawText(text, layout.contentLeft, layout.baseline(sheet.y, height, layout.companyDetailPaint), layout.companyDetailPaint)
        sheet.y += height
    }

    private fun drawTitle(sheet: Sheet, title: String) {
        if (title.isBlank()) return

        val height = layout.lineHeight(layout.titlePaint)

        sheet.canvas.drawText(title, layout.contentCenterX, layout.baseline(sheet.y, height, layout.titlePaint), layout.titlePaint)
        sheet.y += height + layout.gapMd
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
        val columnLength = rows.size / 2

        for (index in 0 until columnLength) {
            drawHeaderRow(sheet, rows[index], rows[index + columnLength])
        }
        sheet.y += layout.gapMd
    }

    private fun drawHeaderRow(sheet: Sheet, left: Pair<String, String>, right: Pair<String, String>) {
        val leftLines = headerLines(left)
        val rightLines = headerLines(right)

        if (leftLines.isEmpty() && rightLines.isEmpty()) return

        val lineCount = maxOf(leftLines.size, rightLines.size)
        val lineHeight = layout.lineHeight(layout.metaStrongPaint)
        val height = maxOf(layout.headerRowHeight, lineCount * lineHeight)

        sheet.ensure(height)
        val firstBaseline = layout.blockBaseline(sheet.y, height, lineCount, layout.metaStrongPaint)

        drawHeaderCell(sheet, left, leftLines, layout.contentLeft, firstBaseline)
        drawHeaderCell(sheet, right, rightLines, layout.headerRightX, firstBaseline)
        sheet.y += height
    }

    private fun headerLines(cell: Pair<String, String>): List<String> {
        if (cell.first.isBlank() && cell.second.isBlank()) return emptyList()
        return layout.wrap(cell.second, layout.metaStrongPaint, layout.headerValueWidth)
    }

    private fun drawHeaderCell(sheet: Sheet, cell: Pair<String, String>, lines: List<String>, columnX: Float, firstBaseline: Float) {
        if (lines.isEmpty()) return

        val lineHeight = layout.lineHeight(layout.metaStrongPaint)

        sheet.canvas.drawText(cell.first, columnX, firstBaseline, layout.labelPaint)
        sheet.canvas.drawText(":", layout.colonX(columnX), firstBaseline, layout.labelPaint)
        lines.forEachIndexed { index, line -> sheet.canvas.drawText(line, layout.headerValueLeft(columnX), firstBaseline + index * lineHeight, layout.metaStrongPaint) }
    }

    private fun drawTable(sheet: Sheet, note: DebitNote) {
        val sections = listOf(note.labels.statutorySection to note.printableStatutory, note.labels.otherSection to note.printableOther).filter { it.second.isNotEmpty() }

        drawRule(sheet, layout.rulePaint)
        for ((heading, lines) in sections) {
            drawBand(sheet, heading)
            for (line in lines) {
                drawChargeRow(sheet, line, note.labels.chargeSuffix)
                drawRule(sheet, layout.rulePaint)
            }
        }
        drawTotals(sheet, note)
    }

    private fun drawBand(sheet: Sheet, heading: String) {
        sheet.ensure(layout.bandHeight + PdfLayout.RULE_THIN + layout.chargeRowHeight)
        sheet.canvas.drawRect(layout.contentLeft, sheet.y, layout.contentRight, sheet.y + layout.bandHeight, layout.bandPaint)
        sheet.canvas.drawText(heading, layout.cellLeft, layout.baseline(sheet.y, layout.bandHeight, layout.labelPaint), layout.labelPaint)
        sheet.y += layout.bandHeight
        drawRule(sheet, layout.rulePaint)
    }

    private fun drawChargeRow(sheet: Sheet, line: ChargeLine, suffix: String) {
        val wrapped = layout.wrap(line.printedLabel(suffix), layout.bodyPaint, layout.chargeLabelWidth)
        val lineHeight = layout.lineHeight(layout.bodyPaint)
        val height = maxOf(layout.chargeRowHeight, wrapped.size * lineHeight)

        sheet.ensure(height)
        val firstBaseline = layout.blockBaseline(sheet.y, height, wrapped.size, layout.bodyPaint)

        wrapped.forEachIndexed { index, text -> sheet.canvas.drawText(text, layout.cellLeft, firstBaseline + index * lineHeight, layout.bodyPaint) }
        sheet.canvas.drawText(MoneyFormat.format(line.amount ?: BigDecimal.ZERO), layout.amountRight, firstBaseline, layout.amountPaint)
        sheet.y += height
    }

    private fun drawTotals(sheet: Sheet, note: DebitNote) {
        val rowCount = if (note.showsAdvance) 3 else 2
        val height = rowCount * (layout.totalsRowHeight + PdfLayout.RULE_THIN)

        sheet.ensure(height)
        drawTotalsRow(sheet, note.labels.subTotal, note.subTotal, layout.totalsValuePaint)
        if (note.showsAdvance) drawTotalsRow(sheet, note.labels.advanceReceived, note.advanceReceived ?: BigDecimal.ZERO, layout.totalsValuePaint)
        drawTotalsRow(sheet, note.labels.total, note.total, layout.totalsValueBoldPaint)
    }

    private fun drawTotalsRow(sheet: Sheet, label: String, amount: BigDecimal, valuePaint: Paint) {
        val baseline = layout.baseline(sheet.y, layout.totalsRowHeight, valuePaint)

        sheet.canvas.drawRect(layout.contentLeft, sheet.y, layout.contentRight, sheet.y + layout.totalsRowHeight, layout.bandPaint)
        sheet.canvas.drawText(label, layout.cellLeft, baseline, layout.labelPaint)
        sheet.canvas.drawText(MoneyFormat.format(amount), layout.amountRight, baseline, valuePaint)
        sheet.y += layout.totalsRowHeight
        drawRule(sheet, layout.rulePaint)
    }

    private fun drawSignature(sheet: Sheet, caption: String) {
        if (caption.isBlank()) return

        sheet.ensure(layout.signatureBlockHeight)
        sheet.y = maxOf(sheet.y + layout.signatureSpace, layout.contentBottom - layout.signatureBlockHeight + layout.signatureSpace)

        val ruleY = layout.snap(sheet.y)

        sheet.canvas.drawLine(layout.signatureLeft, ruleY, layout.contentRight, ruleY, layout.ruleStrongPaint)
        sheet.y += PdfLayout.RULE_STRONG + layout.gapXs
        sheet.canvas.drawText(caption, layout.signatureCenterX, sheet.y - layout.signaturePaint.fontMetrics.ascent, layout.signaturePaint)
        sheet.y += layout.lineHeight(layout.signaturePaint)
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

    private var demand: Float = 0f

    private var page: PdfDocument.Page? = null

    val fitRatio: Float get() = if (demand > layout.contentTop) layout.contentHeight / (demand - layout.contentTop) else 1f

    val isOverflowing: Boolean get() = demand > layout.contentBottom

    fun ensure(height: Float) {
        demand = maxOf(demand, y + height)
    }

    fun start() {
        finish()
        val info = PdfDocument.PageInfo.Builder(PdfLayout.PAGE_WIDTH, PdfLayout.PAGE_HEIGHT, 1).create()
        val started = document.startPage(info)

        page = started
        canvas = started.canvas
        y = layout.contentTop
    }

    fun finish() {
        val current = page ?: return

        document.finishPage(current)
        page = null
    }
}
