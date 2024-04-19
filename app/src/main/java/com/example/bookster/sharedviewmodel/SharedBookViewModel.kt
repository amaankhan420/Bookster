package com.example.bookster.sharedviewmodel

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.bookster.states.Book
import com.example.bookster.utils.cover
import com.example.bookster.utils.description
import com.example.bookster.utils.pdf
import com.example.bookster.utils.title
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SharedBookViewModel : ViewModel(), Parcelable {
    private val _booksList: MutableState<List<Book>> = mutableStateOf(emptyList())

    var booksList = _booksList

    fun setGenreNameList(books: List<Book>) {
        _booksList.value = books
    }

    suspend fun getBooksFromFirestore(collectionName: String): List<Book> = suspendCancellableCoroutine { continuation ->
        val books = mutableListOf<Book>()
        FirebaseFirestore.getInstance().collection(collectionName)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val coverUrl = document.getString(cover)
                    val title = document.getString(title)
                    val description = document.getString(description)
                    val pdfUrl = document.getString(pdf)
                    val book = Book(collectionName, document.id, coverUrl, title, description, pdfUrl)
                    books.add(book)
                }

                continuation.resume(books)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
                continuation.resumeWithException(exception)
            }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeList(booksList.value)
    }

    companion object CREATOR : Parcelable.Creator<SharedBookViewModel> {
        override fun createFromParcel(parcel: Parcel): SharedBookViewModel {
            val viewModel = SharedBookViewModel()
            val books = mutableListOf<Book>()
            parcel.readList(books, Book::class.java.classLoader)
            viewModel.setGenreNameList(books)
            return viewModel
        }

        override fun newArray(size: Int): Array<SharedBookViewModel?> {
            return arrayOfNulls(size)
        }
    }
}