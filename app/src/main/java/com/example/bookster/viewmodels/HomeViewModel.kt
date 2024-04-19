package com.example.bookster.viewmodels
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val _genreNameList: MutableState<List<String>> = mutableStateOf(emptyList())

    var genreNamesList = _genreNameList

    fun setGenreNameList(genres: List<String>) {
        _genreNameList.value = genres
    }
}
