package com.example.bookster.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.bookster.data.DownloadPrefsManager
import com.example.bookster.utils.functions.DownloadBookPDF
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class DownloadedBooksScreenViewModel : ViewModel() {
    private val _downloadedBooks = MutableStateFlow<List<File>>(emptyList())
    val downloadedBooks: StateFlow<List<File>> = _downloadedBooks.asStateFlow()

    fun loadDownloadedBooks(context: Context) {
        try {
            _downloadedBooks.value = DownloadBookPDF.loadPdfs(context = context)
        } catch (e: Exception) {
            Log.e("DownloadedBooksScreenViewModel", "Error loading downloaded books $e")
        }
    }

    suspend fun deleteBook(context: Context, file: File) {
        try {
            val deleted = DownloadBookPDF.deletePdf(file)
            if (deleted) {
                _downloadedBooks.value = _downloadedBooks.value.filterNot { it.name == file.name }
                DownloadPrefsManager.removeDownloadedBook(context, file.name)
            } else {
                Log.e("DownloadedBooksScreenViewModel", "Failed to delete file: ${file.name}")
            }
        } catch (e: Exception) {
            Log.e("DownloadedBooksScreenViewModel", "Error deleting file: $e")
        }
    }
}