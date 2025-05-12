package com.example.bookster.app_screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookster.states.UserData
import com.example.bookster.ui_components.Section
import com.example.bookster.utils.Routes
import com.example.bookster.viewmodels.HomeViewModel
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun HomeScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    val genreNameList by rememberSaveable { homeViewModel.genreNamesList }

    LaunchedEffect(key1 = true) {
        homeViewModel.fetchGenresIfNeeded(FirebaseFirestore.getInstance())
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(), topBar = {
            TopBar(userData = userData, onSignOut, navController)
        }, containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            genreNameList.forEach { genre ->
                Section(type = genre, navController = navController)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    userData: UserData?, onSignOut: () -> Unit, navController: NavController
) {
    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            if (userData?.userName != null) {
                Text(
                    text = "Hi ${userData.userName.split(" ")[0]}",
                    fontSize = 22.sp,
                    fontFamily = FontFamily.Default,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    navController.navigate(Routes.Downloaded.route)
                }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Downloads"
                )
            }
            IconButton(onClick = onSignOut) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Sign Out"
                )
            }
        },
    )
}
