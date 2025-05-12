package com.example.bookster.ui_components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.bookster.states.Book
import com.example.bookster.utils.Routes

@Composable
fun Cards(book: Book, navController: NavController) {
    ElevatedCard(
        modifier = Modifier
            .size(width = 180.dp, height = 220.dp)
            .padding(horizontal = 8.dp)
            .clickable {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    key = "book", value = book
                )
                navController.navigate(Routes.Book.route)
            },
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ) {
        Box {
            Image(
                painter = rememberAsyncImagePainter(book.coverUrl),
                contentDescription = "Book cover",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black), startY = 100f
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                book.title?.let {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        color = White,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}