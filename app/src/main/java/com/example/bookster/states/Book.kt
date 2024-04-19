package com.example.bookster.states

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val genre: String?,
    val id: String?,
    val coverUrl: String?,
    val title: String?,
    val description: String?,
    val pdfUrl: String?
) : Parcelable