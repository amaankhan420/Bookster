package com.example.bookster.utils.functions

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.example.bookster.R
import com.example.bookster.data.DownloadPrefsManager
import java.io.File
import java.io.InputStream
import java.net.URL

class DownloadBookPDF {
    companion object {
        @RequiresApi(Build.VERSION_CODES.Q)
        suspend fun downloadPdfFromFirebaseSync(
            context: Context,
            url: String,
            fileName: String
        ): File? {
            return try {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                    put(
                        MediaStore.Downloads.RELATIVE_PATH,
                        "${Environment.DIRECTORY_DOWNLOADS}/${context.getString(R.string.bookster_directory)}"
                    )
                }

                val collection =
                    MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                val itemUri = resolver.insert(collection, contentValues)

                if (itemUri != null) {
                    resolver.openOutputStream(itemUri).use { outputStream ->
                        val inputStream: InputStream = URL(url).openStream()
                        inputStream.use { input ->
                            outputStream?.use { output ->
                                input.copyTo(output)
                            }
                        }
                    }

                    contentValues.clear()
                    contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                    resolver.update(itemUri, contentValues, null, null)

                    DownloadPrefsManager.addDownloadedBook(context, fileName)
                    findPdfByName(context, fileName.removeSuffix(".pdf"))
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun loadPdfs(context: Context): List<File> {
            try {
                val directory = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    context.getString(R.string.bookster_directory)
                )

                return if (directory.exists() && directory.isDirectory) {
                    directory.listFiles()?.filter { it.extension == "pdf" } ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                return emptyList()
            }
        }

        fun deletePdf(file: File): Boolean {
            return try {
                file.delete()
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun findPdfByName(context: Context, fileName: String): File? {
            try {
                val normalizedFileName =
                    if (fileName.endsWith(".pdf")) fileName else "$fileName.pdf"
                val directory = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    context.getString(R.string.bookster_directory)
                )

                if (directory.exists() && directory.isDirectory) {
                    return directory.listFiles()?.find {
                        it.name.equals(normalizedFileName, ignoreCase = true)
                    }
                }
                return null
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }
}