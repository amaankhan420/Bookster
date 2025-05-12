package com.example.bookster.app_screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.bookster.states.Book
import com.example.bookster.states.DownloadState
import com.example.bookster.ui_components.TopBars
import com.example.bookster.utils.Routes
import com.example.bookster.viewmodels.BookScreenViewModel

@Composable
fun BookScreen(
    bookScreenViewModel: BookScreenViewModel,
    navController: NavController,
    book: Book
) {
    val context = LocalContext.current
    val downloadState by bookScreenViewModel.downloadState
    val isDownloaded by bookScreenViewModel.isDownloaded

    // Check download status when screen loads or book changes
    LaunchedEffect(book.title) {
        bookScreenViewModel.checkIfBookDownloaded(context, book.title)
    }

    // Handle download state changes
    LaunchedEffect(downloadState) {
        when (downloadState) {
            is DownloadState.Success -> {
                Toast.makeText(context, "Download completed", Toast.LENGTH_SHORT).show()
                bookScreenViewModel.resetDownloadState()
            }
            is DownloadState.Error -> {
                Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
                bookScreenViewModel.resetDownloadState()
            }
            else -> {}
        }
    }

    BookDetailContent(book, navController, bookScreenViewModel, isDownloaded)
}

@Composable
private fun BookDetailContent(
    book: Book,
    navController: NavController,
    bookScreenViewModel: BookScreenViewModel,
    isDownloaded: Boolean
) {
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBars(
                heading = book.title ?: "Book Details",
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BookCoverImage(book.coverUrl)

            val isLoading = bookScreenViewModel.downloadState.value is DownloadState.Loading

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else {
                OutlinedButton(
                    onClick = {
                        if (isDownloaded) {
                            bookScreenViewModel.getDownloadedFile(context, book.title ?: "")?.let { file ->
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    key = "file",
                                    value = file
                                )
                                navController.navigate(Routes.BookPDFReader.route)
                            } ?: run {
                                Toast.makeText(
                                    context,
                                    "Cannot find the downloaded file",
                                    Toast.LENGTH_SHORT
                                ).show()
                                bookScreenViewModel.checkIfBookDownloaded(context, book.title)
                            }
                        } else {
                            book.pdfUrl?.let { url ->
                                bookScreenViewModel.downloadBookPdf(
                                    context = context,
                                    url = url,
                                    fileName = book.title ?: book.hashCode().toString()
                                )
                            }
                        }
                    },
                    enabled = book.pdfUrl != null,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .height(50.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = if (isDownloaded) "Read" else "Download",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            BookDescription(book.description)
        }
    }
}

@Composable
private fun BookCoverImage(coverUrl: String?) {
    val painter = rememberAsyncImagePainter(coverUrl)

    Image(
        painter = painter,
        contentDescription = "Book cover",
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp)
            .padding(horizontal = 16.dp),
        contentScale = ContentScale.Crop
    )

    if (painter.state is AsyncImagePainter.State.Error) {
        Text(
            text = "Failed to load cover image",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(8.dp)
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
private fun BookDescription(description: String?) {
    if (!description.isNullOrBlank()) {
        Text(
            text = description,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Justify,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )
    }
}