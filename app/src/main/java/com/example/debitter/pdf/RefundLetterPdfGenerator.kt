package com.example.debitter.pdf

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.debitter.model.LetterLabels
import com.example.debitter.model.LetterReferences
import com.example.debitter.model.Letterhead
import com.example.debitter.model.ReferenceField
import com.example.debitter.model.RefundLetter
import com.example.debitter.model.value
import com.example.debitter.util.DateFormat
import java.io.ByteArrayOutputStream

private const val SINGLE_PAGE: Int = 1

class RefundLetterPdfGenerator(private val layout: LetterLayout) {
    fun render(letter: RefundLetter): ByteArray {
        val document = PdfDocument()

        try {
            val sheet = LetterSheet(countPages(letter), document, layout)

            paint(sheet, letter)
            sheet.finish()

            val stream = ByteArrayOutputStream()

            document.writeTo(stream)
            return stream.toByteArray()
        } finally {
            document.close()
        }
    }

    private fun countPages(letter: RefundLetter): Int {
        val document = PdfDocument()

        try {
            val sheet = LetterSheet(SINGLE_PAGE, document, layout)

            paint(sheet, letter)
            sheet.finish()
            return sheet.number
        } finally {
            document.close()
        }
    }

    private fun paint(sheet: LetterSheet, letter: RefundLetter) {
        sheet.start()
        drawLetterhead(sheet, letter.letterhead)
        drawBlock(sheet, listOf(DateFormat.format(letter.date)))
        drawBlock(sheet, listOf(letter.labels.attention) + punctuated(listOf(letter.agent.name) + letter.agent.address.lines()))
        drawTitle(sheet, letter.labels.title)
        drawReferences(sheet, letter.labels, letter.references)
        drawBlock(sheet, listOf(letter.labels.salutation))
        drawBody(sheet, letter.labels)
        drawSignature(sheet, letter.labels)
    }

    private fun drawLetterhead(sheet: LetterSheet, letterhead: Letterhead) {
        drawWrapped(sheet, letterhead.name, layout.letterheadNamePaint, layout.contentCenterX)
        sheet.y += LetterLayout.GAP_XS
        drawWrapped(sheet, letterhead.tagline, layout.taglinePaint, layout.contentCenterX)
        drawWrapped(sheet, letterhead.addressLine, layout.letterheadDetailPaint, layout.contentCenterX)
        drawWrapped(sheet, letterhead.contactLine, layout.letterheadDetailPaint, layout.contentCenterX)
        sheet.y += LetterLayout.GAP_MD
        drawRule(sheet)
        sheet.y += LetterLayout.GAP_MD
    }

    private fun drawRule(sheet: LetterSheet) {
        sheet.ensure(layout.ruleStrongPaint.strokeWidth)
        val ruleY = layout.snap(sheet.y)

        sheet.canvas.drawLine(layout.contentLeft, ruleY, layout.contentRight, ruleY, layout.ruleStrongPaint)
        sheet.y += layout.ruleStrongPaint.strokeWidth
    }

    private fun drawBlock(sheet: LetterSheet, lines: List<String>) {
        val printed = lines.filter { it.isNotBlank() }

        if (printed.isEmpty()) return
        printed.forEach { drawWrapped(sheet, it, layout.bodyPaint, layout.contentLeft) }
        sheet.y += LetterLayout.GAP_MD
    }

    private fun punctuated(lines: List<String>): List<String> {
        val parts = lines.map { it.trim().trimEnd(',', '.').trimEnd() }.filter { it.isNotEmpty() }

        return parts.mapIndexed { index, part -> if (index == parts.lastIndex) "$part." else "$part," }
    }

    private fun drawTitle(sheet: LetterSheet, title: String) {
        if (title.isBlank()) return

        drawWrapped(sheet, title, layout.titlePaint, layout.contentCenterX)
        sheet.y += LetterLayout.GAP_MD
    }

    private fun drawReferences(sheet: LetterSheet, labels: LetterLabels, references: LetterReferences) {
        val rows = ReferenceField.entries.map { labels.value(it) to references.value(it) }.filter { it.second.isNotBlank() }

        if (rows.isEmpty()) return
        rows.forEach { drawReferenceRow(sheet, it) }
        sheet.y += LetterLayout.GAP_MD
    }

    private fun drawReferenceRow(sheet: LetterSheet, row: Pair<String, String>) {
        val labelLines = layout.wrap(row.first, layout.boldPaint, layout.labelWidth)
        val valueLines = row.second.lines().filter { it.isNotBlank() }.flatMap { layout.wrap(it, layout.bodyPaint, layout.valueWidth) }
        val lineHeight = layout.lineHeight(layout.bodyPaint)
        val height = maxOf(labelLines.size, valueLines.size) * lineHeight

        sheet.ensure(height)
        val firstBaseline = layout.baseline(sheet.y, layout.bodyPaint)

        labelLines.forEachIndexed { index, line -> sheet.canvas.drawText(line, layout.contentLeft, firstBaseline + index * lineHeight, layout.boldPaint) }
        sheet.canvas.drawText(":", layout.colonX, firstBaseline, layout.bodyPaint)
        valueLines.forEachIndexed { index, line -> sheet.canvas.drawText(line, layout.valueLeft, firstBaseline + index * lineHeight, layout.bodyPaint) }
        sheet.y += height
    }

    private fun drawBody(sheet: LetterSheet, labels: LetterLabels) {
        if (labels.body.isBlank()) return

        val lineHeight = layout.lineHeight(layout.bodyPaint)

        for (paragraph in labels.body.trim().lines()) {
            if (paragraph.isBlank()) {
                sheet.y += lineHeight
                continue
            }
            layout.wrapWords(emphasised(paragraph, labels.emphasis), layout.contentWidth).forEach { drawWords(sheet, it) }
        }
        sheet.y += LetterLayout.GAP_MD
    }

    private fun emphasised(text: String, emphasis: String): List<TextRun> {
        if (emphasis.isBlank()) return listOf(TextRun(layout.bodyPaint, text))

        val runs = mutableListOf<TextRun>()
        var start = 0
        var match = text.indexOf(emphasis)

        while (match >= 0) {
            if (match > start) runs.add(TextRun(layout.bodyPaint, text.substring(start, match)))
            runs.add(TextRun(layout.boldPaint, emphasis))
            start = match + emphasis.length
            match = text.indexOf(emphasis, start)
        }
        if (start < text.length) runs.add(TextRun(layout.bodyPaint, text.substring(start)))
        return runs
    }

    private fun drawWords(sheet: LetterSheet, words: List<TextWord>) {
        val lineHeight = layout.lineHeight(layout.bodyPaint)

        sheet.ensure(lineHeight)
        val baseline = layout.baseline(sheet.y, layout.bodyPaint)
        var x = layout.contentLeft

        words.forEachIndexed { index, word ->
            if (index > 0) x += word.spaceWidth
            for (run in word.runs) {
                sheet.canvas.drawText(run.text, x, baseline, run.paint)
                x += run.width
            }
        }
        sheet.y += lineHeight
    }

    private fun drawSignature(sheet: LetterSheet, labels: LetterLabels) {
        val closing = listOf(labels.closing, labels.signOff).filter { it.isNotBlank() }
        val signatory = listOf(labels.signatoryTitle, labels.signatoryName, labels.signatoryPhone).filter { it.isNotBlank() }

        if (closing.isEmpty() && signatory.isEmpty()) return
        sheet.ensure((closing.size + signatory.size) * layout.lineHeight(layout.bodyPaint) + LetterLayout.SIGNATURE_SPACE)
        closing.forEach { drawWrapped(sheet, it, layout.bodyPaint, layout.contentLeft) }
        sheet.y += LetterLayout.SIGNATURE_SPACE
        signatory.forEach { drawWrapped(sheet, it, layout.bodyPaint, layout.contentLeft) }
    }

    private fun drawWrapped(sheet: LetterSheet, text: String, paint: Paint, x: Float) {
        if (text.isBlank()) return

        val height = layout.lineHeight(paint)

        for (line in layout.wrap(text, paint, layout.contentWidth)) {
            sheet.ensure(height)
            sheet.canvas.drawText(line, x, layout.baseline(sheet.y, paint), paint)
            sheet.y += height
        }
    }
}

private class LetterSheet(private val pageCount: Int, private val document: PdfDocument, private val layout: LetterLayout) {
    lateinit var canvas: Canvas

    var y: Float = 0f

    var number: Int = 0

    private var page: PdfDocument.Page? = null

    fun ensure(height: Float) {
        if (y + height > layout.contentBottom) newPage()
    }

    fun start() = newPage()

    fun newPage() {
        finish()
        number += 1
        val info = PdfDocument.PageInfo.Builder(LetterLayout.PAGE_WIDTH, LetterLayout.PAGE_HEIGHT, number).create()
        val started = document.startPage(info)

        page = started
        canvas = started.canvas
        y = layout.contentTop
    }

    fun finish() {
        val current = page ?: return

        if (pageCount > 1) current.canvas.drawText("Page $number of $pageCount", layout.contentRight, layout.footerBaseline, layout.footerPaint)
        document.finishPage(current)
        page = null
    }
}
