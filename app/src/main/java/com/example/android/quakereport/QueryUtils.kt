package com.example.android.quakereport

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset

import java.util.ArrayList

object QueryUtils {
    private val LOG_TAG = QueryUtils::class.java.simpleName
    fun extractEarthquakes(json: String): List<Earthquake> {
        try {
            val base = JSONObject(json)
            val arr = base.getJSONArray("features")
            return (0..arr.length() - 1).map {
                val obj = arr.getJSONObject(it).getJSONObject("properties")
                Earthquake(
                    obj.getDouble("mag"),
                    obj.getString("place"),
                    obj.getLong("time"),
                    obj.getString("url")
                )
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            return ArrayList()
        }
    }

    private fun makeHttpRequest(url: URL): String {
        var connection: HttpURLConnection? = null
        var stream: InputStream? = null

        try {
            connection = url.openConnection() as HttpURLConnection
            connection.readTimeout = 10_000  // milliseconds
            connection.connectTimeout = 15_000
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                stream = connection.inputStream
                val streamReader = InputStreamReader(stream, Charset.forName("UTF-8"))
                return streamReader.readText()
            } else {
                Log.e(LOG_TAG, "Error response code: " + connection.responseCode)
                return ""
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e)
            return ""
        } finally {
            if (connection != null) {
                connection.disconnect()
            }
            if (stream != null) {
                stream.close()
            }
        }
    }

    fun fetchEarthquakeData(url: String): List<Earthquake> {
        val realUrl = try {
            URL(url)
        } catch (e: MalformedURLException) {
            Log.e(LOG_TAG, "Problem building the URL ", e)
            return ArrayList()
        }

        val json = try {
            makeHttpRequest(realUrl)
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e)
            return ArrayList()
        }

        return extractEarthquakes(json)
    }
}
