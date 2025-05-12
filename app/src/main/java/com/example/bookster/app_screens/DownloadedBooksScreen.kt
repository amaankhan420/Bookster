package com.example.bookster.app_screens

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookster.ui_components.TopBars
import com.example.bookster.utils.Routes
import com.example.bookster.viewmodels.DownloadedBooksScreenViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun DownloadedBooksScreen(
    navController: NavController,
    downloadedViewModel: DownloadedBooksScreenViewModel,
) {
    val context = LocalContext.current
    val downloadedBooks by downloadedViewModel.downloadedBooks.collectAsState()
    val showDialog = remember { mutableStateOf(false) }
    val selectedFile = remember { mutableStateOf<File?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        downloadedViewModel.loadDownloadedBooks(context = context)
    }

    Scaffold(
        topBar = {
            TopBars(
                heading = "Downloaded Books", navController = navController
            )
        }) { innerPadding ->
        if (downloadedBooks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Downloaded Books",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                items(items = downloadedBooks) { file ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .combinedClickable(onClick = {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    key = "file", value = file
                                )
                                navController.navigate(Routes.BookPDFReader.route)
                            }, onLongClick = {
                                selectedFile.value = file
                                showDialog.value = true
                            }),
                        elevation = CardDefaults.cardElevation(4.dp)) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = file.name.removeSuffix(".pdf"),
                                fontSize = 18.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "PDF",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        if (showDialog.value && selectedFile.value != null) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Delete Book") },
                text = { Text("Are you sure you want to delete '${selectedFile.value?.name}'?") },
                confirmButton = {
                    Button(onClick = {
                        coroutineScope.launch {
                            downloadedViewModel.deleteBook(context, selectedFile.value!!)
                            showDialog.value = false
                        }
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog.value = false }) {
                        Text("Cancel")
                    }
                })
        }
    }
}