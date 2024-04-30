package com.example.fileselecterapp

//https://us-central1-neat-veld-421803.cloudfunctions.net/TestFunction

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


class Cloud {

    suspend fun sendDataAndWaitForResponse(data: String, url: String, callback: CloudCallback) {
        val response = withContext(Dispatchers.IO) {
            val urlConnection = URL(url).openConnection() as HttpURLConnection

            try {
                with(urlConnection) {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Accept", "application/json")
                    doOutput = true

                    OutputStreamWriter(outputStream).use { writer ->
                        writer.write(data)
                        writer.flush()
                    }

                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        val response = StringBuilder()
                        var responseLine: String?
                        while (reader.readLine().also { responseLine = it } != null) {
                            response.append(responseLine!!.trim())
                        }
                        response.toString()
                    }
                }
            } finally {
                urlConnection.disconnect()
            }
        }
        withContext(Dispatchers.Main) {
            callback.onResponse(response)
        }
    }

    suspend fun SendingTest(data: String, url: String, callback: CloudCallback) {
        val response = withContext(Dispatchers.IO) {
            val urlConnection = URL(url).openConnection() as HttpURLConnection

            try {
                with(urlConnection) {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Accept", "application/json")
                    doOutput = true

                    OutputStreamWriter(outputStream).use { writer ->
                        writer.write(data)
                        writer.flush()
                    }

                    val response = StringBuilder()
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        var responseLine: String?
                        while (reader.readLine().also { responseLine = it } != null) {
                            response.append(responseLine!!.trim())
                        }
                        response.toString()
                    }

                    val data2 = "Test2time"

                    OutputStreamWriter(outputStream).use { writer ->
                        writer.write(data2)
                        writer.flush()
                    }

                    val response2 = StringBuilder()
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        var responseLine: String?
                        while (reader.readLine().also { responseLine = it } != null) {
                            response2.append(responseLine!!.trim())
                        }
                        response2.toString()
                    }

                }
            } finally {
                urlConnection.disconnect()
            }
        }
        withContext(Dispatchers.Main) {
            callback.onResponse(response)
        }
    }
}

