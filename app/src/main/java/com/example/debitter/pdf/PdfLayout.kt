package com.example.debitter.pdf

import android.graphics.Paint
import android.graphics.Typeface
import kotlin.math.floor

class PdfLayout(val typefaces: PdfTypefaces) {
    companion object {
        const val AMOUNT_COLUMN_WIDTH: Float = 84f
        const val BAND_HEIGHT: Float = 15f
        const val CHARGE_ROW_HEIGHT: Float = 13.5f
        const val CHARGE_SIZE: Float = 8.5f
        const val COLON_OFFSET: Float = 84f
        const val COMPANY_DETAIL_SIZE: Float = 7.5f
        const val COMPANY_NAME_SIZE: Float = 15f
        const val GAP_MD: Float = 10f
        const val GAP_SM: Float = 6f
        const val GAP_XS: Float = 3f
        const val HEADER_LABEL_SIZE: Float = 8f
        const val HEADER_ROW_HEIGHT: Float = 13f
        const val HEADER_VALUE_SIZE: Float = 8f
        const val LABEL_GAP: Float = 10f
        const val MARGIN: Float = 28f
        const val ROW_INDENT: Float = 8f
        const val RULE_STRONG: Float = 1f
        const val RULE_THIN: Float = 0.6f
        const val SECTION_HEADING_SIZE: Float = 8f
        const val SIGNATURE_GAP: Float = 30f
        const val SIGNATURE_LINE_WIDTH: Float = 150f
        const val SIGNATURE_SIZE: Float = 8f
        const val TITLE_SIZE: Float = 16f
        const val TOTALS_LABEL_SIZE: Float = 8.5f
        const val TOTALS_ROW_HEIGHT: Float = 15f
        const val TOTALS_VALUE_SIZE: Float = 9.5f
        const val TRACKING_SECTION: Float = 0.09f

        const val PAGE_HEIGHT: Int = 595
        const val PAGE_WIDTH: Int = 420

        val BAND_COLOR: Int = 0xFFF2F2F2.toInt()
        val HAIRLINE_COLOR: Int = 0xFFBFBFBF.toInt()
        val INK_COLOR: Int = 0xFF111111.toInt()
        val INK_MUTED_COLOR: Int = 0xFF666666.toInt()
    }

    val amountPaint: Paint = textPaint(typefaces.regular, CHARGE_SIZE, INK_COLOR, Paint.Align.RIGHT)
    val bandPaint: Paint = fillPaint(BAND_COLOR)
    val chargeLabelPaint: Paint = textPaint(typefaces.regular, CHARGE_SIZE, INK_COLOR, Paint.Align.LEFT)
    val companyDetailPaint: Paint = textPaint(typefaces.regular, COMPANY_DETAIL_SIZE, INK_MUTED_COLOR, Paint.Align.CENTER)
    val companyNamePaint: Paint = textPaint(typefaces.displaySemiBold, COMPANY_NAME_SIZE, INK_COLOR, Paint.Align.CENTER)
    val headerLabelPaint: Paint = textPaint(typefaces.medium, HEADER_LABEL_SIZE, INK_MUTED_COLOR, Paint.Align.LEFT)
    val headerValuePaint: Paint = textPaint(typefaces.semiBold, HEADER_VALUE_SIZE, INK_COLOR, Paint.Align.LEFT)
    val rulePaint: Paint = strokePaint(HAIRLINE_COLOR, RULE_THIN)
    val ruleStrongPaint: Paint = strokePaint(INK_COLOR, RULE_STRONG)
    val sectionPaint: Paint = textPaint(typefaces.semiBold, SECTION_HEADING_SIZE, INK_COLOR, Paint.Align.LEFT, TRACKING_SECTION)
    val signaturePaint: Paint = textPaint(typefaces.regular, SIGNATURE_SIZE, INK_MUTED_COLOR, Paint.Align.CENTER)
    val titlePaint: Paint = textPaint(typefaces.displayBold, TITLE_SIZE, INK_COLOR, Paint.Align.CENTER)
    val totalsLabelPaint: Paint = textPaint(typefaces.semiBold, TOTALS_LABEL_SIZE, INK_COLOR, Paint.Align.RIGHT)
    val totalsValuePaint: Paint = textPaint(typefaces.semiBold, TOTALS_VALUE_SIZE, INK_COLOR, Paint.Align.RIGHT)
    val totalsValueStrongPaint: Paint = textPaint(typefaces.bold, TOTALS_VALUE_SIZE, INK_COLOR, Paint.Align.RIGHT)

    val amountLeft: Float get() = amountRight - AMOUNT_COLUMN_WIDTH
    val amountRight: Float get() = contentRight
    val chargeLabelLeft: Float get() = contentLeft + ROW_INDENT
    val chargeLabelWidth: Float get() = amountLeft - LABEL_GAP - chargeLabelLeft
    val colonX: Float get() = contentLeft + COLON_OFFSET
    val contentBottom: Float get() = PAGE_HEIGHT - MARGIN
    val contentLeft: Float get() = MARGIN
    val contentRight: Float get() = PAGE_WIDTH - MARGIN
    val contentTop: Float get() = MARGIN
    val headerValueLeft: Float get() = colonX + GAP_SM
    val headerValueWidth: Float get() = contentRight - headerValueLeft
    val pageCenterX: Float get() = PAGE_WIDTH / 2f
    val signatureBlockHeight: Float get() = SIGNATURE_GAP + GAP_SM + lineHeight(signaturePaint)

    fun baseline(top: Float, height: Float, paint: Paint): Float = blockBaseline(top, height, 1, paint)

    fun blockBaseline(top: Float, height: Float, count: Int, paint: Paint): Float {
        val metrics = paint.fontMetrics
        return top + (height - count * (metrics.descent - metrics.ascent)) / 2f - metrics.ascent
    }

    fun lineHeight(paint: Paint): Float {
        val metrics = paint.fontMetrics
        return metrics.descent - metrics.ascent
    }

    fun snap(value: Float): Float = floor(value) + 0.5f

    fun wrap(text: String, paint: Paint, maxWidth: Float): List<String> {
        if (text.isBlank()) return listOf("")
        if (paint.measureText(text) <= maxWidth) return listOf(text)

        val lines = mutableListOf<String>()
        var current = ""
        for (word in text.split(' ').filter { it.isNotEmpty() }) {
            val candidate = if (current.isEmpty()) word else "$current $word"
            if (paint.measureText(candidate) <= maxWidth) {
                current = candidate
                continue
            }
            if (current.isNotEmpty()) {
                lines += current
                current = ""
            }
            if (paint.measureText(word) <= maxWidth) {
                current = word
                continue
            }
            val chunks = breakLongWord(word, paint, maxWidth)
            lines += chunks.dropLast(1)
            current = chunks.last()
        }
        if (current.isNotEmpty()) lines += current
        return if (lines.isEmpty()) listOf("") else lines
    }

    private fun breakLongWord(word: String, paint: Paint, maxWidth: Float): List<String> {
        val chunks = mutableListOf<String>()
        var rest = word
        while (rest.isNotEmpty()) {
            val taken = paint.breakText(rest, true, maxWidth, null).coerceAtLeast(1)
            chunks += rest.substring(0, taken)
            rest = rest.substring(taken)
        }
        return chunks
    }

    private fun textPaint(typeface: Typeface, size: Float, color: Int, align: Paint.Align, tracking: Float = 0f): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        this.textAlign = align
        this.textSize = size
        this.typeface = typeface
        fontFeatureSettings = "'tnum'"
        letterSpacing = tracking
    }

    private fun strokePaint(color: Int, width: Float): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        this.strokeWidth = width
        style = Paint.Style.STROKE
    }

    private fun fillPaint(color: Int): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        style = Paint.Style.FILL
    }
}
