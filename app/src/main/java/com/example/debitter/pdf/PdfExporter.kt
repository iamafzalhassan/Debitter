package com.example.debitter.pdf

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.example.debitter.R
import com.example.debitter.model.DebitNote
import com.example.debitter.model.PrintDocument
import com.example.debitter.model.RefundLetter
import com.example.debitter.util.DateFormat
import java.io.File
import java.time.Instant
import java.util.concurrent.TimeUnit

class PdfExporter(private val context: Context) {
    companion object {
        const val CACHE_RETENTION_HOURS: Long = 24

        const val DOWNLOAD_ROOT: String = "Debitter"
        const val MIME_TYPE: String = "application/pdf"
        const val PROVIDER_SUFFIX: String = ".fileprovider"
        const val SHARE_DIRECTORY: String = "shared"
    }

    private val noteGenerator: DebitNotePdfGenerator by lazy { DebitNotePdfGenerator(PdfLayout(typefaces)) }

    private val typefaces: PdfTypefaces by lazy { resolveTypefaces() }

    private val letterGenerator: RefundLetterPdfGenerator by lazy { RefundLetterPdfGenerator(LetterLayout(typefaces)) }

    fun render(document: PrintDocument): ByteArray = when (document) {
        is DebitNote -> noteGenerator.render(document)
        is RefundLetter -> letterGenerator.render(document)
    }

    fun saveToDownloads(bytes: ByteArray, document: PrintDocument): SaveLocation {
        val directory = "$DOWNLOAD_ROOT/${document.kind.directory}"
        val name = fileName(document)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) saveToMediaStore(bytes, directory, name) else saveToAppDownloads(bytes, directory, name)
    }

    fun shareIntent(bytes: ByteArray, document: PrintDocument): Intent {
        val name = fileName(document)
        val uri = contentUri(cacheFile(bytes, name))

        return Intent(Intent.ACTION_SEND).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, name)
            type = MIME_TYPE
        }
    }

    fun cacheFile(bytes: ByteArray, name: String): File {
        val directory = File(context.cacheDir, SHARE_DIRECTORY).apply { mkdirs() }
        val file = File(directory, name)

        pruneCache(directory, file)
        file.writeBytes(bytes)
        return file
    }

    private fun resolveTypefaces(): PdfTypefaces = PdfTypefaces(
        bold = font(R.font.inter_bold),
        displayBold = font(R.font.inter_display_bold),
        regular = font(R.font.inter_regular),
        semiBold = font(R.font.inter_semibold),
    )

    private fun font(id: Int): Typeface = ResourcesCompat.getFont(context, id) ?: throw IllegalStateException("Unresolved font resource $id")

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveToMediaStore(bytes: ByteArray, directory: String, name: String): SaveLocation {
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, name)
            put(MediaStore.Downloads.IS_PENDING, 1)
            put(MediaStore.Downloads.MIME_TYPE, MIME_TYPE)
            put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/$directory")
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return saveToAppDownloads(bytes, directory, name)
        val written = runCatching { resolver.openOutputStream(uri)?.use { it.write(bytes) } }.getOrNull()

        if (written == null) {
            resolver.delete(uri, null, null)
            return saveToAppDownloads(bytes, directory, name)
        }
        values.clear()
        values.put(MediaStore.Downloads.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
        return SaveLocation(isShared = true, path = "${Environment.DIRECTORY_DOWNLOADS}/$directory/$name")
    }

    private fun saveToAppDownloads(bytes: ByteArray, directory: String, name: String): SaveLocation {
        val folder = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir, directory)
        val file = File(folder, name)

        file.parentFile?.mkdirs()
        file.writeBytes(bytes)
        return SaveLocation(isShared = false, path = file.absolutePath)
    }

    private fun fileName(document: PrintDocument): String = "${document.kind.filePrefix}-${DateFormat.stamp(Instant.now())}.pdf"

    private fun contentUri(file: File): Uri = FileProvider.getUriForFile(context, "${context.packageName}$PROVIDER_SUFFIX", file)

    private fun pruneCache(directory: File, keep: File) {
        val cutoff = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(CACHE_RETENTION_HOURS)

        directory.listFiles()?.forEach { if (it != keep && it.lastModified() < cutoff) it.delete() }
    }
}
