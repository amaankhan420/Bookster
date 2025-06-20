package com.example.bookster.viewmodels

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import java.io.File
import java.io.IOException

class BookPdfReaderViewModel : ViewModel() {

    val totalPages = mutableIntStateOf(0)
    val currentPage = mutableIntStateOf(0)
    val scaleFactor = mutableFloatStateOf(1f)
    val isLoading = mutableStateOf(true)
    val currentBitmap = mutableStateOf<Bitmap?>(null)

    private var pdfRenderer: PdfRenderer? = null

    fun openPdf(file: File) {
        try {
            val parcelFileDescriptor =
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            pdfRenderer = PdfRenderer(parcelFileDescriptor)
            totalPages.intValue = pdfRenderer?.pageCount ?: 0
            loadPage(0)
            isLoading.value = false
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadPage(pageIndex: Int) {
        try {
            pdfRenderer?.let { renderer ->
                val page = renderer.openPage(pageIndex)
                val bitmap = createBitmap(page.width, page.height)
                val canvas = android.graphics.Canvas(bitmap)
                canvas.drawColor(android.graphics.Color.WHITE)

                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                currentBitmap.value = bitmap
                currentPage.intValue = pageIndex
                page.close()
            }
        } catch (e: Exception) {
            Log.e("BookPdfReaderViewModel", "Error loading page: $e")
        }
    }

    fun nextPage() {
        if (currentPage.intValue < totalPages.intValue - 1) {
            loadPage(currentPage.intValue + 1)
        }
    }

    fun previousPage() {
        if (currentPage.intValue > 0) {
            loadPage(currentPage.intValue - 1)
        }
    }

    fun resetScale() {
        scaleFactor.floatValue = 1f
    }

    override fun onCleared() {
        super.onCleared()
        pdfRenderer?.close()
    }
}