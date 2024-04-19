package com.example.bookster.states

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)