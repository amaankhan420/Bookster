package com.example.bookster.viewmodels

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookster.data.DownloadPrefsManager
import com.example.bookster.states.DownloadState
import com.example.bookster.utils.functions.DownloadBookPDF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class BookScreenViewModel : ViewModel() {
    private val _downloadState = mutableStateOf<DownloadState>(DownloadState.Idle)
    val downloadState: State<DownloadState> = _downloadState

    private val _isDownloaded = mutableStateOf(false)
    val isDownloaded: State<Boolean> = _isDownloaded

    private var _downloadedFile: File? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    fun downloadBookPdf(context: Context, url: String, fileName: String) {
        if (_downloadState.value is DownloadState.Loading) return

        _downloadState.value = DownloadState.Loading
        viewModelScope.launch {
            try {
                val downloadedFile = withContext(Dispatchers.IO) {
                    DownloadBookPDF.downloadPdfFromFirebaseSync(
                        context = context,
                        url = url,
                        fileName = "$fileName.pdf"
                    )

                    DownloadBookPDF.findPdfByName(context, fileName).also {
                        check(it?.exists() == true) { "Downloaded file not found" }
                    }
                }

                _downloadedFile = downloadedFile
                _isDownloaded.value = true

                _downloadState.value = DownloadState.Success
            } catch (e: Exception) {
                _isDownloaded.value = false
                _downloadState.value = DownloadState.Error(e.message ?: "Download failed")
            }
        }
    }

    fun checkIfBookDownloaded(context: Context, bookName: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val file = DownloadBookPDF.findPdfByName(context, bookName!!)
            val downloaded = file != null || DownloadPrefsManager.isBookDownloaded(context, bookName)

            _downloadedFile = file
            _isDownloaded.value = downloaded
        }
    }

    fun getDownloadedFile(context: Context, bookName: String): File? {
        return _downloadedFile ?: DownloadBookPDF.findPdfByName(context, bookName)
    }

    fun resetDownloadState() {
        _downloadState.value = DownloadState.Idle
    }
}