package com.example.bookster.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.bookster.states.SignInResult
import com.example.bookster.states.SignInState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        try {
            _state.update {
                it.copy(
                    isLoading = false,
                    isSignInSuccessful = result.data != null,
                    signInError = result.errorMessage
                )
            }
        } catch (e: Exception) {
            Log.e("SignInViewModel", "Error updating state: $e")
        }
    }

    fun resetState() {
        try {
            _state.update { SignInState() }
        } catch (e: Exception) {
            Log.e("SignInViewModel", "Error resetting state: $e")
        }
    }

    fun startSignIn() {
        try {
            _state.update { it.copy(isLoading = true) }
        } catch (e: Exception) {
            Log.e("SignInViewModel", "Error in signin start: $e")
        }
    }
}