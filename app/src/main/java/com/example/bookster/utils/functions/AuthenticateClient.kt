package com.example.bookster.utils.functions

import android.content.Intent
import android.content.IntentSender
import com.example.bookster.data.models.SignInResult
import com.example.bookster.data.models.UserData
import com.example.bookster.utils.web_client_id
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.CancellationException

class AuthenticateClient(
    private val oneTapClient: SignInClient
) {
    private val auth = FirebaseAuth.getInstance()

    suspend fun signIn(): IntentSender? {
        return try {
            // Add timeout to prevent hanging
            withTimeoutOrNull(10_000) {
                val result = oneTapClient.beginSignIn(buildSignInRequest()).await()
                result?.pendingIntent?.intentSender
            } ?: throw Exception("Sign-in request timed out")
        } catch (e: Exception) {
            e.printStackTrace()
            when (e) {
                is CancellationException -> throw e
                is FirebaseNetworkException -> throw Exception("No internet connection")
                is FirebaseAuthException -> throw Exception("Authentication failed: ${e.message}")
                else -> null
            }
        }
    }

    suspend fun getSignInWithIntent(intent: Intent): SignInResult {
        return try {
            withTimeoutOrNull(10_000) {
                val credential = oneTapClient.getSignInCredentialFromIntent(intent)
                val googleIdToken = credential.googleIdToken ?: throw Exception("Invalid Google ID token")
                val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
                val user = auth.signInWithCredential(googleCredentials).await().user

                SignInResult(
                    data = user?.run {
                        UserData(
                            userId = uid,
                            userName = displayName,
                            profilePictureURL = photoUrl?.toString()
                        )
                    },
                    errorMessage = null
                )
            } ?: SignInResult(
                data = null,
                errorMessage = "Sign-in timed out. Please try again."
            )
        } catch (e: Exception) {
            e.printStackTrace()
            when (e) {
                is CancellationException -> throw e
                is FirebaseNetworkException -> SignInResult(
                    data = null,
                    errorMessage = "No internet connection. Please check your network."
                )
                is FirebaseAuthException -> SignInResult(
                    data = null,
                    errorMessage = "Authentication failed: ${e.message}"
                )
                else -> SignInResult(
                    data = null,
                    errorMessage = e.message ?: "An unknown error occurred"
                )
            }
        }
    }

    fun getSignedInUser(): UserData? {
        return try {
            auth.currentUser?.run {
                UserData(
                    userId = uid,
                    userName = displayName,
                    profilePictureURL = photoUrl?.toString()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun signOut() {
        try {
            withTimeoutOrNull(5_000) {
                oneTapClient.signOut().await()
                auth.signOut()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(web_client_id)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}