package com.example.bookster.app_screens

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookster.R
import com.example.bookster.data.models.SignInState
import com.example.bookster.data.models.UserData
import com.example.bookster.viewmodels.SignInViewModel

@Composable
fun SignInScreen(
    state: SignInState,
    userData: UserData?,
    viewModel: SignInViewModel,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            showToast(context, error)
            viewModel.resetState()
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        AppTitle()

        when {
            state.isLoading -> LoadingIndicator()
            userData == null -> {
                SignInButton(
                    onSignInClick = {
                        if (isNetworkAvailable(context)) {
                            viewModel.startSignIn()
                            onSignInClick()
                        } else {
                            showToast(context, "No internet connection. Please try again.")
                        }
                    }
                )
            }

            else -> {
                Text(
                    text = "Welcome, ${userData.userName}!",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AppTitle() {
    Text(
        text = "Welcome to Bookster",
        fontSize = 40.sp,
        lineHeight = 48.sp,
        fontFamily = FontFamily.Cursive,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun LoadingIndicator() {
    Spacer(modifier = Modifier.height(50.dp))
    CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
}

@Composable
private fun SignInButton(onSignInClick: () -> Unit) {
    Spacer(modifier = Modifier.height(50.dp))
    OutlinedButton(
        onClick = onSignInClick,
        modifier = Modifier
            .height(50.dp)
            .padding(horizontal = 16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Text(
            text = "Sign In With ",
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_google_logo),
            contentDescription = "Google Logo",
            modifier = Modifier.size(20.dp)
        )
    }
}

private fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

private fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}