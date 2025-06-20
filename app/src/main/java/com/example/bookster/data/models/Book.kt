package com.example.bookster.data.models

import android.os.Parcel
import android.os.Parcelable

data class Book(
    val genre: String?,
    val id: String?,
    val author: String?,
    val coverUrl: String?,
    val title: String?,
    val description: String?,
    val pdfUrl: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(genre)
        parcel.writeString(id)
        parcel.writeString(author)
        parcel.writeString(coverUrl)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(pdfUrl)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }
}