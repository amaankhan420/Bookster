package com.example.bookster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.bookster.app_screens.MainNavigationScreen
import com.example.bookster.ui.theme.BooksterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BooksterTheme {
                MainNavigationScreen(this)
            }
        }
    }
}