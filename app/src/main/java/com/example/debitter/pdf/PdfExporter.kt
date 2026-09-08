package com.example.debitter.pdf

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.example.debitter.R
import com.example.debitter.model.DebitNote
import com.example.debitter.util.DateFormat
import java.io.File
import java.io.FileOutputStream
import java.time.Instant

class PdfExporter(private val context: Context) {
    companion object {
        const val DOWNLOAD_SUBDIRECTORY: String = "Debitter/Debit Notes"
        const val MIME_TYPE: String = "application/pdf"
        const val PROVIDER_SUFFIX: String = ".fileprovider"
        const val SHARE_DIRECTORY: String = "shared"
    }

    private val generator: DebitNotePdfGenerator by lazy { DebitNotePdfGenerator(PdfLayout(typefaces())) }

    fun render(note: DebitNote): ByteArray = generator.render(note)

    fun fileName(): String = "Debit-Note-${DateFormat.stamp(Instant.now())}.pdf"

    fun cacheFile(bytes: ByteArray, name: String): File {
        val directory = File(context.cacheDir, SHARE_DIRECTORY).apply { mkdirs() }
        val file = File(directory, name)

        file.writeBytes(bytes)
        return file
    }

    fun saveToDownloads(bytes: ByteArray, name: String): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) saveToMediaStore(bytes, name) else saveToAppDownloads(bytes, name)

    fun shareIntent(bytes: ByteArray, name: String): Intent {
        val uri = contentUri(cacheFile(bytes, name))

        return Intent(Intent.ACTION_SEND).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, name)
            type = MIME_TYPE
        }
    }

    fun print(bytes: ByteArray, name: String) {
        val manager = context.getSystemService(PrintManager::class.java) ?: return
        val attributes = PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A5).setMinMargins(PrintAttributes.Margins.NO_MARGINS).build()

        manager.print(name, BytesPrintAdapter(bytes, name), attributes)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveToMediaStore(bytes: ByteArray, name: String): String {
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, name)
            put(MediaStore.Downloads.IS_PENDING, 1)
            put(MediaStore.Downloads.MIME_TYPE, MIME_TYPE)
            put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/$DOWNLOAD_SUBDIRECTORY")
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return saveToAppDownloads(bytes, name)
        val written = resolver.openOutputStream(uri)?.use { it.write(bytes) }

        if (written == null) {
            resolver.delete(uri, null, null)
            return saveToAppDownloads(bytes, name)
        }
        values.clear()
        values.put(MediaStore.Downloads.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
        return "${Environment.DIRECTORY_DOWNLOADS}/$DOWNLOAD_SUBDIRECTORY/$name"
    }

    private fun saveToAppDownloads(bytes: ByteArray, name: String): String {
        val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir, DOWNLOAD_SUBDIRECTORY)
        val file = File(directory, name)

        file.parentFile?.mkdirs()
        file.writeBytes(bytes)
        return file.absolutePath
    }

    private fun contentUri(file: File): Uri = FileProvider.getUriForFile(context, "${context.packageName}$PROVIDER_SUFFIX", file)

    private fun typefaces(): PdfTypefaces = PdfTypefaces(
        bold = font(R.font.inter_bold),
        displayBold = font(R.font.inter_display_bold),
        displaySemiBold = font(R.font.inter_display_semibold),
        medium = font(R.font.inter_medium),
        regular = font(R.font.inter_regular),
        semiBold = font(R.font.inter_semibold),
    )

    private fun font(id: Int): Typeface = ResourcesCompat.getFont(context, id) ?: throw IllegalStateException("Unresolved font resource $id")
}

private class BytesPrintAdapter(private val bytes: ByteArray, private val name: String) : PrintDocumentAdapter() {
    override fun onLayout(oldAttributes: PrintAttributes?, newAttributes: PrintAttributes?, cancellationSignal: CancellationSignal?, callback: LayoutResultCallback?, extras: Bundle?) {
        if (cancellationSignal?.isCanceled == true) {
            callback?.onLayoutCancelled()
            return
        }
        val info = PrintDocumentInfo.Builder(name).setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build()

        callback?.onLayoutFinished(info, true)
    }

    override fun onWrite(pages: Array<out PageRange>?, destination: ParcelFileDescriptor?, cancellationSignal: CancellationSignal?, callback: WriteResultCallback?) {
        if (destination == null || callback == null) return
        if (cancellationSignal?.isCanceled == true) {
            callback.onWriteCancelled()
            return
        }
        FileOutputStream(destination.fileDescriptor).use { it.write(bytes) }
        callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
    }
}
