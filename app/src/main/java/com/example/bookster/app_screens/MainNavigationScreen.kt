package com.example.bookster.app_screens

import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookster.states.Book
import com.example.bookster.utils.Routes
import com.example.bookster.utils.functions.AuthenticateClient
import com.example.bookster.viewmodels.BookPdfReaderViewModel
import com.example.bookster.viewmodels.BookScreenViewModel
import com.example.bookster.viewmodels.DownloadedBooksScreenViewModel
import com.example.bookster.viewmodels.HomeViewModel
import com.example.bookster.viewmodels.SignInViewModel
import com.example.bookster.viewmodels.sharedviewmodel.SharedBookViewModel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch
import java.io.File

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainNavigationScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val lifecycleOwner = LocalLifecycleOwner.current

    val sharedBookViewModel = viewModel<SharedBookViewModel>()


    val authenticateClient = remember {
        AuthenticateClient(
            oneTapClient = Identity.getSignInClient(context)
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val startDestination =
            if (authenticateClient.getSignedInUser() != null) Routes.Home.route else Routes.SignIn.route

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            // Sign In Screen
            composable(route = Routes.SignIn.route) {
                val signInViewModel = viewModel<SignInViewModel>()
                val state by signInViewModel.state.collectAsState()

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                    onResult = { result ->
                        if (result.resultCode == Activity.RESULT_OK) {
                            lifecycleOwner.lifecycleScope.launch {
                                val signInResult = authenticateClient.getSignInWithIntent(
                                    intent = result.data ?: return@launch
                                )
                                signInViewModel.onSignInResult(signInResult)
                            }
                        }
                    }
                )

                LaunchedEffect(state.isSignInSuccessful) {
                    if (state.isSignInSuccessful) {
                        Toast.makeText(context, "Signed in", Toast.LENGTH_LONG).show()
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.SignIn.route) { inclusive = true }
                        }
                        signInViewModel.resetState()
                    }
                }

                SignInScreen(
                    state = state,
                    userData = authenticateClient.getSignedInUser(),
                    viewModel = signInViewModel,
                    onSignInClick = {
                        lifecycleOwner.lifecycleScope.launch {
                            val signIn = authenticateClient.signIn()
                            launcher.launch(
                                IntentSenderRequest.Builder(signIn ?: return@launch).build()
                            )
                        }
                    }
                )
            }

            // Home Screen
            composable(route = Routes.Home.route) {
                val homeViewModel = viewModel<HomeViewModel>()
                HomeScreen(
                    userData = authenticateClient.getSignedInUser(),
                    onSignOut = {
                        lifecycleOwner.lifecycleScope.launch {
                            authenticateClient.signOut()
                            Toast.makeText(context, "Signed Out", Toast.LENGTH_LONG).show()
                            navController.navigate(Routes.SignIn.route) {
                                popUpTo(Routes.Home.route) { inclusive = true }
                            }
                        }
                    },
                    navController = navController,
                    homeViewModel = homeViewModel
                )
            }

            // Category Screen
            composable(route = Routes.Category.route) {
                val categoryName = navController
                    .previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<String>("category")


                if (categoryName != null) {
                    CategoryScreen(
                        sharedBookViewModel = sharedBookViewModel,
                        navController = navController,
                        categoryName = categoryName
                    )
                }
            }

            // Book Screen
            composable(route = Routes.Book.route) {
                val bookScreenViewModel = viewModel<BookScreenViewModel>()
                val book = navController
                    .previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<Book>("book")

                if (book != null) {
                    BookScreen(
                        bookScreenViewModel = bookScreenViewModel,
                        navController = navController,
                        book = book
                    )
                }
            }

            // Downloaded Screen
            composable(route = Routes.Downloaded.route) {
                val downloadedViewModel = viewModel<DownloadedBooksScreenViewModel>()

                DownloadedBooksScreen(
                    downloadedViewModel = downloadedViewModel,
                    navController = navController
                )
            }

            // Book PDF Reader Screen
            composable(route = Routes.BookPDFReader.route) {
                val bookPdfReaderViewModel = viewModel<BookPdfReaderViewModel>()
                val file = navController
                    .previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<File>("file")

                if (file != null) {
                    BookPDFReaderScreen(
                        pdfFile = file,
                        navController = navController,
                        bookPdfReaderViewModel = bookPdfReaderViewModel
                    )
                }
            }
        }
    }
}