package com.example.bookster.states

data class SignInState(
    val isLoading: Boolean = false,
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)