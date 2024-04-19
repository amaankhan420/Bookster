package com.example.bookster.utils

sealed class Routes(val route: String) {
    object SignIn: Routes("sign_in")
    object Home: Routes("home")
    object Category: Routes("category_page")
    object Book: Routes("book_screen")
}