package com.example.debitter.pdf

import android.graphics.Paint
import android.graphics.Typeface
import kotlin.math.floor

class TextRun(val paint: Paint, val text: String) {
    val width: Float get() = paint.measureText(text)
}

class TextWord(val runs: List<TextRun>) {
    val spaceWidth: Float get() = runs.first().paint.measureText(" ")
    val width: Float get() = runs.fold(0f) { total, run -> total + run.width }
}

class LetterLayout(val scale: Float, val typefaces: PdfTypefaces) {
    companion object {
        const val BODY_SIZE: Float = 11f
        const val COLON_GAP: Float = 12f
        const val FIT_STEP: Float = 0.02f
        const val GAP_MD: Float = 16f
        const val GAP_XS: Float = 3f
        const val LABEL_COLUMN_WIDTH: Float = 96f
        const val LETTERHEAD_DETAIL_SIZE: Float = 10f
        const val LETTERHEAD_NAME_SIZE: Float = 20f
        const val MARGIN: Float = 56f
        const val MIN_SCALE: Float = 0.5f
        const val RULE_STRONG: Float = 1f
        const val SIGNATURE_SPACE: Float = 40f
        const val TAGLINE_SIZE: Float = 13f

        const val PAGE_HEIGHT: Int = 842
        const val PAGE_WIDTH: Int = 595

        val HAIRLINE_COLOR: Int = 0xFFBFBFBF.toInt()
        val INK_COLOR: Int = 0xFF111111.toInt()
        val LETTERHEAD_COLOR: Int = 0xFF002060.toInt()
    }

    val bodySize: Float = BODY_SIZE * scale
    val gapMd: Float = GAP_MD * scale
    val gapXs: Float = GAP_XS * scale
    val letterheadDetailSize: Float = LETTERHEAD_DETAIL_SIZE * scale
    val letterheadNameSize: Float = LETTERHEAD_NAME_SIZE * scale
    val signatureSpace: Float = SIGNATURE_SPACE * scale
    val taglineSize: Float = TAGLINE_SIZE * scale

    val bodyPaint: Paint = textPaint(typefaces.regular, bodySize, INK_COLOR, Paint.Align.LEFT)
    val boldPaint: Paint = textPaint(typefaces.bold, bodySize, INK_COLOR, Paint.Align.LEFT)
    val letterheadDetailPaint: Paint = textPaint(typefaces.regular, letterheadDetailSize, INK_COLOR, Paint.Align.CENTER)
    val letterheadNamePaint: Paint = textPaint(typefaces.displayBold, letterheadNameSize, LETTERHEAD_COLOR, Paint.Align.CENTER)
    val ruleStrongPaint: Paint = strokePaint(HAIRLINE_COLOR, RULE_STRONG)
    val taglinePaint: Paint = textPaint(typefaces.bold, taglineSize, INK_COLOR, Paint.Align.CENTER)
    val titlePaint: Paint = textPaint(typefaces.bold, bodySize, INK_COLOR, Paint.Align.CENTER)

    private val lineHeights: Map<Paint, Float> by lazy {
        listOf(
            bodyPaint,
            boldPaint,
            letterheadDetailPaint,
            letterheadNamePaint,
            taglinePaint,
            titlePaint,
        ).associateWith { measureLineHeight(it) }
    }

    val colonX: Float get() = contentLeft + LABEL_COLUMN_WIDTH
    val contentBottom: Float get() = PAGE_HEIGHT - MARGIN
    val contentCenterX: Float get() = (contentLeft + contentRight) / 2f
    val contentHeight: Float get() = contentBottom - contentTop
    val contentLeft: Float get() = MARGIN
    val contentRight: Float get() = PAGE_WIDTH - MARGIN
    val contentTop: Float get() = MARGIN
    val contentWidth: Float get() = contentRight - contentLeft
    val labelWidth: Float get() = LABEL_COLUMN_WIDTH - COLON_GAP
    val valueLeft: Float get() = colonX + COLON_GAP
    val valueWidth: Float get() = contentRight - valueLeft

    fun baseline(top: Float, paint: Paint): Float = top - paint.fontMetrics.ascent

    fun lineHeight(paint: Paint): Float = lineHeights[paint] ?: measureLineHeight(paint)

    fun scaledTo(factor: Float): LetterLayout = LetterLayout(scale = factor, typefaces = typefaces)

    fun snap(value: Float): Float = floor(value) + 0.5f

    fun wrap(text: String, paint: Paint, maxWidth: Float): List<String> = wrapWords(listOf(TextRun(paint, text)), maxWidth).map { line -> line.joinToString(" ") { word -> word.runs.joinToString("") { it.text } } }

    fun wrapWords(runs: List<TextRun>, maxWidth: Float): List<List<TextWord>> {
        val lines = mutableListOf<List<TextWord>>()
        var current = mutableListOf<TextWord>()
        var width = 0f

        for (word in words(runs)) {
            val gap = if (current.isEmpty()) 0f else word.spaceWidth
            if (width + gap + word.width <= maxWidth) {
                current.add(word)
                width += gap + word.width
                continue
            }
            if (current.isNotEmpty()) {
                lines.add(current)
                current = mutableListOf()
            }
            if (word.width <= maxWidth || word.runs.size > 1) {
                current.add(word)
                width = word.width
                continue
            }
            val chunks = breakLongWord(word.runs.single(), maxWidth)
            chunks.dropLast(1).forEach { lines.add(listOf(TextWord(listOf(it)))) }
            current.add(TextWord(listOf(chunks.last())))
            width = chunks.last().width
        }
        if (current.isNotEmpty()) lines.add(current)
        return lines
    }

    private fun textPaint(typeface: Typeface, size: Float, color: Int, align: Paint.Align): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        this.textAlign = align
        this.textSize = size
        this.typeface = typeface
        fontFeatureSettings = "'tnum'"
    }

    private fun strokePaint(color: Int, width: Float): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        this.strokeWidth = width
        style = Paint.Style.STROKE
    }

    private fun words(runs: List<TextRun>): List<TextWord> {
        val words = mutableListOf<TextWord>()
        val pieces = mutableListOf<TextRun>()
        val builder = StringBuilder()

        for (run in runs) {
            for (char in run.text) {
                if (!char.isWhitespace()) {
                    builder.append(char)
                    continue
                }
                if (builder.isNotEmpty()) {
                    pieces.add(TextRun(run.paint, builder.toString()))
                    builder.clear()
                }
                if (pieces.isNotEmpty()) {
                    words.add(TextWord(pieces.toList()))
                    pieces.clear()
                }
            }
            if (builder.isNotEmpty()) {
                pieces.add(TextRun(run.paint, builder.toString()))
                builder.clear()
            }
        }
        if (pieces.isNotEmpty()) words.add(TextWord(pieces.toList()))
        return words
    }

    private fun breakLongWord(run: TextRun, maxWidth: Float): List<TextRun> {
        val chunks = mutableListOf<TextRun>()
        var rest = run.text
        while (rest.isNotEmpty()) {
            val taken = run.paint.breakText(rest, true, maxWidth, null).coerceAtLeast(1)
            chunks.add(TextRun(run.paint, rest.substring(0, taken)))
            rest = rest.substring(taken)
        }
        return chunks
    }

    private fun measureLineHeight(paint: Paint): Float {
        val metrics = paint.fontMetrics
        return metrics.descent - metrics.ascent
    }
}
