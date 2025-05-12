package com.example.bookster.app_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bookster.states.Book
import com.example.bookster.ui_components.Cards
import com.example.bookster.ui_components.TopBars
import com.example.bookster.viewmodels.sharedviewmodel.SharedBookViewModel

@Composable
fun CategoryScreen(
    sharedBookViewModel: SharedBookViewModel,
    navController: NavController,
    categoryName: String?
) {
    val bookList by sharedBookViewModel.booksList

    LaunchedEffect(categoryName) {
        categoryName?.let {
            sharedBookViewModel.getBooksFromFirestore(it)
                .also { books -> sharedBookViewModel.setGenreNameList(books) }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopBars(
                heading = categoryName ?: "Category",
                navController = navController
            )
        }
    ) { paddingValues ->
        BookGridLayout(
            books = bookList,
            navController = navController,
            modifier = Modifier
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        )
    }
}

@Composable
private fun BookGridLayout(
    books: List<Book>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        books.chunked(2).forEach { rowBooks ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowBooks.forEach { book ->
                    Box(modifier = Modifier.weight(1f)) {
                        Cards(book = book, navController = navController)
                    }
                }

                if (rowBooks.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
