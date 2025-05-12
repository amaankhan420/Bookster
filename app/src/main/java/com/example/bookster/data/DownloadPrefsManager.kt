package com.example.bookster.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "downloaded_books")

object DownloadPrefsManager {

    private val DOWNLOADED_BOOKS_KEY = stringSetPreferencesKey("downloaded_books")

    suspend fun addDownloadedBook(context: Context, bookName: String) {
        context.dataStore.edit { prefs ->
            val currentSet = prefs[DOWNLOADED_BOOKS_KEY] ?: emptySet()
            prefs[DOWNLOADED_BOOKS_KEY] = currentSet + bookName
        }
    }

    private fun getDownloadedBooks(context: Context): Flow<Set<String>> {
        return context.dataStore.data.map { prefs ->
            prefs[DOWNLOADED_BOOKS_KEY] ?: emptySet()
        }
    }

    suspend fun isBookDownloaded(context: Context, bookName: String): Boolean {
        val downloadedBooks = getDownloadedBooks(context).first()
        return bookName in downloadedBooks
    }
    
    suspend fun removeDownloadedBook(context: Context, bookName: String) {
        context.dataStore.edit { prefs ->
            val currentSet = prefs[DOWNLOADED_BOOKS_KEY] ?: emptySet()
            prefs[DOWNLOADED_BOOKS_KEY] = currentSet - bookName
        }
    }
}
