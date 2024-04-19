package com.example.bookster.app_screens


import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookster.states.UserData
import com.example.bookster.ui.theme.Black
import com.example.bookster.ui_components.SearchBarComponent
import com.example.bookster.ui_components.Section
import com.example.bookster.utils.collection
import com.example.bookster.utils.document
import com.example.bookster.utils.genreNames
import com.example.bookster.viewmodels.HomeViewModel
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun HomeScreen(userData: UserData?, onSignOut: () -> Unit, navController: NavController) {
    val homeViewModel: HomeViewModel = viewModel()
    val genreNameList by rememberSaveable { homeViewModel.genreNamesList }
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(key1 = true) {
        if(genreNameList.isEmpty()) {
            db.collection(collection).document(document)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val genres = document.get(genreNames) as? List<String> ?: emptyList()
                        homeViewModel.setGenreNameList(genres)
                        Log.d("HomeScreen", "Fetched genres from Firestore {$genreNameList}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("HomeScreen", "Error fetching genres from Firestore", exception)
                }
        }
    }

    Scaffold (
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopBar(userData = userData, onSignOut, navController)
        }
    ) { values ->
        Column (
            modifier = Modifier
                .padding(values)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            SearchBarComponent()
            Spacer(modifier = Modifier.height(10.dp))

            genreNameList.forEach { genre ->
                Section(genre, navController)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(userData: UserData?, onSignOut: () -> Unit, navController: NavController) {
    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            actionIconContentColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.padding(top = 5.dp),
        title = {
            UserInfo(name = userData?.userName, url = userData?.profilePictureURL)
        },
        actions = {
            IconButton(
                onClick = {
                    //navController.navigate("")
                },
            ) {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = "History"
                )
            }

            IconButton(
                onClick = {
                    //navController.navigate("")
                },
            ) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite"
                )
            }
            IconButton(
                onClick = onSignOut,
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Sign Out"
                )
            }
        },
    )
}

@Composable
fun UserInfo(name: String?, url: String?) {
    Row (
        verticalAlignment = Alignment.CenterVertically
    ){
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .border(width = 2.dp, color = Black)
            )
        }

        Text(
            text = "$name",
            fontSize = 22.sp,
            fontFamily = FontFamily.Default,
            modifier = Modifier
                .padding(start = 5.dp)
        )
    }
}
