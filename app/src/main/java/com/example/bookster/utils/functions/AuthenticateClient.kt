package com.example.bookster.utils.functions

import android.content.Intent
import android.content.IntentSender
import com.example.bookster.states.SignInResult
import com.example.bookster.states.UserData
import com.example.bookster.utils.web_client_id
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class AuthenticateClient(
    private val oneTapClient: SignInClient
) {

    private val auth = FirebaseAuth.getInstance()

    suspend fun signIn(): IntentSender? {
        return try {
            val result = oneTapClient.beginSignIn(buildSignInRequest()).await()
            result?.pendingIntent?.intentSender
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
    }

    suspend fun getSignInWithIntent(intent: Intent): SignInResult {
        return try {
            val credential = oneTapClient.getSignInCredentialFromIntent(intent)
            val googleIdToken = credential.googleIdToken
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
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message
            )
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
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(web_client_id)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}
