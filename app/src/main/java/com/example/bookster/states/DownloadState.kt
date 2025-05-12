package com.example.bookster.states

sealed class DownloadState {
    data object Idle : DownloadState()
    data object Loading : DownloadState()
    data object Success : DownloadState()
    data class Error(val message: String) : DownloadState()
}