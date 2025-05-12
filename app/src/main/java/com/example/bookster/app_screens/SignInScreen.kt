package com.example.bookster.app_screens

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
import com.example.bookster.states.SignInState
import com.example.bookster.states.UserData
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
            userData == null -> SignInButton(
                onSignInClick = {
                    viewModel.startSignIn()
                    onSignInClick()
                }
            )
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
            modifier = Modifier.size(24.dp)
        )
    }
}

private fun showToast(context: android.content.Context, message: String) {

    android.widget.Toast.makeText(
        context,
        message,
        android.widget.Toast.LENGTH_LONG
    ).show()
}

