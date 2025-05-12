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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookster.ui_components.TopBars
import com.example.bookster.viewmodels.BookPdfReaderViewModel
import java.io.File
import kotlin.math.abs

@Composable
fun BookPDFReaderScreen(
    pdfFile: File,
    navController: NavController,
    bookPdfReaderViewModel: BookPdfReaderViewModel
) {
    val currentBitmap = bookPdfReaderViewModel.currentBitmap
    val isLoading = bookPdfReaderViewModel.isLoading
    val scaleFactor = bookPdfReaderViewModel.scaleFactor
    val currentPage = bookPdfReaderViewModel.currentPage
    val totalPages = bookPdfReaderViewModel.totalPages
    val keyboardController = LocalSoftwareKeyboardController.current

    // New state to track page change gestures
    val pageChangeThreshold = remember { mutableFloatStateOf(0f) }
    val pageChangeLocked = remember { mutableStateOf(false) }

    LaunchedEffect(pdfFile) {
        bookPdfReaderViewModel.openPdf(pdfFile)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    keyboardController?.hide()
                })
            }
    ) {
        Scaffold(topBar = {
            TopBars(heading = pdfFile.name.removeSuffix(".pdf"), navController = navController)
        }) { paddingValues ->
            if (isLoading.value) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        currentBitmap.value?.let { bitmap ->
                            val screenWidthPx =
                                with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
                            val screenHeightPx =
                                with(LocalDensity.current) { LocalConfiguration.current.screenHeightDp.dp.toPx() }

                            val bitmapAspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
                            val screenAspectRatio = screenWidthPx / screenHeightPx

                            val minScale = if (screenAspectRatio > bitmapAspectRatio) {
                                screenHeightPx / bitmap.height
                            } else {
                                screenWidthPx / bitmap.width
                            }

                            val offsetX = remember { mutableFloatStateOf(0f) }
                            val offsetY = remember { mutableFloatStateOf(0f) }

                            LaunchedEffect(bitmap) {
                                scaleFactor.floatValue = minScale
                                offsetX.floatValue = 0f
                                offsetY.floatValue = 0f
                                pageChangeThreshold.floatValue = 0f
                                pageChangeLocked.value = false
                            }

                            Image(
                                painter = BitmapPainter(bitmap.asImageBitmap()),
                                contentDescription = "PDF Page",
                                modifier = Modifier
                                    .padding(8.dp)
                                    .graphicsLayer(
                                        scaleX = scaleFactor.floatValue,
                                        scaleY = scaleFactor.floatValue,
                                        translationX = offsetX.floatValue,
                                        translationY = offsetY.floatValue
                                    )
                                    .pointerInput(Unit) {
                                        detectTransformGestures { _, pan, zoom, _ ->
                                            val newScale =
                                                (scaleFactor.floatValue * zoom).coerceAtLeast(
                                                    minScale
                                                )
                                            val scrollSensitivity =
                                                (newScale / scaleFactor.floatValue)

                                            if (newScale > minScale) {
                                                pageChangeLocked.value = true

                                                offsetX.floatValue += pan.x * scrollSensitivity
                                                offsetY.floatValue += pan.y * scrollSensitivity
                                            } else {
                                                offsetX.floatValue = 0f
                                                offsetY.floatValue = 0f

                                                if (!pageChangeLocked.value) {
                                                    pageChangeThreshold.floatValue += pan.x

                                                    val changePageThreshold = screenWidthPx * 0.2f
                                                    if (abs(pageChangeThreshold.floatValue) >= changePageThreshold) {
                                                        if (pageChangeThreshold.floatValue < 0) {
                                                            bookPdfReaderViewModel.nextPage()
                                                        } else {
                                                            bookPdfReaderViewModel.previousPage()
                                                        }
                                                        pageChangeThreshold.floatValue = 0f
                                                    }
                                                }
                                            }

                                            scaleFactor.floatValue = newScale

                                            if (newScale == minScale) {
                                                pageChangeLocked.value = false
                                            }
                                        }
                                    }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { bookPdfReaderViewModel.previousPage() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous Page",
                                tint = Color.Black
                            )
                        }

                        BasicTextField(
                            value = (currentPage.intValue + 1).toString(),
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let {
                                    val zeroBasedPage = it - 1
                                    if (zeroBasedPage in 0 until totalPages.intValue) {
                                        bookPdfReaderViewModel.loadPage(zeroBasedPage)
                                    }
                                }
                            },
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    bookPdfReaderViewModel.loadPage(currentPage.intValue)
                                }
                            ),
                            modifier = Modifier
                                .padding(8.dp)
                                .width(30.dp),
                            textStyle = TextStyle(
                                color = Color.Black,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            ),
                            singleLine = true
                        )

                        IconButton(onClick = { bookPdfReaderViewModel.nextPage() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Page",
                                tint = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}