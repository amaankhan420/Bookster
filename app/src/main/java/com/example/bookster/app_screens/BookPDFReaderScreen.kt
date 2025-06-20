package com.example.bookster.app_screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookster.ui_components.TopBars
import com.example.bookster.viewmodels.BookPdfReaderViewModel
import java.io.File
import kotlin.math.abs
import kotlin.math.max

@Composable
fun BookPDFReaderScreen(
    pdfFile: File,
    navController: NavController,
    bookPdfReaderViewModel: BookPdfReaderViewModel
) {
    val currentBitmap = bookPdfReaderViewModel.currentBitmap
    val isLoading = bookPdfReaderViewModel.isLoading
    val scaleFactor = remember { mutableFloatStateOf(1f) }
    val offsetX = remember { mutableFloatStateOf(0f) }
    val offsetY = remember { mutableFloatStateOf(0f) }

    val currentPage = bookPdfReaderViewModel.currentPage
    val totalPages = bookPdfReaderViewModel.totalPages

    // Track if a page change is in progress to avoid multiple triggers per swipe
    var isPageChanging by remember { mutableStateOf(false) }

    // For keyboard dismissal
    val focusManager = LocalFocusManager.current

    // For TextField state
    var textFieldValue by remember { mutableStateOf((currentPage.intValue + 1).toString()) }

    // Update textFieldValue if currentPage changes from outside
    LaunchedEffect(currentPage.intValue) {
        textFieldValue = (currentPage.intValue + 1).toString()
    }

    LaunchedEffect(pdfFile) {
        bookPdfReaderViewModel.openPdf(pdfFile)
    }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidthPx = with(LocalDensity.current) { screenWidth.toPx() }
    val screenHeightPx = with(LocalDensity.current) { screenHeight.toPx() }

    Scaffold(topBar = {
        TopBars(heading = pdfFile.name.removeSuffix(".pdf"), navController = navController)
    }) { paddingValues ->
        if (isLoading.value) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues), Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
                    // Dismiss keyboard on tap outside
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { focusManager.clearFocus() })
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                val bitmap = currentBitmap.value ?: return@detectTransformGestures

                                val defaultScale = screenWidthPx / bitmap.width
                                val newScale = (scaleFactor.floatValue * zoom).coerceIn(
                                    defaultScale,
                                    defaultScale * 4
                                )

                                val scaledWidth = bitmap.width * newScale
                                val scaledHeight = bitmap.height * newScale

                                val maxOffsetX = max(0f, (scaledWidth - screenWidthPx) / 2)
                                val maxOffsetY = max(0f, (scaledHeight - screenHeightPx) / 2)

                                // Only change page if not already changing and swipe is strong enough
                                if (abs(pan.x) > 50 && newScale == defaultScale && !isPageChanging) {
                                    isPageChanging = true
                                    if (pan.x > 0) bookPdfReaderViewModel.previousPage()
                                    else bookPdfReaderViewModel.nextPage()
                                    offsetX.floatValue = 0f
                                    offsetY.floatValue = 0f
                                }

                                if (newScale > defaultScale) {
                                    offsetX.floatValue = (offsetX.floatValue + pan.x).coerceIn(
                                        -maxOffsetX,
                                        maxOffsetX
                                    )
                                    offsetY.floatValue = (offsetY.floatValue + pan.y).coerceIn(
                                        -maxOffsetY,
                                        maxOffsetY
                                    )
                                } else {
                                    offsetX.floatValue = 0f
                                    offsetY.floatValue = 0f
                                }
                                scaleFactor.floatValue = newScale
                            }
                        }
                        // Reset isPageChanging when gesture ends
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    if (event.changes.all { it.changedToUp() }) {
                                        isPageChanging = false
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    currentBitmap.value?.let { bitmap ->
                        val defaultScale = screenWidthPx / bitmap.width
                        if (scaleFactor.floatValue < defaultScale) scaleFactor.floatValue = defaultScale

                        Image(
                            painter = BitmapPainter(bitmap.asImageBitmap()),
                            contentDescription = null,
                            modifier = Modifier
                                .graphicsLayer(
                                    scaleX = scaleFactor.floatValue,
                                    scaleY = scaleFactor.floatValue,
                                    translationX = offsetX.floatValue,
                                    translationY = offsetY.floatValue
                                )
                                .width(screenWidth)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        bookPdfReaderViewModel.previousPage()
                        offsetX.floatValue = 0f
                        offsetY.floatValue = 0f
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Page",
                            tint = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BasicTextField(
                            value = textFieldValue,
                            onValueChange = {
                                // Reset to 1 if empty
                                if (it.isEmpty()) {
                                    textFieldValue = "1"
                                    bookPdfReaderViewModel.loadPage(0)
                                    offsetX.floatValue = 0f
                                    offsetY.floatValue = 0f
                                } else {
                                    val pageNumber = it.toIntOrNull()
                                    if (pageNumber != null && pageNumber in 1..totalPages.intValue) {
                                        textFieldValue = it
                                        bookPdfReaderViewModel.loadPage(pageNumber - 1)
                                        offsetX.floatValue = 0f
                                        offsetY.floatValue = 0f
                                    } else {
                                        // Ignore invalid input, do not update textFieldValue
                                    }
                                }
                            },
                            textStyle = TextStyle(
                                color = Color.Black,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.width(40.dp),
                            singleLine = true
                        )

                        Text(
                            text = "/ ${totalPages.intValue}",
                            style = TextStyle(color = Color.Black, fontSize = 16.sp),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = {
                        bookPdfReaderViewModel.nextPage()
                        offsetX.floatValue = 0f
                        offsetY.floatValue = 0f
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Page",
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    }
}
