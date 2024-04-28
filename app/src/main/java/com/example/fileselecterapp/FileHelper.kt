package com.example.fileselecterapp

import android.content.Context
import android.net.Uri
import java.io.*

class FileHelper {
    companion object {
        // to read txt file
        fun readFileContent(context: Context, uri: Uri): String {
            val contentResolver = context.contentResolver
            val stringBuilder = StringBuilder()

            contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String? = reader.readLine()
                    while (line != null) {
                        stringBuilder.append(line)
                        line = reader.readLine()
                    }
                }
            }

            return stringBuilder.toString()
        }

        fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
            val inputStream = context.contentResolver.openInputStream(uri)
            return try {
                inputStream?.readBytes()
            } catch (e: IOException) {
                e.printStackTrace()
                null
            } finally {
                inputStream?.close()
            }
        }
    }
}