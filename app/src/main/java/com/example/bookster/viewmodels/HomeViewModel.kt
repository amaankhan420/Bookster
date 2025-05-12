package com.example.bookster.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.bookster.utils.collection
import com.example.bookster.utils.document
import com.example.bookster.utils.genreNames
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel : ViewModel() {
    private val _genreNameList: MutableState<List<String>> = mutableStateOf(emptyList())

    val genreNamesList: MutableState<List<String>> get() = _genreNameList


    fun fetchGenresIfNeeded(db: FirebaseFirestore) {
        try {
            if (_genreNameList.value.isEmpty()) {
                db.collection(collection).document(document)
                    .get()
                    .addOnSuccessListener { doc ->
                        if (doc.exists()) {
                            val genres = doc.get(genreNames) as? List<String> ?: emptyList()
                            _genreNameList.value = genres
                        }
                    }
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error fetching genres: ${e.message}")
        }
    }
}
