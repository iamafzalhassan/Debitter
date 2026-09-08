package com.example.debitter.pdf

import android.graphics.Paint
import android.graphics.Typeface
import kotlin.math.floor

class PdfLayout(val typefaces: PdfTypefaces) {
    companion object {
        const val AMOUNT_COLUMN_WIDTH: Float = 84f
        const val BAND_HEIGHT: Float = 16f
        const val BODY_SIZE: Float = 9f
        const val CELL_PAD_X: Float = 5f
        const val CHARGE_ROW_HEIGHT: Float = 18f
        const val COLON_OFFSET: Float = 76f
        const val COMPANY_NAME_SIZE: Float = 14f
        const val FOOTER_HEIGHT: Float = 15f
        const val FOOTER_SIZE: Float = 7.5f
        const val GAP_MD: Float = 10f
        const val GAP_SM: Float = 6f
        const val GAP_XS: Float = 3f
        const val HEADER_COLUMN_GAP: Float = 12f
        const val HEADER_ROW_HEIGHT: Float = 14f
        const val LABEL_SIZE: Float = 7.5f
        const val MARGIN: Float = 24f
        const val META_SIZE: Float = 8.5f
        const val RULE_STRONG: Float = 1f
        const val RULE_THIN: Float = 0.75f
        const val SIGNATURE_SPACE: Float = 22f
        const val SIGNATURE_WIDTH: Float = 130f
        const val TOTALS_ROW_HEIGHT: Float = 18f
        const val TRACKING_LABEL: Float = 0.08f

        const val PAGE_HEIGHT: Int = 595
        const val PAGE_WIDTH: Int = 420

        val BAND_COLOR: Int = 0xFFF2F2F2.toInt()
        val HAIRLINE_COLOR: Int = 0xFFBFBFBF.toInt()
        val INK_COLOR: Int = 0xFF111111.toInt()
        val INK_MUTED_COLOR: Int = 0xFF666666.toInt()
    }

    val amountPaint: Paint = textPaint(typefaces.semiBold, BODY_SIZE, INK_COLOR, Paint.Align.RIGHT)
    val bandPaint: Paint = fillPaint(BAND_COLOR)
    val bodyPaint: Paint = textPaint(typefaces.regular, BODY_SIZE, INK_COLOR, Paint.Align.LEFT)
    val companyDetailPaint: Paint = textPaint(typefaces.regular, META_SIZE, INK_COLOR, Paint.Align.LEFT)
    val companyNamePaint: Paint = textPaint(typefaces.displayBold, COMPANY_NAME_SIZE, INK_COLOR, Paint.Align.LEFT)
    val footerPaint: Paint = textPaint(typefaces.regular, FOOTER_SIZE, INK_MUTED_COLOR, Paint.Align.RIGHT)
    val labelPaint: Paint = textPaint(typefaces.semiBold, LABEL_SIZE, INK_MUTED_COLOR, Paint.Align.LEFT, TRACKING_LABEL)
    val metaStrongPaint: Paint = textPaint(typefaces.semiBold, META_SIZE, INK_COLOR, Paint.Align.LEFT)
    val rulePaint: Paint = strokePaint(HAIRLINE_COLOR, RULE_THIN)
    val ruleStrongPaint: Paint = strokePaint(HAIRLINE_COLOR, RULE_STRONG)
    val signaturePaint: Paint = textPaint(typefaces.semiBold, LABEL_SIZE, INK_MUTED_COLOR, Paint.Align.CENTER, TRACKING_LABEL)
    val titlePaint: Paint = textPaint(typefaces.displayBold, COMPANY_NAME_SIZE, INK_COLOR, Paint.Align.CENTER)
    val totalsValueBoldPaint: Paint = textPaint(typefaces.bold, BODY_SIZE, INK_COLOR, Paint.Align.RIGHT)
    val totalsValuePaint: Paint = textPaint(typefaces.regular, BODY_SIZE, INK_COLOR, Paint.Align.RIGHT)

    private val lineHeights: Map<Paint, Float> by lazy { listOf(amountPaint, bodyPaint, companyDetailPaint, companyNamePaint, footerPaint, labelPaint, metaStrongPaint, signaturePaint, titlePaint, totalsValueBoldPaint, totalsValuePaint).associateWith { measureLineHeight(it) } }

    val amountLeft: Float get() = contentRight - AMOUNT_COLUMN_WIDTH
    val amountRight: Float get() = contentRight - CELL_PAD_X
    val cellLeft: Float get() = contentLeft + CELL_PAD_X
    val chargeLabelWidth: Float get() = amountLeft - cellLeft - CELL_PAD_X
    val contentBottom: Float get() = PAGE_HEIGHT - MARGIN - FOOTER_HEIGHT
    val contentCenterX: Float get() = (contentLeft + contentRight) / 2f
    val contentLeft: Float get() = MARGIN
    val contentRight: Float get() = PAGE_WIDTH - MARGIN
    val contentTop: Float get() = MARGIN
    val footerBaseline: Float get() = PAGE_HEIGHT - MARGIN - footerPaint.fontMetrics.descent
    val headerColumnWidth: Float get() = (contentRight - contentLeft - HEADER_COLUMN_GAP) / 2f
    val headerRightX: Float get() = contentLeft + headerColumnWidth + HEADER_COLUMN_GAP
    val headerValueWidth: Float get() = headerColumnWidth - COLON_OFFSET - GAP_SM
    val signatureBlockHeight: Float get() = SIGNATURE_SPACE + RULE_STRONG + GAP_XS + lineHeight(signaturePaint)
    val signatureCenterX: Float get() = signatureLeft + SIGNATURE_WIDTH / 2f
    val signatureLeft: Float get() = contentRight - SIGNATURE_WIDTH

    fun baseline(top: Float, height: Float, paint: Paint): Float = blockBaseline(top, height, 1, paint)

    fun blockBaseline(top: Float, height: Float, count: Int, paint: Paint): Float {
        val metrics = paint.fontMetrics
        return top + (height - count * (metrics.descent - metrics.ascent)) / 2f - metrics.ascent
    }

    fun lineHeight(paint: Paint): Float = lineHeights[paint] ?: measureLineHeight(paint)

    fun snap(value: Float): Float = floor(value) + 0.5f

    fun colonX(columnX: Float): Float = columnX + COLON_OFFSET

    fun headerValueLeft(columnX: Float): Float = columnX + COLON_OFFSET + GAP_SM

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

    private fun measureLineHeight(paint: Paint): Float {
        val metrics = paint.fontMetrics
        return metrics.descent - metrics.ascent
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
