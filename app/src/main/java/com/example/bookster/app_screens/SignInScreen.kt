package com.example.bookster.app_screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookster.R
import com.example.bookster.states.SignInState
import com.example.bookster.states.UserData
import com.example.bookster.ui.theme.Black

@Composable
fun SignInScreen(
    state: SignInState,
    userData: UserData?,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF3A5368), Black),
                    start = Offset.Zero,
                    end = Offset.Infinite,
                )
            )
    ){
        Text(
            color = Color.White,
            text = "Welcome to Bookster",
            fontSize = 40.sp,
            fontFamily = FontFamily.Cursive,
            textAlign = TextAlign.Center
        )

        if (userData == null) {
            Spacer(modifier = Modifier.height(50.dp))
            OutlinedButton(
                onClick = onSignInClick,
            ) {
                Text(
                    color = Color.White,
                    text = "Sign In With "
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_google_logo),
                    contentDescription = "Google Logo",
                    modifier = Modifier
                        .size(24.dp)
                )
            }
        }
    }
}