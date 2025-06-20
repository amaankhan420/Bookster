package com.example.bookster.viewmodels.sharedviewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.bookster.data.models.Book
import com.example.bookster.utils.author
import com.example.bookster.utils.cover
import com.example.bookster.utils.description
import com.example.bookster.utils.pdf
import com.example.bookster.utils.title
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SharedBookViewModel : ViewModel() {
    private val _booksList: MutableState<List<Book>> = mutableStateOf(emptyList())

    var booksList = _booksList

    fun setGenreNameList(books: List<Book>) {
        _booksList.value = books
    }

    suspend fun getBooksFromFirestore(collectionName: String): List<Book> =
        suspendCancellableCoroutine { continuation ->
            try {
                val books = mutableListOf<Book>()
                FirebaseFirestore.getInstance().collection(collectionName).get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val coverUrl = document.getString(cover)
                            val title = document.getString(title)
                            val description = document.getString(description)
                            val pdfUrl = document.getString(pdf)
                            val author = document.getString(author)
                            val book = Book(
                                collectionName,
                                document.id,
                                author,
                                coverUrl,
                                title,
                                description,
                                pdfUrl
                            )
                            books.add(book)
                        }

                        continuation.resume(books)
                    }.addOnFailureListener { exception ->
                        Log.w("SharedBookViewModel", "Error getting documents: ", exception)
                        continuation.resumeWithException(exception)
                    }
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
}
