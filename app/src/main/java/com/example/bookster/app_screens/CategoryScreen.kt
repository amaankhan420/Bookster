package com.example.bookster.app_screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bookster.ui_components.Cards
import com.example.bookster.ui_components.TopBars
import com.example.bookster.sharedviewmodel.SharedBookViewModel

@Composable
fun CategoryScreen(
    sharedBookViewModel: SharedBookViewModel,
    navController: NavController
) {
    val bookList = sharedBookViewModel.booksList.value

    Scaffold (
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            bookList[0].genre.let {
                if (it != null) {
                    TopBars(heading = it, navController = navController)
                }
            }
        }
    ) { values ->
        Column(
            modifier = Modifier
                .padding(values)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Column (
                modifier = Modifier
                    .padding(vertical = 8.dp),
            ){
                var index = 0
                while (index < bookList.size) {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ){
                        if (index < bookList.size) {
                            Cards(book = bookList[index], navController = navController)
                        }

                        if (index + 1 < bookList.size) {
                            Cards(book = bookList[index + 1], navController = navController)
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    index += 2
                }
            }
        }
    }
}