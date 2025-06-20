package com.example.bookster.app_screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.bookster.ui_components.Section
import com.example.bookster.utils.Routes
import com.example.bookster.viewmodels.HomeViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun HomeScreen(
    onSignOut: () -> Unit,
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    val genreNameList by rememberSaveable { homeViewModel.genreNamesList }

    LaunchedEffect(key1 = true) {
        homeViewModel.fetchGenresIfNeeded(FirebaseFirestore.getInstance())
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(onSignOut, navController)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 10.dp)
        ) {
            items(genreNameList.size) { genre ->
                Section(
                    type = genreNameList[genre],
                    navController = navController
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onSignOut: () -> Unit,
    navController: NavController
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.secondary,
            navigationIconContentColor = MaterialTheme.colorScheme.secondary
        ),
        title = {
            Text(
                text = "Welcome Reader",
                fontSize = 22.sp,
                fontFamily = FontFamily.Default,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(start = 5.dp)
            )
        },
        actions = {
            IconButton(
                onClick = {
                    navController.navigate(Routes.Downloaded.route)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Downloads"
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