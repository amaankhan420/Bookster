package com.example.bookster.ui_components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bookster.utils.Routes
import com.example.bookster.viewmodels.sharedviewmodel.SharedBookViewModel
import com.valentinilk.shimmer.shimmer
import kotlin.math.min

@Composable
fun Section(type: String, navController: NavController) {
    val viewModelKey = "viewModel_$type"
    val bookViewModel: SharedBookViewModel = viewModel(key = viewModelKey)

    LaunchedEffect(key1 = type) {
        try {
            val fetchedBooks = bookViewModel.getBooksFromFirestore(type)
            bookViewModel.setGenreNameList(fetchedBooks)
        } catch (e: Exception) {
            Log.e("Section", "Error fetching books from Firestore", e)
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(start = 5.dp),
                text = type,
                fontSize = 20.sp,
            )
            TextButton(
                onClick = {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        key = "category", value = type
                    )
                    navController.navigate(Routes.Category.route)
                }) {
                Text(
                    text = "More", style = TextStyle(
                        textDecoration = TextDecoration.Underline, fontStyle = FontStyle.Italic
                    ), color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(top = 3.dp, bottom = 5.dp, start = 5.dp, end = 5.dp)
                .horizontalScroll(rememberScrollState())
                .fillMaxWidth()
                .height(240.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (bookViewModel.booksList.value.isNotEmpty()) {
                for (i in 0 until min(8, bookViewModel.booksList.value.size)) {
                    Cards(book = bookViewModel.booksList.value[i], navController = navController)
                }
            } else {
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(width = 180.dp, height = 220.dp)
                            .shimmer()
                            .background(
                                color = Color.LightGray,
                                shape = RoundedCornerShape(15.dp)
                            )
                    )
                }
            }
        }
    }
}