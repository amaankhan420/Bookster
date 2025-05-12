package com.example.bookster.utils

sealed class Routes(val route: String) {
    data object SignIn: Routes("sign_in")
    data object Home: Routes("home")
    data object Category: Routes("category_page")
    data object Book: Routes("book_screen")
    data object Downloaded: Routes("downloaded_screen")
    data object BookPDFReader: Routes("pdf_screen")
}