package com.example.bookster.app_screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookster.sharedviewmodel.SharedBookViewModel
import com.example.bookster.states.Book
import com.example.bookster.utils.Routes
import com.example.bookster.utils.functions.AuthenticateClient
import com.example.bookster.viewmodels.SignInViewModel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainNavigationScreen(activity: Activity) {

    val authenticateClient by lazy {
        AuthenticateClient(
            oneTapClient = Identity.getSignInClient(activity)
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val navController = rememberNavController()
        LaunchedEffect(key1 = Unit) {
            delay(1000)
            if (authenticateClient.getSignedInUser() != null) {
                navController.navigate("home") {
                    popUpTo("sign_in") {
                        inclusive = true
                    }
                }
            }
        }

        NavHost(navController = navController, startDestination = Routes.SignIn.route) {
            composable(route = Routes.SignIn.route) {
                val viewModel = viewModel<SignInViewModel>()
                val state by viewModel.state.collectAsState()
                val lifecycleOwner = LocalLifecycleOwner.current

                val launcher =  rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                    onResult = { result ->
                        if (result.resultCode == ComponentActivity.RESULT_OK) {
                            lifecycleOwner.lifecycleScope.launch {
                                val signInResult = authenticateClient.getSignInWithIntent(
                                    intent = result.data ?: return@launch
                                )
                                viewModel.onSignInResult(signInResult)
                            }
                        }
                    }
                )

                LaunchedEffect(key1 = state.isSignInSuccessful) {
                    if (state.isSignInSuccessful) {
                        Toast.makeText(
                            activity,
                            "Signed in",
                            Toast.LENGTH_LONG
                        ).show()
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.SignIn.route) {
                                inclusive = true
                            }
                        }
                        viewModel.resetState()
                    }
                }

                SignInScreen(state = state, userData = authenticateClient.getSignedInUser(),onSignInClick = {
                    lifecycleOwner.lifecycleScope.launch {
                        val signIn = authenticateClient.signIn()
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                signIn ?: return@launch
                            ).build()
                        )
                    }
                })
            }

            composable(route = Routes.Home.route) {
                val lifecycleOwner = LocalLifecycleOwner.current
                HomeScreen(
                    userData = authenticateClient.getSignedInUser(),
                    onSignOut = {
                        lifecycleOwner.lifecycleScope.launch {
                            authenticateClient.signOut()
                            Toast.makeText(
                                activity,
                                "Signed Out",
                                Toast.LENGTH_LONG
                            ).show()
                            navController.navigate("sign_in") {
                                popUpTo("home") {
                                    inclusive = true
                                }
                            }
                        }
                    },
                    navController = navController
                )
            }

            composable(route = Routes.Category.route) {
                val sharedBookViewModel = navController
                    .previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<SharedBookViewModel>("data")
                if (sharedBookViewModel != null) {
                    CategoryScreen(sharedBookViewModel, navController = navController)
                }
            }

            composable(route = Routes.Book.route) {
                val book = navController
                    .previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<Book>("book")
                if (book != null) {
                    BookScreen(book = book, navController = navController)
                }
            }
        }
    }
}